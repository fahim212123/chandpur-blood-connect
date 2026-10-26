package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.model.BloodGroup
import com.example.model.Donor
import com.example.model.DonorDate
import com.example.model.Upazila
import com.example.model.VerificationStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class DonorRepository(private val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in before performing this action.")
  }

  // 1. Observe verified donors for public search directory (Query Alignment with Security Rules)
  fun observeVerifiedDonors(): Flow<List<Donor>> {
    val path = "donors"
    return db.collection(path)
      .whereEqualTo("verificationStatus", "VERIFIED")
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(FirestoreDonor::class.java).map { it.toDomain() }
      }
      .catch { exception ->
        if (exception is Exception) {
          handleFirestoreError(exception, OperationType.LIST, path)
        }
        throw exception
      }
  }

  // 2. Fetch private contact details (Only accessible if owner or admin per security rules)
  suspend fun getDonorContactPhone(donorId: String): Result<String?> {
    val path = "donors/$donorId/private/contact"
    return try {
      val snapshot = db.collection("donors")
        .document(donorId)
        .collection("private")
        .document("contact")
        .get()
        .await()

      val privateDoc = snapshot.toObject(FirestoreDonorPrivate::class.java)
      Result.success(privateDoc?.phone)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, path)
      Result.failure(e)
    }
  }

  // 3. Submit donor registration (Enforces status = PENDING on write)
  suspend fun submitRegistration(
    name: String,
    phone: String,
    bloodGroup: BloodGroup,
    upazila: Upazila,
    area: String,
    isFirstTime: Boolean,
    lastDonationDate: DonorDate?,
    emergencyPhone: String?,
    notes: String?
  ): Result<String> {
    val uid = requireUserId()
    val collectionRef = db.collection("donor_registrations")
    val formattedDate = lastDonationDate?.let { "${it.year}-${it.month.toString().padStart(2, '0')}-${it.day.toString().padStart(2, '0')}" }

    val payload = mutableMapOf<String, Any>(
      "userId" to uid,
      "name" to name.trim(),
      "phone" to phone.trim(),
      "bloodGroup" to bloodGroup.label,
      "upazila" to upazila.enName,
      "area" to area.trim(),
      "isFirstTimeDonor" to isFirstTime,
      "verificationStatus" to "PENDING",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    formattedDate?.let { payload["lastDonationDate"] = it }
    emergencyPhone?.takeIf { it.isNotBlank() }?.let { payload["emergencyContact"] = it.trim() }
    notes?.takeIf { it.isNotBlank() }?.let { payload["notes"] = it.trim() }

    return try {
      val docRef = collectionRef.add(payload).await()
      Result.success(docRef.id)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, collectionRef.path)
      Result.failure(e)
    }
  }

  // 4. Update donor's last donation date & location
  suspend fun updateDonationDate(
    donorId: String,
    newDate: DonorDate,
    hospital: String?,
    newTotalDonations: Int
  ): Result<Unit> {
    requireUserId()
    val docRef = db.collection("donors").document(donorId)
    val dateStr = "${newDate.year}-${newDate.month.toString().padStart(2, '0')}-${newDate.day.toString().padStart(2, '0')}"

    val updates = mutableMapOf<String, Any>(
      "lastDonationDate" to dateStr,
      "totalDonations" to newTotalDonations,
      "isAvailableManually" to true,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    hospital?.takeIf { it.isNotBlank() }?.let { updates["lastDonationLocation"] = it.trim() }

    return try {
      docRef.update(updates).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  // 5. Update manual availability toggle
  suspend fun updateManualAvailability(donorId: String, isAvailable: Boolean): Result<Unit> {
    requireUserId()
    val docRef = db.collection("donors").document(donorId)
    return try {
      docRef.update(
        mapOf(
          "isAvailableManually" to isAvailable,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  // 6. Admin: Observe pending registrations
  fun observePendingRegistrations(): Flow<List<Donor>> {
    val path = "donor_registrations"
    return db.collection(path)
      .whereEqualTo("verificationStatus", "PENDING")
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(FirestoreRegistration::class.java).map { it.toDomain() }
      }
      .catch { exception ->
        if (exception is Exception) {
          handleFirestoreError(exception, OperationType.LIST, path)
        }
        throw exception
      }
  }

  // 7. Admin: Update registration status
  suspend fun moderateRegistrationStatus(registrationId: String, newStatus: VerificationStatus): Result<Unit> {
    requireUserId()
    val docRef = db.collection("donor_registrations").document(registrationId)
    return try {
      docRef.update(
        mapOf(
          "verificationStatus" to newStatus.name,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }
}
