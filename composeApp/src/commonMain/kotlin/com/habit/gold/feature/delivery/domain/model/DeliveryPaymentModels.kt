package com.habit.gold.feature.delivery.domain.model

sealed interface DeliveryPaymentLaunchRequest {
    data class Juspay(
        val payloadJson: String,
        val preferredUpiPackage: String? = null,
    ) : DeliveryPaymentLaunchRequest
}

sealed interface DeliveryPaymentLaunchResult {
    data class Success(val status: String) : DeliveryPaymentLaunchResult
    data class Failure(
        val status: String,
        val message: String,
        val shouldPollOrderStatus: Boolean = false,
    ) : DeliveryPaymentLaunchResult
    data object BackPressed : DeliveryPaymentLaunchResult
}
