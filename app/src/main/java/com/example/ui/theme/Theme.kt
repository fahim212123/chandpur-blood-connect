package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = BloodRedAccent,
  onPrimary = PureWhite,
  primaryContainer = BloodRed900,
  onPrimaryContainer = BloodRedContainer,
  secondary = BloodRed500,
  onSecondary = PureWhite,
  secondaryContainer = BloodRed800,
  onSecondaryContainer = PureWhite,
  background = Color(0xFF141212),
  surface = Color(0xFF1C1919),
  onBackground = PureWhite,
  onSurface = PureWhite,
  surfaceVariant = Color(0xFF2B2525),
  onSurfaceVariant = Color(0xFFE0D5D5),
  outline = Color(0xFF534343)
)

private val LightColorScheme = lightColorScheme(
  primary = BloodRed800,
  onPrimary = PureWhite,
  primaryContainer = BloodRedContainer,
  onPrimaryContainer = BloodRed900,
  secondary = BloodRed600,
  onSecondary = PureWhite,
  secondaryContainer = BloodRedBorder,
  onSecondaryContainer = BloodRed900,
  tertiary = BloodRedAccent,
  background = SurfaceSnow,
  surface = PureWhite,
  onBackground = TextDark,
  onSurface = TextDark,
  surfaceVariant = SurfaceOffWhite,
  onSurfaceVariant = TextMuted,
  outline = OutlineSoft,
  outlineVariant = BloodRedBorder
)

@Composable
fun BloodDonationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // We keep false by default so our signature crimson & white palette is prominently showcased
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
