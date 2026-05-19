package com.habit.gold.feature.auth.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformOtpAutoFillEffect(
    enabled: Boolean,
    onOtpReceived: (String) -> Unit,
)
