package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = RedLight,
    onPrimary = PureWhite,
    primaryContainer = RedDark,
    onPrimaryContainer = PureWhite,
    background = PureWhite,
    surface = SlateSurface50,
    onBackground = TextDark,
    onSurface = TextDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RedPrimary,
    onPrimary = PureWhite,
    primaryContainer = RedContainer,
    onPrimaryContainer = RedDark,
    secondary = RedDark,
    onSecondary = PureWhite,
    tertiary = RedLight,
    background = PureWhite,
    surface = PureWhite,
    surfaceVariant = SlateSurface50,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder200,
    outlineVariant = SlateBorder100
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

