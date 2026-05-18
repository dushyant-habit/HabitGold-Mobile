package com.habit.gold.feature.delivery.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto

sealed interface DeliveryTrackingState : MviState {
    data object Loading : DeliveryTrackingState
    data class Success(val orders: List<DeliveryOrderDto>) : DeliveryTrackingState
    data class Error(val message: String) : DeliveryTrackingState
}

sealed interface DeliveryTrackingIntent : MviIntent {
    data object FetchOrders : DeliveryTrackingIntent
    data object Refresh : DeliveryTrackingIntent
}

sealed interface DeliveryTrackingEffect : MviEffect {
    data class ShowError(val message: String) : DeliveryTrackingEffect
}
