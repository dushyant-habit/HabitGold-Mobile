package com.habit.gold.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.sp

object HabitGoldDesignSystem {
    val spacing: HabitGoldSpacing
        @Composable get() = LocalHabitGoldSpacing.current

    val radii: HabitGoldCornerRadii
        @Composable get() = LocalHabitGoldRadii.current

    val elevations: HabitGoldElevation
        @Composable get() = LocalHabitGoldElevation.current

    val gradients: HabitGoldGradientSet
        @Composable get() = LocalHabitGoldGradients.current

    val typeScale: HabitGoldTypographyScale
        @Composable get() = LocalHabitGoldTypographyScale.current
}

@Composable
fun HabitGoldTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val spacing = HabitGoldSpacing()
    val radii = HabitGoldCornerRadii()
    val elevations = HabitGoldElevation()
    val gradients = HabitGoldGradientSet()
    val typeScale = HabitGoldTypographyScale()

    CompositionLocalProvider(
        LocalHabitGoldSpacing provides spacing,
        LocalHabitGoldRadii provides radii,
        LocalHabitGoldElevation provides elevations,
        LocalHabitGoldGradients provides gradients,
        LocalHabitGoldTypographyScale provides typeScale,
    ) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) HabitGoldDarkColors else HabitGoldLightColors,
            typography = Typography(
                displaySmall = typeScale.display,
                headlineMedium = typeScale.headline,
                headlineSmall = typeScale.title.copy(fontSize = 24.sp, lineHeight = 30.sp),
                titleLarge = typeScale.title,
                titleMedium = typeScale.bodyStrong,
                bodyLarge = typeScale.body,
                bodyMedium = typeScale.body.copy(fontSize = 15.sp, lineHeight = 22.sp),
                bodySmall = typeScale.caption,
                labelLarge = typeScale.label,
                labelMedium = typeScale.label.copy(fontSize = 12.sp),
            ),
            content = content,
        )
    }
}
