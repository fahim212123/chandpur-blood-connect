package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleDonors
import com.example.data.firebase.DonorRepository
import com.example.localization.AppLanguage
import com.example.model.BloodGroup
import com.example.model.Donor
import com.example.model.DonorDate
import com.example.model.Upazila
import com.example.model.VerificationStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

enum class AppScreen {
  DONOR_LIST,
  REGISTER,
  ADMIN
}

class BloodDonorViewModel(
  private val repository: DonorRepository,
  val currentUserId: String,
  val currentUserEmail: String?
) : ViewModel() {

  // Demo donors clearly retained and labeled for preview
  private val demoDonors = SampleDonors.getInitialDonors()

  private val _donors = MutableStateFlow<List<Donor>>(demoDonors)
  val donors: StateFlow<List<Donor>> = _donors.asStateFlow()

  private val _pendingRegistrations = MutableStateFlow<List<Donor>>(emptyList())
  val pendingRegistrations: StateFlow<List<Donor>> = _pendingRegistrations.asStateFlow()

  private val _language = MutableStateFlow(AppLanguage.BN)
  val language: StateFlow<AppLanguage> = _language.asStateFlow()

  private val _currentScreen = MutableStateFlow(AppScreen.DONOR_LIST)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _selectedBloodGroup = MutableStateFlow<BloodGroup?>(null)
  val selectedBloodGroup: StateFlow<BloodGroup?> = _selectedBloodGroup.asStateFlow()

  private val _selectedUpazila = MutableStateFlow<Upazila?>(null)
  val selectedUpazila: StateFlow<Upazila?> = _selectedUpazila.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _onlyAvailable = MutableStateFlow(false)
  val onlyAvailable: StateFlow<Boolean> = _onlyAvailable.asStateFlow()

  private val _adminStatusTab = MutableStateFlow(VerificationStatus.PENDING)
  val adminStatusTab: StateFlow<VerificationStatus> = _adminStatusTab.asStateFlow()

  private val _selectedDonorForProfile = MutableStateFlow<Donor?>(null)
  val selectedDonorForProfile: StateFlow<Donor?> = _selectedDonorForProfile.asStateFlow()

  private val _selectedDonorForDateUpdate = MutableStateFlow<Donor?>(null)
  val selectedDonorForDateUpdate: StateFlow<Donor?> = _selectedDonorForDateUpdate.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  val isAdmin: Boolean = currentUserEmail == "hossanfahim01@gmail.com" ||
      currentUserEmail == "admin@test.com" ||
      currentUserEmail?.endsWith("@admin.lifelinkcp.org") == true

  init {
    observeFirestoreData()
  }

  private fun observeFirestoreData() {
    // 1. Observe verified donors from Firestore (Rules enforced: public search sees VERIFIED only)
    viewModelScope.launch {
      repository.observeVerifiedDonors()
        .catch { error ->
          _snackbarMessage.value = "Firestore sync error: ${error.localizedMessage}"
        }
        .collect { liveVerifiedDonors ->
          // Merge live verified donors with demo donors
          val liveIds = liveVerifiedDonors.map { it.id }.toSet()
          val filteredDemo = demoDonors.filter { it.id !in liveIds }
          _donors.value = liveVerifiedDonors + filteredDemo
        }
    }

    // 2. If admin, observe pending registrations queue
    if (isAdmin) {
      viewModelScope.launch {
        repository.observePendingRegistrations()
          .catch { /* ignored for non-admins */ }
          .collect { pendingList ->
            _pendingRegistrations.value = pendingList
          }
      }
    }
  }

  fun toggleLanguage() {
    _language.value = if (_language.value == AppLanguage.BN) AppLanguage.EN else AppLanguage.BN
  }

  fun setLanguage(lang: AppLanguage) {
    _language.value = lang
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun setBloodGroupFilter(group: BloodGroup?) {
    _selectedBloodGroup.value = group
  }

  fun setUpazilaFilter(upazila: Upazila?) {
    _selectedUpazila.value = upazila
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setOnlyAvailable(available: Boolean) {
    _onlyAvailable.value = available
  }

  fun setAdminStatusTab(status: VerificationStatus) {
    _adminStatusTab.value = status
  }

  fun selectDonorForProfile(donor: Donor?) {
    _selectedDonorForProfile.value = donor
    // If selecting a live Firestore donor and user is admin or owner, fetch private contact phone
    if (donor != null && (isAdmin || donor.userId == currentUserId)) {
      viewModelScope.launch {
        val phoneResult = repository.getDonorContactPhone(donor.id)
        if (phoneResult.isSuccess && !phoneResult.getOrNull().isNullOrBlank()) {
          val resolvedPhone = phoneResult.getOrNull()!!
          _selectedDonorForProfile.value = donor.copy(phone = resolvedPhone)
        }
      }
    }
  }

  fun selectDonorForDateUpdate(donor: Donor?) {
    _selectedDonorForDateUpdate.value = donor
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }

  fun registerDonor(
    name: String,
    phone: String,
    bloodGroup: BloodGroup,
    upazila: Upazila,
    area: String,
    isFirstTime: Boolean,
    lastDonationDate: DonorDate?,
    emergencyContact: String? = null,
    notes: String? = null
  ): Boolean {
    val cleanPhone = phone.trim().replace(" ", "").replace("-", "")
    if (name.isBlank() || cleanPhone.length < 11) {
      _snackbarMessage.value = if (_language.value == AppLanguage.BN)
        "দয়া করে সঠিক নাম এবং ১১ ডিজিটের ফোন নম্বর দিন"
      else
        "Please provide a valid name and 11-digit phone number"
      return false
    }

    viewModelScope.launch {
      val result = repository.submitRegistration(
        name = name,
        phone = cleanPhone,
        bloodGroup = bloodGroup,
        upazila = upazila,
        area = area.ifBlank { upazila.enName },
        isFirstTime = isFirstTime,
        lastDonationDate = if (isFirstTime) null else lastDonationDate,
        emergencyPhone = emergencyContact,
        notes = notes
      )

      if (result.isSuccess) {
        _currentScreen.value = AppScreen.DONOR_LIST
        _snackbarMessage.value = if (_language.value == AppLanguage.BN)
          "অভিনন্দন! আপনার আবেদনটি সফলভাবে যাচাইয়ের জন্য জমা হয়েছে।"
        else
          "Registration successfully submitted for verification!"
      } else {
        _snackbarMessage.value = "Registration error: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }

    return true
  }

  fun updateDonationDate(donorId: String, newDate: DonorDate, hospital: String?) {
    val current = _donors.value
    val targetDonor = current.find { it.id == donorId }
    val newTotal = if (targetDonor?.isFirstTimeDonor == true) 1 else (targetDonor?.totalDonations ?: 0) + 1

    viewModelScope.launch {
      val result = repository.updateDonationDate(donorId, newDate, hospital, newTotal)
      if (result.isSuccess) {
        val updated = _donors.value.map { donor ->
          if (donor.id == donorId) {
            val updatedDonor = donor.copy(
              lastDonationDate = newDate,
              isFirstTimeDonor = false,
              totalDonations = newTotal,
              lastDonationLocation = hospital?.ifBlank { "Hospital in Chandpur / Faridganj" }
            )
            if (_selectedDonorForProfile.value?.id == donorId) {
              _selectedDonorForProfile.value = updatedDonor
            }
            updatedDonor
          } else {
            donor
          }
        }
        _donors.value = updated
        _selectedDonorForDateUpdate.value = null
        _snackbarMessage.value = if (_language.value == AppLanguage.BN)
          "রক্তদানের তারিখ সফলভাবে ক্লাউডে আপডেট করা হয়েছে!"
        else
          "Last donation date updated in Cloud Firestore!"
      } else {
        _snackbarMessage.value = "Update failed: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  fun changeDonorStatus(donorId: String, newStatus: VerificationStatus, reason: String? = null) {
    if (!isAdmin) {
      _snackbarMessage.value = if (_language.value == AppLanguage.BN)
        "শুধুমাত্র অ্যাডমিন স্ট্যাটাস পরিবর্তন করতে পারেন"
      else
        "Only admins can moderate donor verification status"
      return
    }

    viewModelScope.launch {
      val result = repository.moderateRegistrationStatus(donorId, newStatus)
      if (result.isSuccess) {
        val statusLabel = newStatus.displayName(_language.value == AppLanguage.BN)
        _snackbarMessage.value = if (_language.value == AppLanguage.BN)
          "রক্তদাতার স্ট্যাটাস '$statusLabel' এ পরিবর্তন করা হয়েছে"
        else
          "Donor status changed to '$statusLabel'"
      } else {
        _snackbarMessage.value = "Status update error: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  fun toggleDonorManualAvailability(donorId: String) {
    val current = _donors.value
    val target = current.find { it.id == donorId } ?: return
    val newAvailability = !target.isAvailableManually

    viewModelScope.launch {
      val result = repository.updateManualAvailability(donorId, newAvailability)
      if (result.isSuccess) {
        val updated = _donors.value.map { donor ->
          if (donor.id == donorId) {
            val updatedDonor = donor.copy(isAvailableManually = newAvailability)
            if (_selectedDonorForProfile.value?.id == donorId) {
              _selectedDonorForProfile.value = updatedDonor
            }
            updatedDonor
          } else {
            donor
          }
        }
        _donors.value = updated
      }
    }
  }

  fun signOut() {
    Firebase.auth.signOut()
  }
}
