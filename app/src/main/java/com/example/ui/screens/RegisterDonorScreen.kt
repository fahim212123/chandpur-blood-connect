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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.BloodGroup
import com.example.model.DonorDate
import com.example.model.Upazila
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
fun RegisterDonorScreen(
  currentLanguage: AppLanguage,
  onRegisterSubmit: (
    name: String,
    phone: String,
    bloodGroup: BloodGroup,
    upazila: Upazila,
    area: String,
    isFirstTime: Boolean,
    lastDonationDate: DonorDate?,
    emergencyPhone: String?,
    notes: String?
  ) -> Boolean,
  modifier: Modifier = Modifier
) {
  val isBengali = currentLanguage == AppLanguage.BN

  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var selectedBloodGroup by remember { mutableStateOf(BloodGroup.O_POS) }
  var selectedUpazila by remember { mutableStateOf(Upazila.CHANDPUR_SADAR) }
  var area by remember { mutableStateOf("") }
  var isFirstTimeDonor by remember { mutableStateOf(false) }
  var lastDonationDaysAgo by remember { mutableStateOf(100) } // default ~3+ months ago
  var emergencyPhone by remember { mutableStateOf("") }
  var agreementChecked by remember { mutableStateOf(true) }
  var validationError by remember { mutableStateOf<String?>(null) }

  val chandpurAreas = remember {
    listOf("Puran Bazar", "Natun Bazar", "Baburhat", "Bagadi", "Bishnupur", "Ashikati", "Kalyanpur")
  }
  val faridganjAreas = remember {
    listOf("Faridganj Bazar", "Rupsa North", "Rupsa South", "Gupti", "Paikpara", "Balithuba", "Subidpur")
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceOffWhite)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .padding(bottom = 80.dp)
  ) {
    // Top Hero Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      border = BorderStroke(1.dp, BloodRedContainer),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(BloodRedContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.VolunteerActivism,
            contentDescription = null,
            tint = BloodRed800,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = AppStrings.regTitle(currentLanguage),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = BloodRed800
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = AppStrings.regSubtitle(currentLanguage),
            fontSize = 12.sp,
            color = TextMuted,
            lineHeight = 16.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Form Container Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      border = BorderStroke(1.dp, OutlineSoft),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {

        // 1. Full Name
        Text(
          text = AppStrings.fullName(currentLanguage) + " *",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            validationError = null
          },
          placeholder = { Text(if (isBengali) "যেমন: মো: তানভীর হোসেন" else "e.g. Tanvir Hossain", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BloodRed800) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BloodRed800,
            unfocusedBorderColor = OutlineSoft
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_name_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Mobile Phone Number
        Text(
          text = AppStrings.phoneNumber(currentLanguage) + " *",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = phone,
          onValueChange = {
            if (it.length <= 11) phone = it
            validationError = null
          },
          placeholder = { Text(if (isBengali) "০১৭১২৩৪৫৬৭৮" else "017XXXXXXXX", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BloodRed800) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BloodRed800,
            unfocusedBorderColor = OutlineSoft
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_phone_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Blood Group Selection (8 chips)
        Text(
          text = AppStrings.bloodGroupLabel(currentLanguage) + " *",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          BloodGroup.entries.forEach { group ->
            val isSelected = selectedBloodGroup == group
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) BloodRed800 else SurfaceOffWhite,
              border = BorderStroke(1.dp, if (isSelected) BloodRed800 else OutlineSoft),
              modifier = Modifier
                .clickable { selectedBloodGroup = group }
                .testTag("reg_blood_${group.name}")
            ) {
              Box(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = group.label,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = if (isSelected) PureWhite else TextDark
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Location: Upazila Selector
        Text(
          text = AppStrings.upazilaLabel(currentLanguage) + " *",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Upazila.entries.forEach { upazila ->
            val isSelected = selectedUpazila == upazila
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) BloodRedContainer else SurfaceOffWhite,
              border = BorderStroke(1.5.dp, if (isSelected) BloodRed800 else OutlineSoft),
              modifier = Modifier
                .weight(1f)
                .clickable { selectedUpazila = upazila }
                .testTag("reg_upazila_${upazila.name}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = if (isSelected) BloodRed800 else TextMuted,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = upazila.displayName(isBengali),
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 13.sp,
                  color = if (isSelected) BloodRed800 else TextDark
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Area / Union Input & Quick suggestions
        Text(
          text = AppStrings.areaLabel(currentLanguage) + " *",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = area,
          onValueChange = { area = it },
          placeholder = {
            Text(
              if (selectedUpazila == Upazila.CHANDPUR_SADAR)
                (if (isBengali) "যেমন: পুরাণ বাজার, বাবুরহাট" else "e.g. Puran Bazar, Baburhat")
              else
                (if (isBengali) "যেমন: রূপসা, গুপ্তি, পাইকপাড়া" else "e.g. Rupsa, Gupti, Paikpara"),
              fontSize = 13.sp
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
            .testTag("reg_area_input")
        )

        // Quick suggested chip clicks
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val suggestions = if (selectedUpazila == Upazila.CHANDPUR_SADAR) chandpurAreas else faridganjAreas
          suggestions.forEach { place ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = SurfaceOffWhite,
              border = BorderStroke(1.dp, OutlineSoft),
              modifier = Modifier.clickable { area = place }
            ) {
              Text(
                text = place,
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 6. First-time Donor Option (Requirement #3)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (isFirstTimeDonor) BloodRedContainer.copy(alpha = 0.6f) else SurfaceOffWhite,
          border = BorderStroke(1.dp, if (isFirstTimeDonor) BloodRed800 else OutlineSoft),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { isFirstTimeDonor = !isFirstTimeDonor }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = AppStrings.firstTimeCheckbox(currentLanguage),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isFirstTimeDonor) BloodRed800 else TextDark
              )
              Text(
                text = if (isBengali)
                  "পূর্বে কখনো রক্ত না দিয়ে থাকলে এটি চালু রাখুন।"
                else
                  "Toggle on if you haven't donated blood before.",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
            Switch(
              checked = isFirstTimeDonor,
              onCheckedChange = { isFirstTimeDonor = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = PureWhite,
                checkedTrackColor = BloodRed800,
                uncheckedTrackColor = OutlineSoft
              ),
              modifier = Modifier.testTag("reg_first_time_switch")
            )
          }
        }

        // 7. If NOT first-time donor: Last Donation Timeline
        if (!isFirstTimeDonor) {
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = AppStrings.lastDonationDateLabel(currentLanguage),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextDark
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Presets row: 3 months ago (eligible), 1 month ago (not eligible yet), 6 months ago
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val presets = listOf(
              Triple(100, if (isBengali) "৩+ মাস আগে (যোগ্য)" else "3+ months ago (Eligible)", true),
              Triple(30, if (isBengali) "১ মাস আগে (বিশ্রাম)" else "1 month ago (Resting)", false),
              Triple(180, if (isBengali) "৬ মাস আগে" else "6 months ago", true),
              Triple(10, if (isBengali) "১০ দিন আগে" else "10 days ago", false)
            )

            presets.forEach { (days, label, _) ->
              FilterChip(
                selected = lastDonationDaysAgo == days,
                onClick = { lastDonationDaysAgo = days },
                label = { Text(label, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = BloodRed800,
                  selectedLabelColor = PureWhite
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 8. Emergency Alternate Phone (Optional)
        Text(
          text = AppStrings.emergencyContactLabel(currentLanguage),
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = emergencyPhone,
          onValueChange = { emergencyPhone = it },
          placeholder = { Text(if (isBengali) "ঐচ্ছিক" else "Optional contact number", fontSize = 13.sp) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BloodRed800,
            unfocusedBorderColor = OutlineSoft
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 9. Agreement Checkbox
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = agreementChecked,
            onCheckedChange = { agreementChecked = it },
            colors = CheckboxDefaults.colors(
              checkedColor = BloodRed800,
              checkmarkColor = PureWhite
            ),
            modifier = Modifier.testTag("reg_agreement_checkbox")
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = AppStrings.agreementText(currentLanguage),
            fontSize = 11.sp,
            color = TextMuted,
            lineHeight = 15.sp,
            modifier = Modifier.clickable { agreementChecked = !agreementChecked }
          )
        }

        // Validation Error Message if any
        if (validationError != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = validationError ?: "",
            color = BloodRed600,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Submit Button
        Button(
          onClick = {
            if (name.isBlank()) {
              validationError = if (isBengali) "অনুগ্রহ করে আপনার নাম লিখুন।" else "Please enter your name."
              return@Button
            }
            if (phone.trim().length < 11) {
              validationError = if (isBengali)
                "১১ ডিজিটের সঠিক মোবাইল নম্বর দিন (যেমন: ০১৭১২৩৪৫৬৭৮)।"
              else
                "Please enter a valid 11-digit mobile number."
              return@Button
            }
            if (!agreementChecked) {
              validationError = if (isBengali)
                "রক্তদানে সম্মতি জানিয়ে বক্সে টিক দিন।"
              else
                "Please confirm your agreement to proceed."
              return@Button
            }

            val lastDate = if (isFirstTimeDonor) null else DonorDate.daysAgo(lastDonationDaysAgo)
            val success = onRegisterSubmit(
              name,
              phone,
              selectedBloodGroup,
              selectedUpazila,
              area.ifBlank { selectedUpazila.enName },
              isFirstTimeDonor,
              lastDate,
              emergencyPhone,
              if (isFirstTimeDonor) "First-time registered donor" else "Self-registered donor"
            )
            if (success) {
              name = ""
              phone = ""
              area = ""
              emergencyPhone = ""
            }
          },
          enabled = agreementChecked,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BloodRed800,
            contentColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("submit_registration_button")
        ) {
          Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = AppStrings.submitRegistration(currentLanguage),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
