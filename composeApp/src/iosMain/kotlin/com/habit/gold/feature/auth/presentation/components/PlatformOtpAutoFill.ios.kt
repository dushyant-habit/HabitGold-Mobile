package com.habit.gold.feature.auth.presentation.components

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformOtpAutoFillEffect(
    enabled: Boolean,
    onOtpReceived: (String) -> Unit,
) = Unit
