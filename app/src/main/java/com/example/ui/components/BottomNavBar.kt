package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.BloodRed800
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AppScreen

@Composable
fun BottomNavBar(
  currentScreen: AppScreen,
  onScreenSelect: (AppScreen) -> Unit,
  currentLanguage: AppLanguage,
  pendingCount: Int,
  isUserSignedIn: Boolean = false,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.navigationBarsPadding(),
    containerColor = PureWhite,
    tonalElevation = 4.dp
  ) {
    // Tab 1: Donors
    NavigationBarItem(
      selected = currentScreen == AppScreen.DONOR_LIST,
      onClick = { onScreenSelect(AppScreen.DONOR_LIST) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.DONOR_LIST) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
          contentDescription = AppStrings.tabDonors(currentLanguage)
        )
      },
      label = {
        Text(
          text = AppStrings.tabDonors(currentLanguage),
          fontSize = 12.sp,
          fontWeight = if (currentScreen == AppScreen.DONOR_LIST) FontWeight.Bold else FontWeight.Medium
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BloodRed800,
        selectedTextColor = BloodRed800,
        indicatorColor = BloodRedContainer,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_tab_donors")
    )

    // Tab 2: Register
    NavigationBarItem(
      selected = currentScreen == AppScreen.REGISTER,
      onClick = { onScreenSelect(AppScreen.REGISTER) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.REGISTER) Icons.Default.PersonAdd else Icons.Outlined.PersonAdd,
          contentDescription = AppStrings.tabRegister(currentLanguage)
        )
      },
      label = {
        Text(
          text = AppStrings.tabRegister(currentLanguage),
          fontSize = 12.sp,
          fontWeight = if (currentScreen == AppScreen.REGISTER) FontWeight.Bold else FontWeight.Medium
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BloodRed800,
        selectedTextColor = BloodRed800,
        indicatorColor = BloodRedContainer,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_tab_register")
    )

    // Tab 3: Admin
    NavigationBarItem(
      selected = currentScreen == AppScreen.ADMIN,
      onClick = { onScreenSelect(AppScreen.ADMIN) },
      icon = {
        BadgedBox(
          badge = {
            if (pendingCount > 0) {
              Badge(
                containerColor = PendingAmber,
                contentColor = PureWhite
              ) {
                Text(
                  text = if (currentLanguage == AppLanguage.BN)
                    AppStrings.toBengaliDigits(pendingCount.toString())
                  else
                    pendingCount.toString()
                )
              }
            }
          }
        ) {
          Icon(
            imageVector = if (currentScreen == AppScreen.ADMIN) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
            contentDescription = AppStrings.tabAdmin(currentLanguage)
          )
        }
      },
      label = {
        Text(
          text = AppStrings.tabAdmin(currentLanguage),
          fontSize = 12.sp,
          fontWeight = if (currentScreen == AppScreen.ADMIN) FontWeight.Bold else FontWeight.Medium
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BloodRed800,
        selectedTextColor = BloodRed800,
        indicatorColor = BloodRedContainer,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_tab_admin")
    )

    // Tab 4: Login / Account
    val isLoginSelected = currentScreen == AppScreen.LOGIN
    val loginLabel = if (isUserSignedIn) AppStrings.tabAccount(currentLanguage) else AppStrings.tabLogin(currentLanguage)
    NavigationBarItem(
      selected = isLoginSelected,
      onClick = { onScreenSelect(AppScreen.LOGIN) },
      icon = {
        Icon(
          imageVector = if (isLoginSelected) Icons.Default.AccountCircle else Icons.Outlined.AccountCircle,
          contentDescription = loginLabel
        )
      },
      label = {
        Text(
          text = loginLabel,
          fontSize = 12.sp,
          fontWeight = if (isLoginSelected) FontWeight.Bold else FontWeight.Medium
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BloodRed800,
        selectedTextColor = BloodRed800,
        indicatorColor = BloodRedContainer,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_tab_login")
    )
  }
}
