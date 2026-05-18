package com.habit.gold.feature.delivery.domain

import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchRequest
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchResult

/**
 * Launched from the UI to start a platform-specific checkout (Juspay / etc).
 */
interface DeliveryPaymentLauncher {
    suspend fun launch(request: DeliveryPaymentLaunchRequest): DeliveryPaymentLaunchResult
}
