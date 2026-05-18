package com.habit.gold.feature.delivery.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.feature.delivery.domain.usecase.ListDeliveryOrdersUseCase
import kotlinx.coroutines.launch

class DeliveryTrackingViewModel(
    private val listDeliveryOrdersUseCase: ListDeliveryOrdersUseCase
) : MviViewModel<DeliveryTrackingState, DeliveryTrackingIntent, DeliveryTrackingEffect>(DeliveryTrackingState.Loading) {

    init {
        onIntent(DeliveryTrackingIntent.FetchOrders)
    }

    override fun onIntent(intent: DeliveryTrackingIntent) {
        when (intent) {
            is DeliveryTrackingIntent.FetchOrders -> fetchOrders()
            is DeliveryTrackingIntent.Refresh -> fetchOrders(isRefresh = true)
        }
    }

    private fun fetchOrders(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) {
                updateState { DeliveryTrackingState.Loading }
            }
            listDeliveryOrdersUseCase().fold(
                onSuccess = { orders ->
                    updateState { DeliveryTrackingState.Success(orders) }
                },
                onFailure = { error ->
                    val message = error.message ?: "Failed to fetch orders"
                    updateState { DeliveryTrackingState.Error(message) }
                    emitEffect(DeliveryTrackingEffect.ShowError(message))
                }
            )
        }
    }
}
