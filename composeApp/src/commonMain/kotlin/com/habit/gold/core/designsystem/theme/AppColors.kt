package com.habit.gold.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import com.habit.gold.core.designsystem.HabitGoldPalette

/**
 * Delivery-specific semantic aliases derived from the existing app design tokens.
 *
 * This keeps the delivery flow aligned with the established HabitGold palette instead of
 * introducing another raw-color system with parallel brand definitions.
 */
object AppColors {
    val Black = HabitGoldPalette.ink
    val White = HabitGoldPalette.white

    val Slate10 = Color(0xFFF9FAFB)
    val Slate50 = HabitGoldPalette.cloud
    val Slate100 = Color(0xFFF1F5F9)
    val Slate125 = Color(0xFFEBF0F5)
    val Slate200 = Color(0xFFE2E8F0)
    val Slate200Alt = Color(0xFFDDE3EC)
    val Slate300 = Color(0xFFCBD5E1)
    val Slate400 = Color(0xFF94A3B8)
    val Slate500 = Color(0xFF64748B)
    val Slate600 = Color(0xFF475569)
    val Slate700 = Color(0xFF334155)
    val Slate800 = Color(0xFF1E293B)
    val Slate900 = Color(0xFF0F172A)
    val Slate950 = Color(0xFF020617)

    val Neutral25 = Color(0xFFFCFCFD)
    val Neutral400 = Color(0xFF98A2B3)

    val Gold50 = Color(0xFFFFFBEB)
    val Gold100 = Color(0xFFFEF3C7)
    val Gold600 = Color(0xFFD97706)
    val Yellow400 = Color(0xFFFACC15)

    val Amber50 = Gold50
    val Amber200 = Color(0xFFFDE68A)
    val Amber800 = Color(0xFF92400E)
    val Amber900 = Color(0xFF78350F)

    val Orange700 = Color(0xFFC2410C)

    val Purple50 = Color(0xFFFAF5FF)
    val Purple100 = HabitGoldPalette.plumSoft
    val Purple200 = Color(0xFFE9D5FF)
    val Purple300 = Color(0xFFD8B4FE)
    val Purple700 = HabitGoldPalette.plum
    val Purple900 = Color(0xFF4C1D95)
    val Purple950 = Color(0xFF2E1065)
    val Violet600 = Color(0xFF7C3AED)
    val PurpleGray600 = HabitGoldPalette.slate
    val PurpleGray700 = Color(0xFF374151)

    val Green50 = Color(0xFFF0FDF4)
    val Green100 = Color(0xFFDCFCE7)
    val Green200 = Color(0xFFBBF7D0)
    val Green600 = Color(0xFF16A34A)
    val Green700 = Color(0xFF15803D)
    val Green800 = Color(0xFF166534)

    val Red100 = Color(0xFFFFE4E6)
    val Red600 = Color(0xFFDC2626)
    val Red700 = Color(0xFFB91C1C)
    val Danger = HabitGoldPalette.danger

    val SurfaceLight = Slate50
    val Divider = HabitGoldPalette.mist
    val Primary = HabitGoldPalette.plum
}
