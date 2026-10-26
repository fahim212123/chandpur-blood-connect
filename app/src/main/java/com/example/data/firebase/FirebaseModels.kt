package com.example.data.firebase

import com.example.model.BloodGroup
import com.example.model.Donor
import com.example.model.DonorDate
import com.example.model.Upazila
import com.example.model.VerificationStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreDonor(
  @DocumentId val id: String = "",
  val userId: String = "",
  val name: String = "",
  val nameBn: String? = null,
  val bloodGroup: String = "O+",
  val upazila: String = "Chandpur Sadar",
  val area: String = "",
  val areaBn: String? = null,
  val isFirstTimeDonor: Boolean = false,
  val lastDonationDate: String? = null,
  val lastDonationLocation: String? = null,
  val totalDonations: Long = 0L,
  val verificationStatus: String = "VERIFIED",
  val isAvailableManually: Boolean = true,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  fun toDomain(phone: String? = null, emergencyContact: String? = null, notes: String? = null): Donor {
    val bGroup = BloodGroup.fromLabel(bloodGroup) ?: BloodGroup.O_POS
    val upazilaEnum = if (upazila.equals("Faridganj", ignoreCase = true)) Upazila.FARIDGANJ else Upazila.CHANDPUR_SADAR
    val vStatus = when (verificationStatus.uppercase()) {
      "VERIFIED" -> VerificationStatus.VERIFIED
      "PENDING" -> VerificationStatus.PENDING
      "REJECTED" -> VerificationStatus.REJECTED
      "SUSPENDED" -> VerificationStatus.SUSPENDED
      else -> VerificationStatus.VERIFIED
    }

    val parsedDate = lastDonationDate?.let { dateStr ->
      val parts = dateStr.split("-")
      if (parts.size == 3) {
        val y = parts[0].toIntOrNull() ?: 2026
        val m = parts[1].toIntOrNull() ?: 1
        val d = parts[2].toIntOrNull() ?: 1
        DonorDate(y, m, d)
      } else null
    }

    return Donor(
      id = id,
      name = name,
      nameBn = nameBn,
      phone = phone ?: "",
      bloodGroup = bGroup,
      upazila = upazilaEnum,
      area = area,
      areaBn = areaBn,
      isFirstTimeDonor = isFirstTimeDonor,
      lastDonationDate = parsedDate,
      totalDonations = totalDonations.toInt(),
      verificationStatus = vStatus,
      isAvailableManually = isAvailableManually,
      lastDonationLocation = lastDonationLocation,
      emergencyContact = emergencyContact,
      notes = notes
    )
  }
}

@IgnoreExtraProperties
data class FirestoreDonorPrivate(
  @DocumentId val id: String = "",
  val userId: String = "",
  val phone: String = "",
  val emergencyContact: String? = null,
  val notes: String? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreRegistration(
  @DocumentId val id: String = "",
  val userId: String = "",
  val name: String = "",
  val phone: String = "",
  val bloodGroup: String = "O+",
  val upazila: String = "Chandpur Sadar",
  val area: String = "",
  val isFirstTimeDonor: Boolean = false,
  val lastDonationDate: String? = null,
  val emergencyContact: String? = null,
  val notes: String? = null,
  val verificationStatus: String = "PENDING",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  fun toDomain(): Donor {
    val bGroup = BloodGroup.fromLabel(bloodGroup) ?: BloodGroup.O_POS
    val upazilaEnum = if (upazila.equals("Faridganj", ignoreCase = true)) Upazila.FARIDGANJ else Upazila.CHANDPUR_SADAR
    val vStatus = when (verificationStatus.uppercase()) {
      "VERIFIED" -> VerificationStatus.VERIFIED
      "PENDING" -> VerificationStatus.PENDING
      "REJECTED" -> VerificationStatus.REJECTED
      "SUSPENDED" -> VerificationStatus.SUSPENDED
      else -> VerificationStatus.PENDING
    }

    val parsedDate = lastDonationDate?.let { dateStr ->
      val parts = dateStr.split("-")
      if (parts.size == 3) {
        val y = parts[0].toIntOrNull() ?: 2026
        val m = parts[1].toIntOrNull() ?: 1
        val d = parts[2].toIntOrNull() ?: 1
        DonorDate(y, m, d)
      } else null
    }

    return Donor(
      id = id,
      name = name,
      phone = phone,
      bloodGroup = bGroup,
      upazila = upazilaEnum,
      area = area,
      isFirstTimeDonor = isFirstTimeDonor,
      lastDonationDate = parsedDate,
      totalDonations = if (isFirstTimeDonor) 0 else 1,
      verificationStatus = vStatus,
      isAvailableManually = true,
      emergencyContact = emergencyContact,
      notes = notes
    )
  }
}
