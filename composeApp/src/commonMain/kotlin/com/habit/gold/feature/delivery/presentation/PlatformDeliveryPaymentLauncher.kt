package com.habit.gold.feature.delivery.presentation

import androidx.compose.runtime.Composable
import com.habit.gold.feature.delivery.domain.DeliveryPaymentLauncher

@Composable
expect fun rememberPlatformDeliveryPaymentLauncher(): DeliveryPaymentLauncher
