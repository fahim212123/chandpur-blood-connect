package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firebase.DonorRepository
import com.example.model.BloodGroup
import com.example.model.Upazila
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class DonorRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun submitRegistration_authenticatedUser_createsPendingRegistration() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = DonorRepository(firestore)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.submitRegistration(
        name = "Alice Hossain",
        phone = "01712345678",
        bloodGroup = BloodGroup.O_POS,
        upazila = Upazila.CHANDPUR_SADAR,
        area = "Puran Bazar",
        isFirstTime = true,
        lastDonationDate = null,
        emergencyPhone = null,
        notes = "Ready for test donation"
      )
    }

    assertTrue(result.isSuccess)
    val regId = result.getOrThrow()
    assertTrue(regId.isNotEmpty())

    // Verify registration status is strictly PENDING
    val snapshot = firestore.collection("donor_registrations").document(regId).get().await()
    assertEquals("PENDING", snapshot.getString("verificationStatus"))
  }

  @Test
  fun observeVerifiedDonors_publicSearch_returnsOnlyVerifiedDonors() = runBlocking {
    val adminUid = signInTestUser(ADMIN_EMAIL)

    val verifiedDonorId = "donor_v_${UUID.randomUUID().toString().take(6)}"
    val pendingDonorId = "donor_p_${UUID.randomUUID().toString().take(6)}"

    // Admin seeds verified donor
    firestore.collection("donors").document(verifiedDonorId).set(
      mapOf(
        "userId" to adminUid,
        "name" to "Verified Donor",
        "bloodGroup" to "O+",
        "upazila" to "Chandpur Sadar",
        "area" to "Puran Bazar",
        "verificationStatus" to "VERIFIED",
        "totalDonations" to 2,
        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
        "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
      )
    ).await()

    // Sign in as regular user Bob to observe public verified donors
    signInTestUser(BOB_EMAIL)
    val repository = DonorRepository(firestore)

    // Observe verified donors flow
    val verifiedList = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeVerifiedDonors().first { list -> list.any { it.id == verifiedDonorId } }
    }

    assertTrue(verifiedList.any { it.id == verifiedDonorId })
    assertFalse(verifiedList.any { it.id == pendingDonorId })
  }

  @Test
  fun observeVerifiedDonors_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repository = DonorRepository(firestore)

    var caughtException: Exception? = null
    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repository.observeVerifiedDonors().first()
      }
    } catch (e: Exception) {
      caughtException = e
    }

    assertNotNull("Expected query to fail when unauthenticated", caughtException)
    val firestoreException = checkNotNull(caughtException?.unwrapFirestoreException()) {
      "Expected nested FirebaseFirestoreException, got $caughtException"
    }
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
  }

  @Test
  fun getDonorContact_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)

    // Seed Alice's private contact details
    firestore.collection("donors")
      .document(aliceUid)
      .collection("private")
      .document("contact")
      .set(
        mapOf(
          "userId" to aliceUid,
          "phone" to "01712345678",
          "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
      ).await()

    // Sign in as Bob (different user)
    signInTestUser(BOB_EMAIL)
    val bobRepo = DonorRepository(firestore)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      bobRepo.getDonorContactPhone(aliceUid)
    }

    assertTrue(result.isFailure)
    val exception = checkNotNull(result.exceptionOrNull()?.unwrapFirestoreException()) {
      "Expected nested FirebaseFirestoreException"
    }
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, exception.code)
  }

  private companion object {
    const val ALICE_EMAIL = "alice@test.com"
    const val BOB_EMAIL = "bob@test.com"
    const val ADMIN_EMAIL = "admin@test.com"
    const val DEFAULT_TIMEOUT_MS = 6000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
