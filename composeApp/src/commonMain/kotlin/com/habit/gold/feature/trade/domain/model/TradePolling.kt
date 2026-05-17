package com.habit.gold.feature.trade.domain.model

enum class TradePollingTimeoutOutcome {
    Processing,
    Pending,
}

data class TradePollingPolicy(
    val intervalMillis: Long,
    val maxAttempts: Int,
    val successStatuses: Set<String>,
    val failureStatuses: Set<String>,
    val nonTerminalStatuses: Set<String>,
    val timeoutOutcome: TradePollingTimeoutOutcome,
)

data class TradePollingSnapshot(
    val orderId: String,
    val status: String,
    val attempt: Int,
)

sealed interface TradePollingOutcome {
    data class Success(val snapshot: TradePollingSnapshot) : TradePollingOutcome
    data class Failure(val snapshot: TradePollingSnapshot, val message: String) : TradePollingOutcome
    data class Processing(val snapshot: TradePollingSnapshot?) : TradePollingOutcome
    data class Pending(val snapshot: TradePollingSnapshot?) : TradePollingOutcome
}

object TradePollingPolicies {
    fun buy(): TradePollingPolicy {
        return TradePollingPolicy(
            intervalMillis = 5_000L,
            maxAttempts = 6,
            successStatuses = setOf("COMPLETED", "SUCCESS"),
            failureStatuses = setOf("FAILED"),
            nonTerminalStatuses = setOf(
                "PENDING",
                "PAYMENT_RECEIVED",
                "GOLD_BUY_FAILED",
                "PAYOUT_PROCESSING",
            ),
            timeoutOutcome = TradePollingTimeoutOutcome.Processing,
        )
    }

    fun sell(): TradePollingPolicy {
        return TradePollingPolicy(
            intervalMillis = 5_000L,
            maxAttempts = 2,
            successStatuses = setOf("COMPLETED", "SUCCESS"),
            failureStatuses = setOf("FAILED"),
            nonTerminalStatuses = setOf("PENDING", "PAYMENT_RECEIVED", "PAYOUT_PROCESSING"),
            timeoutOutcome = TradePollingTimeoutOutcome.Pending,
        )
    }
}

