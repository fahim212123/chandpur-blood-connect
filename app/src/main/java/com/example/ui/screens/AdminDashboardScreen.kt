package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.model.Donor
import com.example.model.VerificationStatus
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.PhoneIntentHelper
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BloodRed600
import com.example.ui.theme.BloodRed800
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.OutlineSoft
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.PendingAmberContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RejectedRed
import com.example.ui.theme.RejectedRedContainer
import com.example.ui.theme.SurfaceOffWhite
import com.example.ui.theme.SuspendedGray
import com.example.ui.theme.SuspendedGrayContainer
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer

@Composable
fun AdminDashboardScreen(
  donors: List<Donor>,
  activeTab: VerificationStatus,
  currentLanguage: AppLanguage,
  onTabSelect: (VerificationStatus) -> Unit,
  onStatusChange: (donorId: String, newStatus: VerificationStatus) -> Unit,
  onDonorClick: (Donor) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isBengali = currentLanguage == AppLanguage.BN

  val pendingDonors by remember(donors) {
    derivedStateOf { donors.filter { it.verificationStatus == VerificationStatus.PENDING } }
  }
  val verifiedDonors by remember(donors) {
    derivedStateOf { donors.filter { it.verificationStatus == VerificationStatus.VERIFIED } }
  }
  val rejectedDonors by remember(donors) {
    derivedStateOf { donors.filter { it.verificationStatus == VerificationStatus.REJECTED } }
  }
  val suspendedDonors by remember(donors) {
    derivedStateOf { donors.filter { it.verificationStatus == VerificationStatus.SUSPENDED } }
  }

  val displayedDonors = when (activeTab) {
    VerificationStatus.PENDING -> pendingDonors
    VerificationStatus.VERIFIED -> verifiedDonors
    VerificationStatus.REJECTED -> rejectedDonors
    VerificationStatus.SUSPENDED -> suspendedDonors
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceOffWhite)
  ) {
    // Admin Top Stats Banner
    Surface(
      color = PureWhite,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(BloodRedContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = BloodRed800,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = AppStrings.adminTitle(currentLanguage),
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = BloodRed800
            )
            Text(
              text = AppStrings.adminSubtitle(currentLanguage),
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4 Stat Counters Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AdminStatPill(
            label = VerificationStatus.PENDING.displayName(isBengali),
            count = pendingDonors.size,
            containerColor = PendingAmberContainer,
            contentColor = PendingAmber,
            isSelected = activeTab == VerificationStatus.PENDING,
            onClick = { onTabSelect(VerificationStatus.PENDING) },
            isBengali = isBengali,
            modifier = Modifier.weight(1f)
          )

          AdminStatPill(
            label = VerificationStatus.VERIFIED.displayName(isBengali),
            count = verifiedDonors.size,
            containerColor = VerifiedGreenContainer,
            contentColor = VerifiedGreen,
            isSelected = activeTab == VerificationStatus.VERIFIED,
            onClick = { onTabSelect(VerificationStatus.VERIFIED) },
            isBengali = isBengali,
            modifier = Modifier.weight(1f)
          )

          AdminStatPill(
            label = VerificationStatus.REJECTED.displayName(isBengali),
            count = rejectedDonors.size,
            containerColor = RejectedRedContainer,
            contentColor = RejectedRed,
            isSelected = activeTab == VerificationStatus.REJECTED,
            onClick = { onTabSelect(VerificationStatus.REJECTED) },
            isBengali = isBengali,
            modifier = Modifier.weight(1f)
          )

          AdminStatPill(
            label = VerificationStatus.SUSPENDED.displayName(isBengali),
            count = suspendedDonors.size,
            containerColor = SuspendedGrayContainer,
            contentColor = SuspendedGray,
            isSelected = activeTab == VerificationStatus.SUSPENDED,
            onClick = { onTabSelect(VerificationStatus.SUSPENDED) },
            isBengali = isBengali,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 4 Modern Tabs
    val tabs = listOf(
      VerificationStatus.PENDING,
      VerificationStatus.VERIFIED,
      VerificationStatus.REJECTED,
      VerificationStatus.SUSPENDED
    )
    val selectedTabIndex = tabs.indexOf(activeTab)

    ScrollableTabRow(
      selectedTabIndex = selectedTabIndex,
      containerColor = PureWhite,
      contentColor = BloodRed800,
      edgePadding = 16.dp,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
          color = BloodRed800,
          height = 3.dp
        )
      }
    ) {
      tabs.forEach { tab ->
        val isSelected = activeTab == tab
        val tabCount = when (tab) {
          VerificationStatus.PENDING -> pendingDonors.size
          VerificationStatus.VERIFIED -> verifiedDonors.size
          VerificationStatus.REJECTED -> rejectedDonors.size
          VerificationStatus.SUSPENDED -> suspendedDonors.size
        }
        val displayCount = if (isBengali) AppStrings.toBengaliDigits(tabCount.toString()) else tabCount.toString()

        Tab(
          selected = isSelected,
          onClick = { onTabSelect(tab) },
          text = {
            Text(
              text = "${tab.displayName(isBengali)} ($displayCount)",
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) BloodRed800 else TextMuted
            )
          },
          modifier = Modifier.testTag("admin_tab_${tab.name}")
        )
      }
    }

    // Tab Content List
    if (displayedDonors.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = when (activeTab) {
              VerificationStatus.PENDING -> Icons.Default.HourglassTop
              VerificationStatus.VERIFIED -> Icons.Default.CheckCircle
              VerificationStatus.REJECTED -> Icons.Default.Warning
              VerificationStatus.SUSPENDED -> Icons.Default.Lock
            },
            contentDescription = null,
            tint = OutlineSoft,
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = AppStrings.noDonorsInStatus(activeTab.displayName(isBengali), currentLanguage),
            color = TextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(displayedDonors, key = { it.id }) { donor ->
          AdminDonorCard(
            donor = donor,
            isBengali = isBengali,
            currentLanguage = currentLanguage,
            onCardClick = { onDonorClick(donor) },
            onCallClick = { PhoneIntentHelper.dialPhone(context, donor.phone) },
            onApprove = { onStatusChange(donor.id, VerificationStatus.VERIFIED) },
            onReject = { onStatusChange(donor.id, VerificationStatus.REJECTED) },
            onSuspend = { onStatusChange(donor.id, VerificationStatus.SUSPENDED) },
            onReactivate = { onStatusChange(donor.id, VerificationStatus.VERIFIED) }
          )
        }
      }
    }
  }
}

@Composable
fun AdminStatPill(
  label: String,
  count: Int,
  containerColor: Color,
  contentColor: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  isBengali: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) containerColor else SurfaceOffWhite,
    border = BorderStroke(1.5.dp, if (isSelected) contentColor else OutlineSoft),
    modifier = modifier.clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = if (isBengali) AppStrings.toBengaliDigits(count.toString()) else count.toString(),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = contentColor
      )
      Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextDark.copy(alpha = 0.85f),
        maxLines = 1
      )
    }
  }
}

@Composable
fun AdminDonorCard(
  donor: Donor,
  isBengali: Boolean,
  currentLanguage: AppLanguage,
  onCardClick: () -> Unit,
  onCallClick: () -> Unit,
  onApprove: () -> Unit,
  onReject: () -> Unit,
  onSuspend: () -> Unit,
  onReactivate: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    border = BorderStroke(1.dp, OutlineSoft),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("admin_donor_card_${donor.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Blood Group, Name, Upazila
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 42.dp, fontSize = 15)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = donor.displayName(isBengali),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextDark
          )
          Text(
            text = "${donor.upazila.displayName(isBengali)} • ${donor.displayArea(isBengali)}",
            fontSize = 12.sp,
            color = TextMuted
          )
        }
        StatusBadge(status = donor.verificationStatus, isBengali = isBengali)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Phone & First-time info row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SurfaceOffWhite)
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onCallClick() }
        ) {
          Icon(Icons.Default.Phone, contentDescription = null, tint = BloodRed800, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = donor.phone, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BloodRed800)
        }

        if (donor.isFirstTimeDonor) {
          Text(
            text = if (isBengali) "প্রথমবার রক্তদাতা" else "First-time donor",
            fontSize = 11.sp,
            color = VerifiedGreen,
            fontWeight = FontWeight.SemiBold
          )
        } else {
          Text(
            text = if (isBengali)
              "মোট: ${AppStrings.toBengaliDigits(donor.totalDonations.toString())} বার"
            else
              "Total: ${donor.totalDonations} times",
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }

      // Notes if any
      if (!donor.notes.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = donor.notes,
          fontSize = 11.sp,
          color = TextMuted,
          maxLines = 2
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons depending on status tab
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        when (donor.verificationStatus) {
          VerificationStatus.PENDING -> {
            // Approve button
            Button(
              onClick = onApprove,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen, contentColor = PureWhite),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("admin_approve_${donor.id}")
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(AppStrings.approveAction(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Reject button
            OutlinedButton(
              onClick = onReject,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, RejectedRed),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = RejectedRed),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("admin_reject_${donor.id}")
            ) {
              Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(AppStrings.rejectAction(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          VerificationStatus.VERIFIED -> {
            // Suspend option
            OutlinedButton(
              onClick = onSuspend,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, SuspendedGray),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = SuspendedGray),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("admin_suspend_${donor.id}")
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(AppStrings.suspendAction(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          VerificationStatus.REJECTED -> {
            // Reactivate / Re-evaluate button
            Button(
              onClick = onReactivate,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BloodRed800, contentColor = PureWhite),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("admin_reactivate_${donor.id}")
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isBengali) "পুনরায় সক্রিয় করুন" else "Re-evaluate & Verify",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          VerificationStatus.SUSPENDED -> {
            // Reactivate button
            Button(
              onClick = onReactivate,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen, contentColor = PureWhite),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("admin_unsuspend_${donor.id}")
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(AppStrings.reactivateAction(currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
