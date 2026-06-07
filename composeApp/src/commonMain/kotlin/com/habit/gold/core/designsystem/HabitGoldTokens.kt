package com.habit.gold.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object HabitGoldPalette {
    val plum = Color(0xFF7B2CBF)
    val plumDeep = Color(0xFF5A189A)
    val plumSoft = Color(0xFFF2E7FB)
    val splashBackground = Color(0xFF441C67)
    val orchid = Color(0xFFEDE9FE)
    val ink = Color(0xFF111827)
    val slate = Color(0xFF6B7280)
    val mist = Color(0xFFE5E7EB)
    val cloud = Color(0xFFF8FAFC)
    val surfaceTint = Color(0xFFF7F2FC)
    val successSurface = Color(0xFFEDE9FE)
    val danger = Color(0xFFD92D20)
    val gold = Color(0xFFF5C518)
    val goldSoft = Color(0xFFFFE99B)
    val white = Color(0xFFFFFFFF)
    val transparent = Color(0x00000000)
    val night = Color(0xFF100A17)
    val nightSurface = Color(0xFF17111F)
    val nightMuted = Color(0xFFD1D5DB)
}

internal val HabitGoldLightColors = lightColorScheme(
    primary = HabitGoldPalette.plum,
    onPrimary = HabitGoldPalette.white,
    primaryContainer = HabitGoldPalette.plumSoft,
    onPrimaryContainer = HabitGoldPalette.plumDeep,
    secondary = Color(0xFF5B5F97),
    onSecondary = HabitGoldPalette.white,
    secondaryContainer = Color(0xFFE8EAFD),
    onSecondaryContainer = Color(0xFF1A1D33),
    tertiary = Color(0xFF7C3AED),
    onTertiary = HabitGoldPalette.white,
    tertiaryContainer = HabitGoldPalette.orchid,
    onTertiaryContainer = Color(0xFF23103C),
    background = HabitGoldPalette.white,
    onBackground = HabitGoldPalette.ink,
    surface = HabitGoldPalette.white,
    onSurface = HabitGoldPalette.ink,
    surfaceVariant = HabitGoldPalette.surfaceTint,
    onSurfaceVariant = HabitGoldPalette.slate,
    outline = HabitGoldPalette.mist,
    error = HabitGoldPalette.danger,
    onError = HabitGoldPalette.white,
)

internal val HabitGoldDarkColors = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF3E1168),
    primaryContainer = HabitGoldPalette.plumDeep,
    onPrimaryContainer = HabitGoldPalette.plumSoft,
    background = HabitGoldPalette.night,
    onBackground = HabitGoldPalette.plumSoft,
    surface = HabitGoldPalette.nightSurface,
    onSurface = HabitGoldPalette.plumSoft,
    surfaceVariant = Color(0xFF2B2238),
    onSurfaceVariant = HabitGoldPalette.nightMuted,
    outline = Color(0xFF4C445A),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

data class HabitGoldSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 40.dp,
)

data class HabitGoldCornerRadii(
    val sm: RoundedCornerShape = RoundedCornerShape(14.dp),
    val md: RoundedCornerShape = RoundedCornerShape(18.dp),
    val lg: RoundedCornerShape = RoundedCornerShape(24.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(999.dp),
)

data class HabitGoldElevation(
    val card: Dp = 0.dp,
    val button: Dp = 14.dp,
)

data class HabitGoldGradientSet(
    val brandVertical: Brush = Brush.verticalGradient(
        colors = listOf(HabitGoldPalette.plum, HabitGoldPalette.plumDeep),
    ),
    val brandLinear: Brush = Brush.linearGradient(
        colors = listOf(HabitGoldPalette.plum, HabitGoldPalette.plumDeep),
    ),
)

data class HabitGoldTypographyScale(
    val fontFamily: FontFamily = FontFamily.SansSerif,
    val display: TextStyle = tokenTextStyle(fontFamily, 36.sp, 42.sp, FontWeight.ExtraBold, -0.8.sp),
    val headline: TextStyle = tokenTextStyle(fontFamily, 28.sp, 34.sp, FontWeight.ExtraBold),
    val title: TextStyle = tokenTextStyle(fontFamily, 20.sp, 26.sp, FontWeight.Bold),
    val body: TextStyle = tokenTextStyle(fontFamily, 16.sp, 24.sp, FontWeight.Normal),
    val bodyStrong: TextStyle = tokenTextStyle(fontFamily, 16.sp, 24.sp, FontWeight.SemiBold),
    val label: TextStyle = tokenTextStyle(fontFamily, 13.sp, 18.sp, FontWeight.Bold, 0.6.sp),
    val caption: TextStyle = tokenTextStyle(fontFamily, 12.sp, 18.sp, FontWeight.Normal),
)

private fun tokenTextStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    fontWeight: FontWeight,
    letterSpacing: TextUnit = 0.sp,
): TextStyle {
    return TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
    )
}

internal val LocalHabitGoldSpacing = staticCompositionLocalOf { HabitGoldSpacing() }
internal val LocalHabitGoldRadii = staticCompositionLocalOf { HabitGoldCornerRadii() }
internal val LocalHabitGoldElevation = staticCompositionLocalOf { HabitGoldElevation() }
internal val LocalHabitGoldGradients = staticCompositionLocalOf { HabitGoldGradientSet() }
internal val LocalHabitGoldTypographyScale = staticCompositionLocalOf { HabitGoldTypographyScale() }
