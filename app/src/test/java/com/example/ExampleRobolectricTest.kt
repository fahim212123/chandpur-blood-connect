package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BloodGroup
import com.example.model.Donor
import com.example.model.DonorDate
import com.example.model.Upazila
import com.example.model.VerificationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LifeLink CP", appName)
  }

  @Test
  fun `donor eligibility calculation follows 90 days rule`() {
    val today = DonorDate(2026, 10, 25)

    // Donor donated 100 days ago -> Eligible
    val eligibleDonor = Donor(
      id = "d1",
      name = "Test Donor",
      phone = "01700000000",
      bloodGroup = BloodGroup.O_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Puran Bazar",
      lastDonationDate = DonorDate(2026, 7, 10), // ~107 days
      verificationStatus = VerificationStatus.VERIFIED
    )
    assertTrue(eligibleDonor.isEligibleByInterval(today))
    assertTrue(eligibleDonor.isAvailableNow(today))

    // Donor donated 20 days ago -> Not eligible yet
    val restingDonor = eligibleDonor.copy(
      lastDonationDate = DonorDate(2026, 10, 5) // 20 days ago
    )
    assertFalse(restingDonor.isEligibleByInterval(today))
    assertFalse(restingDonor.isAvailableNow(today))
    assertTrue(restingDonor.daysUntilEligible(today) > 0)

    // First-time donor -> always eligible
    val firstTimeDonor = eligibleDonor.copy(
      isFirstTimeDonor = true,
      lastDonationDate = null
    )
    assertTrue(firstTimeDonor.isEligibleByInterval(today))
    assertTrue(firstTimeDonor.isAvailableNow(today))
  }
}
