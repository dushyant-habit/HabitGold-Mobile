package com.habit.gold.feature.trade.domain.model

enum class TradePaymentContext {
    BuyOneTime,
    BuySipSetup,
    BuySipUpgrade,
}

sealed interface TradePaymentLaunchRequest {
    data class Juspay(
        val payloadJson: String,
        val context: TradePaymentContext,
        val preferredUpiPackage: String? = null,
    ) : TradePaymentLaunchRequest
}

sealed interface TradePaymentLaunchResult {
    data class Success(val status: String) : TradePaymentLaunchResult
    data class Failure(
        val status: String,
        val message: String,
        val shouldPollOrderStatus: Boolean = false,
    ) : TradePaymentLaunchResult
    data object BackPressed : TradePaymentLaunchResult
}
