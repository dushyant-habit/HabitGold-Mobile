package com.habit.gold.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HabitGoldLightColors = lightColorScheme(
    primary = Color(0xFF8D6A15),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF6E4A4),
    onPrimaryContainer = Color(0xFF2A1D00),
    secondary = Color(0xFF4F6370),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD3E8F8),
    onSecondaryContainer = Color(0xFF091E29),
    tertiary = Color(0xFF6A5540),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF5DDC3),
    onTertiaryContainer = Color(0xFF26180A),
    background = Color(0xFFFFFBF2),
    onBackground = Color(0xFF201A12),
    surface = Color(0xFFFFFBF7),
    onSurface = Color(0xFF201B14),
    surfaceVariant = Color(0xFFF0E1CC),
    onSurfaceVariant = Color(0xFF524434),
    outline = Color(0xFF87735C),
)

private val HabitGoldDarkColors = darkColorScheme(
    primary = Color(0xFFF0C95A),
    onPrimary = Color(0xFF4A3700),
    background = Color(0xFF16120E),
    onBackground = Color(0xFFF2E8DB),
    surface = Color(0xFF1F1B16),
    onSurface = Color(0xFFF2E8DB),
    surfaceVariant = Color(0xFF4A4032),
    onSurfaceVariant = Color(0xFFD8C7AF),
)

@Composable
fun HabitGoldTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) HabitGoldDarkColors else HabitGoldLightColors,
        typography = Typography(),
        content = content,
    )
}
