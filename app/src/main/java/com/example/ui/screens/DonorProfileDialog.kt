package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.Donor
import com.example.model.DonorDate
import com.example.model.VerificationStatus
import com.example.ui.components.AvailabilityIndicator
import com.example.ui.components.PhoneIntentHelper
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BloodRed600
import com.example.ui.theme.BloodRed800
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.OutlineSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceOffWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VerifiedGreen

@Composable
fun DonorProfileDialog(
  donor: Donor,
  currentLanguage: AppLanguage,
  onDismiss: () -> Unit,
  onUpdateDonationDate: () -> Unit,
  onToggleAvailability: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isBengali = currentLanguage == AppLanguage.BN
  val today = remember { DonorDate.today() }
  val isReady = donor.isAvailableNow(today)
  val daysRemaining = donor.daysUntilEligible(today)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("donor_profile_dialog"),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Red Top Header Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(BloodRed800)
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isBengali) "রক্তদাতার প্রোফাইল" else "Donor Profile",
              color = PureWhite,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = PureWhite
              )
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Large Blood Group Circular Badge
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .background(BloodRedContainer)
              .border(3.dp, BloodRed800, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = donor.bloodGroup.label,
              fontSize = 28.sp,
              fontWeight = FontWeight.ExtraBold,
              color = BloodRed800
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Donor Name
          Text(
            text = donor.displayName(isBengali),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Upazila & Area
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = BloodRed800,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${donor.upazila.displayName(isBengali)} • ${donor.displayArea(isBengali)}",
              fontSize = 14.sp,
              color = TextMuted
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Verification Status Pill
          StatusBadge(status = donor.verificationStatus, isBengali = isBengali)

          Spacer(modifier = Modifier.height(16.dp))

          // Quick Phone Call & SMS Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { PhoneIntentHelper.dialPhone(context, donor.phone) },
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("profile_call_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = BloodRed800,
                contentColor = PureWhite
              )
            ) {
              Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${AppStrings.callAction(currentLanguage)} (${donor.phone})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }

            OutlinedButton(
              onClick = {
                val msg = if (isBengali)
                  "আসসালামু আলাইকুম ${donor.displayName(true)}, LifeLink CP অ্যাপ থেকে জরুরি রক্তদানের বিষয়ে যোগাযোগ করা হচ্ছে।"
                else
                  "Hello ${donor.name}, reaching out for urgent blood donation via LifeLink CP."
                PhoneIntentHelper.sendSms(context, donor.phone, msg)
              },
              modifier = Modifier
                .weight(0.6f)
                .height(44.dp)
                .testTag("profile_sms_button"),
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.5.dp, BloodRed800),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = BloodRed800)
            ) {
              Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(AppStrings.smsAction(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Availability Details Card
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceOffWhite,
            border = BorderStroke(1.dp, OutlineSoft)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = if (isBengali) "রক্তদান যোগ্যতা ও প্রাপ্যতা" else "Donation Eligibility & Status",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextDark
              )
              Spacer(modifier = Modifier.height(8.dp))

              // Eligibility calculation
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (isBengali) "বর্তমান অবস্থা:" else "Current Status:",
                  fontSize = 12.sp,
                  color = TextMuted
                )
                if (donor.isFirstTimeDonor) {
                  AvailabilityIndicator(isReady = true, text = AppStrings.firstTimeDonor(currentLanguage))
                } else if (isReady) {
                  AvailabilityIndicator(isReady = true, text = AppStrings.availableNow(currentLanguage))
                } else {
                  AvailabilityIndicator(isReady = false, text = AppStrings.eligibleInDays(daysRemaining, currentLanguage))
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              HorizontalDivider(color = OutlineSoft)
              Spacer(modifier = Modifier.height(8.dp))

              // Last Donation Date
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = AppStrings.lastDonated(currentLanguage),
                  fontSize = 12.sp,
                  color = TextMuted
                )
                Text(
                  text = if (donor.isFirstTimeDonor || donor.lastDonationDate == null) {
                    AppStrings.neverDonated(currentLanguage)
                  } else {
                    if (isBengali) donor.lastDonationDate.toFormattedStringBn() else donor.lastDonationDate.toFormattedString()
                  },
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextDark
                )
              }

              if (!donor.lastDonationLocation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = if (isBengali) "রক্তদানের স্থান:" else "Place:",
                    fontSize = 12.sp,
                    color = TextMuted
                  )
                  Text(
                    text = donor.lastDonationLocation,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              HorizontalDivider(color = OutlineSoft)
              Spacer(modifier = Modifier.height(8.dp))

              // Total Donations
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (isBengali) "মোট রক্তদান:" else "Total Donations:",
                  fontSize = 12.sp,
                  color = TextMuted
                )
                Text(
                  text = AppStrings.totalDonationsCount(donor.totalDonations, currentLanguage),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = BloodRed800
                )
              }

              Spacer(modifier = Modifier.height(8.dp))
              HorizontalDivider(color = OutlineSoft)
              Spacer(modifier = Modifier.height(8.dp))

              // Willingness / Manual Availability Switch
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = if (isBengali) "রক্তদানে সম্মতি" else "Donor Willingness",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                  )
                  Text(
                    text = if (donor.isAvailableManually)
                      (if (isBengali) "রক্ত দিতে ইচ্ছুক" else "Willing to donate")
                    else
                      (if (isBengali) "সাময়িক অনুপলব্ধ" else "Temporarily unavailable"),
                    fontSize = 11.sp,
                    color = TextMuted
                  )
                }

                Switch(
                  checked = donor.isAvailableManually,
                  onCheckedChange = { onToggleAvailability() },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = PureWhite,
                    checkedTrackColor = VerifiedGreen,
                    uncheckedTrackColor = OutlineSoft
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Update Last Donation Date Action Button
          Button(
            onClick = onUpdateDonationDate,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("profile_update_date_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = BloodRedContainer,
              contentColor = BloodRed800
            )
          ) {
            Icon(Icons.Outlined.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.updateDonationDate(currentLanguage),
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Optional Notes
          if (!donor.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (isBengali) "মন্তব্য: ${donor.notes}" else "Notes: ${donor.notes}",
              fontSize = 11.sp,
              color = TextMuted,
              modifier = Modifier.align(Alignment.Start)
            )
          }
        }
      }
    }
  }
}
