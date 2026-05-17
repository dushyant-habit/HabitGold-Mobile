package com.habit.gold.core.presentation

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // No-op on iOS. Native navigation/back gestures are handled by the host platform.
}
