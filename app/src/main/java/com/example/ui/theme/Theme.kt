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

private val DarkColorScheme = darkColorScheme(
    primary = GatePrimaryDark,
    onPrimary = GateOnPrimaryDark,
    primaryContainer = GatePrimaryContainerDark,
    onPrimaryContainer = GateOnPrimaryContainerDark,
    secondary = GateSecondaryDark,
    onSecondary = GateOnSecondaryDark,
    secondaryContainer = GateSecondaryContainerDark,
    onSecondaryContainer = GateOnSecondaryContainerDark,
    tertiary = GateTertiaryDark,
    onTertiary = GateOnTertiaryDark,
    tertiaryContainer = GateTertiaryContainerDark,
    onTertiaryContainer = GateOnTertiaryContainerDark,
    background = GateBackgroundDark,
    surface = GateSurfaceDark,
    surfaceVariant = GateSurfaceVariantDark,
    outline = GateOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = GatePrimaryLight,
    onPrimary = GateOnPrimaryLight,
    primaryContainer = GatePrimaryContainerLight,
    onPrimaryContainer = GateOnPrimaryContainerLight,
    secondary = GateSecondaryLight,
    onSecondary = GateOnSecondaryLight,
    secondaryContainer = GateSecondaryContainerLight,
    onSecondaryContainer = GateOnSecondaryContainerLight,
    tertiary = GateTertiaryLight,
    onTertiary = GateOnTertiaryLight,
    tertiaryContainer = GateTertiaryContainerLight,
    onTertiaryContainer = GateOnTertiaryContainerLight,
    background = GateBackgroundLight,
    surface = GateSurfaceLight,
    surfaceVariant = GateSurfaceVariantLight,
    outline = GateOutlineLight
)

@Composable
fun GateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

