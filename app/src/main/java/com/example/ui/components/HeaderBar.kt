package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.BloodRed800
import com.example.ui.theme.BloodRed900
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.PureWhite

import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.IconButton

@Composable
fun HeaderBar(
  currentLanguage: AppLanguage,
  onToggleLanguage: () -> Unit,
  currentUserEmail: String? = null,
  onSignOut: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val isBengali = currentLanguage == AppLanguage.BN

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(BloodRed800)
      .statusBarsPadding()
  ) {
    // Top Bar with Brand & Language Toggle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Red Drop in White Circle
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(PureWhite),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = "LifeLink CP Logo",
            tint = BloodRed800,
            modifier = Modifier.size(26.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = AppStrings.appName(currentLanguage),
              color = PureWhite,
              fontSize = 21.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = PureWhite.copy(alpha = 0.22f)
            ) {
              Text(
                text = "BD",
                color = PureWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          // Official Tagline
          Text(
            text = AppStrings.appTagline(currentLanguage),
            color = PureWhite.copy(alpha = 0.92f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp
          )

          // Area subtitle
          Text(
            text = if (isBengali) "চাঁদপুর সদর ও ফরিদগঞ্জ" else "Chandpur Sadar & Faridganj",
            color = PureWhite.copy(alpha = 0.78f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Language Switcher Pill
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = PureWhite.copy(alpha = 0.18f),
          modifier = Modifier
            .testTag("language_toggle_button")
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggleLanguage() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Switch Language",
              tint = PureWhite,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = if (isBengali) "English" else "বাংলা",
              color = PureWhite,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        if (onSignOut != null) {
          Spacer(modifier = Modifier.width(6.dp))
          IconButton(
            onClick = onSignOut,
            modifier = Modifier
              .size(34.dp)
              .testTag("header_sign_out_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = "Sign Out",
              tint = PureWhite,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // Demo Data Ribbon
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(BloodRed900)
        .padding(horizontal = 16.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.Info,
        contentDescription = "Demo Information",
        tint = PureWhite.copy(alpha = 0.9f),
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = AppStrings.demoBanner(currentLanguage),
        color = PureWhite.copy(alpha = 0.95f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
