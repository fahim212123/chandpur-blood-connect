package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.firebase.DonorRepository
import com.example.localization.AppLanguage
import com.example.model.VerificationStatus
import com.example.ui.components.BottomNavBar
import com.example.ui.components.HeaderBar
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.DonorListScreen
import com.example.ui.screens.DonorProfileDialog
import com.example.ui.screens.RegisterDonorScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.UpdateDonationDateDialog
import com.example.ui.theme.BloodDonationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.BloodDonorViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
  val listener = FirebaseAuth.AuthStateListener { auth ->
    trySend(auth.currentUser)
  }
  addAuthStateListener(listener)
  awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BloodDonationTheme {
        AppRoot()
      }
    }
  }
}

@Composable
fun AppRoot(auth: FirebaseAuth = Firebase.auth) {
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  var authLanguage by remember { mutableStateOf(AppLanguage.BN) }

  val user = currentUser
  if (user == null) {
    // Unauthenticated: Render Sign-In Gate (No Firestore queries triggered)
    SignInScreen(
      currentLanguage = authLanguage,
      onToggleLanguage = {
        authLanguage = if (authLanguage == AppLanguage.BN) AppLanguage.EN else AppLanguage.BN
      }
    )
  } else {
    // Authenticated: Initialize ViewModel with provisioned database ID
    val viewModel: BloodDonorViewModel = viewModel(
      key = user.uid,
      factory = viewModelFactory {
        initializer {
          val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
          val databaseId = app.getString(R.string.firestore_database_id)
          val db = FirebaseFirestore.getInstance(databaseId)
          BloodDonorViewModel(DonorRepository(db), user.uid, user.email)
        }
      }
    )

    BloodDonationApp(viewModel = viewModel, currentUserEmail = user.email)
  }
}

@Composable
fun BloodDonationApp(
  viewModel: BloodDonorViewModel,
  currentUserEmail: String?
) {
  val donors by viewModel.donors.collectAsState()
  val pendingRegistrations by viewModel.pendingRegistrations.collectAsState()
  val currentLanguage by viewModel.language.collectAsState()
  val currentScreen by viewModel.currentScreen.collectAsState()
  val selectedBloodGroup by viewModel.selectedBloodGroup.collectAsState()
  val selectedUpazila by viewModel.selectedUpazila.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val onlyAvailable by viewModel.onlyAvailable.collectAsState()
  val adminStatusTab by viewModel.adminStatusTab.collectAsState()
  val selectedDonorForProfile by viewModel.selectedDonorForProfile.collectAsState()
  val selectedDonorForDateUpdate by viewModel.selectedDonorForDateUpdate.collectAsState()
  val snackbarMessage by viewModel.snackbarMessage.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearSnackbar()
    }
  }

  val adminMergedDonors by remember(donors, pendingRegistrations) {
    derivedStateOf {
      val pendingIds = pendingRegistrations.map { it.id }.toSet()
      pendingRegistrations + donors.filter { it.id !in pendingIds }
    }
  }

  val pendingCount by remember(adminMergedDonors) {
    derivedStateOf { adminMergedDonors.count { it.verificationStatus == VerificationStatus.PENDING } }
  }

  // Handle system back navigation
  BackHandler(enabled = selectedDonorForProfile != null || selectedDonorForDateUpdate != null || currentScreen != AppScreen.DONOR_LIST) {
    when {
      selectedDonorForDateUpdate != null -> viewModel.selectDonorForDateUpdate(null)
      selectedDonorForProfile != null -> viewModel.selectDonorForProfile(null)
      currentScreen != AppScreen.DONOR_LIST -> viewModel.navigateTo(AppScreen.DONOR_LIST)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      HeaderBar(
        currentLanguage = currentLanguage,
        onToggleLanguage = { viewModel.toggleLanguage() },
        currentUserEmail = currentUserEmail,
        onSignOut = { viewModel.signOut() }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = currentScreen,
        onScreenSelect = { viewModel.navigateTo(it) },
        currentLanguage = currentLanguage,
        pendingCount = pendingCount
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        AppScreen.DONOR_LIST -> {
          DonorListScreen(
            donors = donors,
            selectedBloodGroup = selectedBloodGroup,
            selectedUpazila = selectedUpazila,
            searchQuery = searchQuery,
            onlyAvailable = onlyAvailable,
            currentLanguage = currentLanguage,
            onBloodGroupSelect = { viewModel.setBloodGroupFilter(it) },
            onUpazilaSelect = { viewModel.setUpazilaFilter(it) },
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onToggleOnlyAvailable = { viewModel.setOnlyAvailable(it) },
            onDonorClick = { viewModel.selectDonorForProfile(it) },
            onUpdateDateClick = { viewModel.selectDonorForDateUpdate(it) }
          )
        }

        AppScreen.REGISTER -> {
          RegisterDonorScreen(
            currentLanguage = currentLanguage,
            onRegisterSubmit = { name, phone, bloodGroup, upazila, area, isFirstTime, lastDate, emergencyPhone, notes ->
              viewModel.registerDonor(
                name = name,
                phone = phone,
                bloodGroup = bloodGroup,
                upazila = upazila,
                area = area,
                isFirstTime = isFirstTime,
                lastDonationDate = lastDate,
                emergencyContact = emergencyPhone,
                notes = notes
              )
            }
          )
        }

        AppScreen.ADMIN -> {
          AdminDashboardScreen(
            donors = adminMergedDonors,
            activeTab = adminStatusTab,
            currentLanguage = currentLanguage,
            onTabSelect = { viewModel.setAdminStatusTab(it) },
            onStatusChange = { donorId, newStatus ->
              viewModel.changeDonorStatus(donorId, newStatus)
            },
            onDonorClick = { viewModel.selectDonorForProfile(it) }
          )
        }
      }

      // Dialog: Donor Profile
      selectedDonorForProfile?.let { donor ->
        DonorProfileDialog(
          donor = donor,
          currentLanguage = currentLanguage,
          onDismiss = { viewModel.selectDonorForProfile(null) },
          onUpdateDonationDate = {
            viewModel.selectDonorForDateUpdate(donor)
          },
          onToggleAvailability = {
            viewModel.toggleDonorManualAvailability(donor.id)
          }
        )
      }

      // Dialog: Update Last Donation Date
      selectedDonorForDateUpdate?.let { donor ->
        UpdateDonationDateDialog(
          donor = donor,
          currentLanguage = currentLanguage,
          onDismiss = { viewModel.selectDonorForDateUpdate(null) },
          onSaveDate = { newDate, hospital ->
            viewModel.updateDonationDate(donor.id, newDate, hospital)
          }
        )
      }
    }
  }
}
