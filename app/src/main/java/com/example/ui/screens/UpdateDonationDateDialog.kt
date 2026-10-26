package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.BloodGroupBadge
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
fun UpdateDonationDateDialog(
  donor: Donor,
  currentLanguage: AppLanguage,
  onDismiss: () -> Unit,
  onSaveDate: (newDate: DonorDate, hospital: String?) -> Unit,
  modifier: Modifier = Modifier
) {
  val isBengali = currentLanguage == AppLanguage.BN
  val today = remember { DonorDate.today() }

  // Days offset: 0 = Today, 1 = Yesterday, 7 = 1 week ago, 30 = 1 month ago, 90 = 3 months ago
  var selectedDaysAgo by remember { mutableIntStateOf(0) }
  var hospitalName by remember {
    mutableStateOf(donor.lastDonationLocation ?: "Chandpur 250 Bed General Hospital")
  }

  val selectedDate = remember(selectedDaysAgo) { DonorDate.daysAgo(selectedDaysAgo) }

  val hospitalSuggestions = remember {
    listOf(
      "Chandpur 250 Bed Hospital",
      "Faridganj Upazila Health Complex",
      "Red Crescent Blood Bank Chandpur",
      "Premier Hospital Chandpur"
    )
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(22.dp))
        .testTag("update_donation_date_dialog"),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Red Top Header
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
              text = AppStrings.updateDateTitle(currentLanguage),
              color = PureWhite,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(30.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = PureWhite
              )
            }
          }
        }

        Column(modifier = Modifier.padding(18.dp)) {

          // Donor mini banner
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(BloodRedContainer)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 42.dp, fontSize = 15)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = donor.displayName(isBengali),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BloodRed800
              )
              Text(
                text = "${donor.upazila.displayName(isBengali)} • ${donor.displayArea(isBengali)}",
                fontSize = 11.sp,
                color = TextDark.copy(alpha = 0.8f)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Instruction text
          Text(
            text = AppStrings.updateDateDesc(currentLanguage),
            fontSize = 12.sp,
            color = TextMuted,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Select Timeline
          Text(
            text = AppStrings.donationDateOption(currentLanguage),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextDark
          )
          Spacer(modifier = Modifier.height(8.dp))

          val timelineOptions = listOf(
            0 to (if (isBengali) "আজ (${today.day}/${today.month})" else "Today (${today.day}/${today.month})"),
            1 to (if (isBengali) "গতকাল" else "Yesterday"),
            7 to (if (isBengali) "৭ দিন আগে" else "7 days ago"),
            30 to (if (isBengali) "১ মাস আগে" else "1 month ago"),
            90 to (if (isBengali) "৩ মাস আগে" else "3 months ago"),
            120 to (if (isBengali) "৪ মাস আগে" else "4 months ago")
          )

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // First row of 3
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              timelineOptions.take(3).forEach { (days, label) ->
                val isSelected = selectedDaysAgo == days
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) BloodRed800 else SurfaceOffWhite,
                  border = BorderStroke(1.dp, if (isSelected) BloodRed800 else OutlineSoft),
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedDaysAgo = days }
                ) {
                  Box(
                    modifier = Modifier.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = label,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) PureWhite else TextDark
                    )
                  }
                }
              }
            }

            // Second row of 3
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              timelineOptions.drop(3).forEach { (days, label) ->
                val isSelected = selectedDaysAgo == days
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) BloodRed800 else SurfaceOffWhite,
                  border = BorderStroke(1.dp, if (isSelected) BloodRed800 else OutlineSoft),
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedDaysAgo = days }
                ) {
                  Box(
                    modifier = Modifier.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = label,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) PureWhite else TextDark
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Selected date preview banner
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = SurfaceOffWhite,
            border = BorderStroke(1.dp, OutlineSoft),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = BloodRed800,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = if (isBengali) "নির্বাচিত তারিখ:" else "Selected Date:",
                  fontSize = 11.sp,
                  color = TextMuted
                )
                Text(
                  text = if (isBengali) selectedDate.toFormattedStringBn() else selectedDate.toFormattedString(),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = BloodRed800
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Donation Hospital / Location
          Text(
            text = AppStrings.donationHospitalLabel(currentLanguage),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = hospitalName,
            onValueChange = { hospitalName = it },
            placeholder = { Text(if (isBengali) "যেমন: চাঁদপুর ২৫০ শয্যা হাসপাতাল" else "e.g. Hospital name", fontSize = 12.sp) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = null,
                tint = BloodRed800,
                modifier = Modifier.size(18.dp)
              )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BloodRed800,
              unfocusedBorderColor = OutlineSoft
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("update_hospital_input")
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Hospital suggestion chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            hospitalSuggestions.forEach { name ->
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceOffWhite,
                border = BorderStroke(1.dp, OutlineSoft),
                modifier = Modifier.clickable { hospitalName = name }
              ) {
                Text(
                  text = name,
                  fontSize = 10.sp,
                  color = TextMuted,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Action Buttons: Save & Cancel
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, OutlineSoft),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
            ) {
              Text(AppStrings.cancel(currentLanguage), color = TextDark, fontSize = 13.sp)
            }

            Button(
              onClick = { onSaveDate(selectedDate, hospitalName) },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BloodRed800, contentColor = PureWhite),
              modifier = Modifier
                .weight(1.3f)
                .height(44.dp)
                .testTag("save_donation_date_button")
            ) {
              Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(AppStrings.saveUpdate(currentLanguage), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
