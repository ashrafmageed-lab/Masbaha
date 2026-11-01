package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MarianBlueLight,
    onPrimary = NightNavyDark,
    primaryContainer = MarianBlueContainerDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = SacredGoldBright,
    onSecondary = NightNavyDark,
    secondaryContainer = SacredGoldDark,
    onSecondaryContainer = SacredGoldContainer,
    background = NightNavyDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = MarianBluePrimary,
    onPrimary = SurfaceLight,
    primaryContainer = MarianBlueContainerLight,
    onPrimaryContainer = MarianBlueDark,
    secondary = SacredGold,
    onSecondary = SurfaceLight,
    secondaryContainer = SacredGoldContainer,
    onSecondaryContainer = SacredGoldDark,
    background = ParchmentLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceCardLight,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun RosaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
