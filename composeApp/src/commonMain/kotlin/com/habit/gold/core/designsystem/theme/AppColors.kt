package com.habit.gold.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens shared across all features.
 * Screens import via `com.habit.gold.core.designsystem.theme.*`.
 */
object AppColors {
    val Black = Color(0xFF111827)
    val White = Color(0xFFFFFFFF)

    // Slate scale
    val Slate10  = Color(0xFFF9FAFB)
    val Slate50  = Color(0xFFF8FAFC)
    val Slate100 = Color(0xFFF1F5F9)
    val Slate125 = Color(0xFFEBF0F5)   // between 100 and 200
    val Slate200 = Color(0xFFE2E8F0)
    val Slate200Alt = Color(0xFFDDE3EC) // slightly darker slate200
    val Slate300 = Color(0xFFCBD5E1)
    val Slate400 = Color(0xFF94A3B8)
    val Slate500 = Color(0xFF64748B)
    val Slate600 = Color(0xFF475569)
    val Slate700 = Color(0xFF334155)
    val Slate800 = Color(0xFF1E293B)
    val Slate900 = Color(0xFF0F172A)
    val Slate950 = Color(0xFF020617)

    // Neutral scale
    val Neutral25  = Color(0xFFFCFCFD)
    val Neutral400 = Color(0xFF98A2B3)

    // Gold / Amber / Yellow scale
    val Gold50    = Color(0xFFFFFBEB)
    val Gold100   = Color(0xFFFEF3C7)
    val Gold200   = Color(0xFFFDE68A)
    val Gold400   = Color(0xFFFBBF24)
    val Gold500   = Color(0xFFF59E0B)
    val Gold600   = Color(0xFFD97706)
    val Yellow400 = Color(0xFFFACC15)

    val Amber50   = Color(0xFFFFFBEB)
    val Amber200  = Color(0xFFFDE68A)
    val Amber800  = Color(0xFF92400E)
    val Amber900  = Color(0xFF78350F)

    // Orange scale
    val Orange700 = Color(0xFFC2410C)

    // Purple / Brand scale
    val Purple50    = Color(0xFFFAF5FF)
    val Purple100   = Color(0xFFF3E8FF)
    val Purple200   = Color(0xFFE9D5FF)
    val Purple300   = Color(0xFFD8B4FE)
    val Purple400   = Color(0xFFA855F7)
    val Purple500   = Color(0xFF9C5DD9)
    val Purple600   = Color(0xFF7C3AED)
    val Purple700   = Color(0xFF7B2CBF)
    val Purple800   = Color(0xFF5A189A)
    val Purple900   = Color(0xFF4C1D95)
    val Purple950   = Color(0xFF2E1065)
    val Purple40Tone = Color(0xFFF2E7FB)

    // Violet scale (alias to purple spectrum for vibrant brand use)
    val Violet600 = Color(0xFF7C3AED)

    // PurpleGray (neutral purple-tinted grays)
    val PurpleGray600 = Color(0xFF6B7280)
    val PurpleGray700 = Color(0xFF374151)

    // Green scale
    val Green50  = Color(0xFFF0FDF4)
    val Green100 = Color(0xFFDCFCE7)
    val Green200 = Color(0xFFBBF7D0)
    val Green500 = Color(0xFF22C55E)
    val Green600 = Color(0xFF16A34A)
    val Green700 = Color(0xFF15803D)
    val Green800 = Color(0xFF166534)

    // Red / Error scale
    val Red50  = Color(0xFFFFF1F2)
    val Red100 = Color(0xFFFFE4E6)
    val Red500 = Color(0xFFEF4444)
    val Red600 = Color(0xFFDC2626)
    val Red700 = Color(0xFFB91C1C)
    val Danger = Color(0xFFD92D20)

    // Surface / neutral backgrounds
    val SurfaceLight = Color(0xFFF8FAFC)
    val SurfaceTint  = Color(0xFFF7F2FC)
    val Divider      = Color(0xFFE5E7EB)

    // Brand aliases
    /** Maps to Purple700 — the app primary brand colour. */
    val Primary = Purple700
}
