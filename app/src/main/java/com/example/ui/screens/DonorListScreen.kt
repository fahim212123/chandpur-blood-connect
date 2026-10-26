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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.EditCalendar
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.BloodGroup
import com.example.model.Donor
import com.example.model.DonorDate
import com.example.model.Upazila
import com.example.ui.components.AvailabilityIndicator
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.PhoneIntentHelper
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BloodRed600
import com.example.ui.theme.BloodRed800
import com.example.ui.theme.BloodRedBorder
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.OutlineSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceOffWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VerifiedGreen

@Composable
fun DonorListScreen(
  donors: List<Donor>,
  selectedBloodGroup: BloodGroup?,
  selectedUpazila: Upazila?,
  searchQuery: String,
  onlyAvailable: Boolean,
  currentLanguage: AppLanguage,
  onBloodGroupSelect: (BloodGroup?) -> Unit,
  onUpazilaSelect: (Upazila?) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onToggleOnlyAvailable: (Boolean) -> Unit,
  onDonorClick: (Donor) -> Unit,
  onUpdateDateClick: (Donor) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isBengali = currentLanguage == AppLanguage.BN
  val today = remember { DonorDate.today() }

  val filteredDonors by remember(donors, selectedBloodGroup, selectedUpazila, searchQuery, onlyAvailable) {
    derivedStateOf {
      donors.filter { donor ->
        // Blood group filter
        val matchesGroup = selectedBloodGroup == null || donor.bloodGroup == selectedBloodGroup
        // Upazila filter
        val matchesUpazila = selectedUpazila == null || donor.upazila == selectedUpazila
        // Search query
        val matchesQuery = if (searchQuery.isBlank()) true else {
          donor.name.contains(searchQuery, ignoreCase = true) ||
              (donor.nameBn?.contains(searchQuery, ignoreCase = true) == true) ||
              donor.phone.contains(searchQuery) ||
              donor.area.contains(searchQuery, ignoreCase = true) ||
              (donor.areaBn?.contains(searchQuery, ignoreCase = true) == true)
        }
        // Availability
        val matchesAvailability = if (onlyAvailable) donor.isAvailableNow(today) else true

        matchesGroup && matchesUpazila && matchesQuery && matchesAvailability
      }
    }
  }

  val totalAvailableCount by remember(donors) {
    derivedStateOf { donors.count { it.isAvailableNow(today) } }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceOffWhite)
  ) {
    // Top Filter & Search Section
    Surface(
      color = PureWhite,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp, bottom = 8.dp)
      ) {
        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = { Text(AppStrings.searchHint(currentLanguage), fontSize = 13.sp) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = BloodRed800,
              modifier = Modifier.size(20.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChange("") }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear search",
                  tint = TextMuted,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BloodRed800,
            unfocusedBorderColor = OutlineSoft,
            focusedContainerColor = PureWhite,
            unfocusedContainerColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Blood Group Horizontal Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedBloodGroup == null,
            onClick = { onBloodGroupSelect(null) },
            label = { Text(AppStrings.allGroups(currentLanguage), fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BloodRed800,
              selectedLabelColor = PureWhite
            ),
            modifier = Modifier.testTag("filter_all_blood_groups")
          )

          BloodGroup.entries.forEach { group ->
            FilterChip(
              selected = selectedBloodGroup == group,
              onClick = { onBloodGroupSelect(if (selectedBloodGroup == group) null else group) },
              label = {
                Text(
                  text = group.label,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = BloodRed800,
                selectedLabelColor = PureWhite
              ),
              modifier = Modifier.testTag("filter_blood_${group.name}")
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Location & Availability row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // All Upazilas
          FilterChip(
            selected = selectedUpazila == null,
            onClick = { onUpazilaSelect(null) },
            label = { Text(AppStrings.allLocations(currentLanguage), fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BloodRedContainer,
              selectedLabelColor = BloodRed800
            ),
            modifier = Modifier.testTag("filter_all_locations")
          )

          // Chandpur Sadar
          FilterChip(
            selected = selectedUpazila == Upazila.CHANDPUR_SADAR,
            onClick = {
              onUpazilaSelect(if (selectedUpazila == Upazila.CHANDPUR_SADAR) null else Upazila.CHANDPUR_SADAR)
            },
            label = { Text(Upazila.CHANDPUR_SADAR.displayName(isBengali), fontSize = 11.sp) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BloodRedContainer,
              selectedLabelColor = BloodRed800
            ),
            modifier = Modifier.testTag("filter_chandpur_sadar")
          )

          // Faridganj
          FilterChip(
            selected = selectedUpazila == Upazila.FARIDGANJ,
            onClick = {
              onUpazilaSelect(if (selectedUpazila == Upazila.FARIDGANJ) null else Upazila.FARIDGANJ)
            },
            label = { Text(Upazila.FARIDGANJ.displayName(isBengali), fontSize = 11.sp) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BloodRedContainer,
              selectedLabelColor = BloodRed800
            ),
            modifier = Modifier.testTag("filter_faridganj")
          )

          // Only Available Toggle
          FilterChip(
            selected = onlyAvailable,
            onClick = { onToggleOnlyAvailable(!onlyAvailable) },
            label = { Text(AppStrings.filterAvailableOnly(currentLanguage), fontSize = 11.sp) },
            leadingIcon = {
              if (onlyAvailable) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp)
                )
              }
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = VerifiedGreen,
              selectedLabelColor = PureWhite
            ),
            modifier = Modifier.testTag("filter_available_toggle")
          )
        }
      }
    }

    // Results Header Count
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = AppStrings.donorsFound(filteredDonors.size, currentLanguage),
        color = TextDark,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )

      Surface(
        color = BloodRedContainer,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = if (isBengali)
            "${AppStrings.toBengaliDigits(totalAvailableCount.toString())} জন প্রস্তুত"
          else
            "$totalAvailableCount Ready Now",
          color = BloodRed800,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    // Donor List or Empty State
    if (filteredDonors.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Bloodtype,
            contentDescription = null,
            tint = BloodRedBorder,
            modifier = Modifier.size(64.dp)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = if (isBengali) "কোনো রক্তদাতা পাওয়া যায়নি" else "No Donors Found",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (isBengali)
              "অনুগ্রহ করে ফিল্টার পরিবর্তন করুন অথবা অনুসন্ধান মুছে দিন।"
            else
              "Try adjusting your filters or search keywords.",
            color = TextMuted,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = {
              onBloodGroupSelect(null)
              onUpazilaSelect(null)
              onSearchQueryChange("")
              onToggleOnlyAvailable(false)
            },
            colors = ButtonDefaults.buttonColors(containerColor = BloodRed800)
          ) {
            Text(if (isBengali) "সব ফিল্টার রিসেট করুন" else "Reset All Filters")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(filteredDonors, key = { it.id }) { donor ->
          DonorCard(
            donor = donor,
            isBengali = isBengali,
            today = today,
            currentLanguage = currentLanguage,
            onCardClick = { onDonorClick(donor) },
            onCallClick = { PhoneIntentHelper.dialPhone(context, donor.phone) },
            onSmsClick = {
              val msg = if (isBengali)
                "আসসালামু আলাইকুম, LifeLink CP অ্যাপ থেকে আপনার সাথে জরুরি রক্তদানের বিষয়ে যোগাযোগ করা হচ্ছে।"
              else
                "Hello, contacting you regarding blood donation via LifeLink CP."
              PhoneIntentHelper.sendSms(context, donor.phone, msg)
            },
            onUpdateDateClick = { onUpdateDateClick(donor) }
          )
        }
      }
    }
  }
}

@Composable
fun DonorCard(
  donor: Donor,
  isBengali: Boolean,
  today: DonorDate,
  currentLanguage: AppLanguage,
  onCardClick: () -> Unit,
  onCallClick: () -> Unit,
  onSmsClick: () -> Unit,
  onUpdateDateClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isReady = donor.isAvailableNow(today)
  val daysRemaining = donor.daysUntilEligible(today)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("donor_card_${donor.id}")
      .clickable { onCardClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    border = BorderStroke(1.dp, OutlineSoft)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Row 1: Blood Group, Name, Verification Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        BloodGroupBadge(
          bloodGroup = donor.bloodGroup,
          size = 46.dp,
          fontSize = 17
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = donor.displayName(isBengali),
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = TextDark
            )
            StatusBadge(status = donor.verificationStatus, isBengali = isBengali)
          }

          Spacer(modifier = Modifier.height(3.dp))

          // Location
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = BloodRed800,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${donor.upazila.displayName(isBengali)} • ${donor.displayArea(isBengali)}",
              color = TextMuted,
              fontSize = 12.sp,
              fontWeight = FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Row 2: Availability & Last Donation Date
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SurfaceOffWhite)
          .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Availability Status
        if (donor.isFirstTimeDonor) {
          AvailabilityIndicator(
            isReady = true,
            text = AppStrings.firstTimeDonor(currentLanguage)
          )
        } else if (isReady) {
          AvailabilityIndicator(
            isReady = true,
            text = AppStrings.availableNow(currentLanguage)
          )
        } else {
          AvailabilityIndicator(
            isReady = false,
            text = AppStrings.eligibleInDays(daysRemaining, currentLanguage)
          )
        }

        // Last Donation Date
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (donor.isFirstTimeDonor || donor.lastDonationDate == null) {
              if (isBengali) "প্রথমবার" else "First time"
            } else {
              if (isBengali) donor.lastDonationDate.toFormattedStringBn() else donor.lastDonationDate.toFormattedString()
            },
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Row 3: Action Buttons (Call, SMS, Update Date)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Call Button
        Button(
          onClick = onCallClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BloodRed800,
            contentColor = PureWhite
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("call_button_${donor.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = AppStrings.callAction(currentLanguage),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // SMS Button
        OutlinedButton(
          onClick = onSmsClick,
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, BloodRed800),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = BloodRed800),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(0.9f)
            .height(38.dp)
            .testTag("sms_button_${donor.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Message,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = AppStrings.smsAction(currentLanguage),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Quick Date Update icon button
        OutlinedButton(
          onClick = onUpdateDateClick,
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, OutlineSoft),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .height(38.dp)
            .testTag("update_date_button_${donor.id}")
        ) {
          Icon(
            imageVector = Icons.Outlined.EditCalendar,
            contentDescription = "Update donation date",
            tint = BloodRed800,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
