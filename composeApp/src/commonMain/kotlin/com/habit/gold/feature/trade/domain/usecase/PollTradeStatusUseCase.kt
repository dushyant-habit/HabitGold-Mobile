package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradePollingOutcome
import com.habit.gold.feature.trade.domain.model.TradePollingPolicy
import com.habit.gold.feature.trade.domain.model.TradePollingSnapshot
import com.habit.gold.feature.trade.domain.model.TradePollingTimeoutOutcome
import kotlinx.coroutines.delay

class PollTradeStatusUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(
        orderId: String,
        policy: TradePollingPolicy,
    ): ApiResult<TradePollingOutcome> {
        var latestSnapshot: TradePollingSnapshot? = null

        repeat(policy.maxAttempts) { index ->
            val attempt = index + 1
            when (val statusResult = repository.getTradeStatus(orderId)) {
                is ApiResult.Failure -> {
                    if (attempt == policy.maxAttempts) return statusResult
                }
                is ApiResult.Success -> {
                    val normalizedStatus = statusResult.value.status.uppercase()
                    latestSnapshot = TradePollingSnapshot(
                        orderId = statusResult.value.orderId,
                        status = normalizedStatus,
                        attempt = attempt,
                    )

                    if (normalizedStatus in policy.successStatuses) {
                        return ApiResult.Success(TradePollingOutcome.Success(latestSnapshot))
                    }
                    if (normalizedStatus in policy.failureStatuses) {
                        return ApiResult.Success(
                            TradePollingOutcome.Failure(
                                snapshot = latestSnapshot,
                                message = statusResult.value.message ?: "Trade request failed.",
                            )
                        )
                    }
                    if (normalizedStatus !in policy.nonTerminalStatuses) {
                        return ApiResult.Success(
                            TradePollingOutcome.Failure(
                                snapshot = latestSnapshot,
                                message = statusResult.value.message ?: "Unexpected order status: $normalizedStatus",
                            )
                        )
                    }
                }
            }

            if (attempt < policy.maxAttempts) {
                delay(policy.intervalMillis)
            }
        }

        val timeoutOutcome = when (policy.timeoutOutcome) {
            TradePollingTimeoutOutcome.Processing -> TradePollingOutcome.Processing(latestSnapshot)
            TradePollingTimeoutOutcome.Pending -> TradePollingOutcome.Pending(latestSnapshot)
        }
        return ApiResult.Success(timeoutOutcome)
    }
}
