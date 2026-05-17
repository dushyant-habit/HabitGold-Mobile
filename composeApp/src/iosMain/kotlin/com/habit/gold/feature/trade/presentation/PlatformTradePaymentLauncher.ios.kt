package com.habit.gold.feature.trade.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.habit.gold.feature.trade.domain.TradePaymentLauncher
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUUID
import kotlin.coroutines.resume

private const val TradePaymentLaunchNotificationName = "HabitGoldTradePaymentLaunch"
private const val TradePaymentRequestIdKey = "requestId"
private const val TradePaymentPayloadJsonKey = "payloadJson"
private const val TradePaymentContextKey = "context"
private const val TradePaymentPreferredUpiPackageKey = "preferredUpiPackage"

@Composable
actual fun rememberPlatformTradePaymentLauncher(): TradePaymentLauncher {
    return remember {
        object : TradePaymentLauncher {
            override suspend fun launch(request: TradePaymentLaunchRequest): TradePaymentLaunchResult {
                val juspayRequest = request as? TradePaymentLaunchRequest.Juspay
                    ?: return TradePaymentLaunchResult.Failure(
                        status = "unsupported_request",
                        message = "Unsupported payment request for this build.",
                    )
                return IosTradePaymentBridge.launch(juspayRequest)
            }
        }
    }
}

private object IosTradePaymentBridge {
    private var pendingRequestId: String? = null
    private var pendingContinuation: CancellableContinuation<TradePaymentLaunchResult>? = null

    suspend fun launch(request: TradePaymentLaunchRequest.Juspay): TradePaymentLaunchResult {
        return suspendCancellableCoroutine { continuation ->
            val existingContinuation = pendingContinuation
            if (existingContinuation != null && existingContinuation.isActive) {
                continuation.resume(
                    TradePaymentLaunchResult.Failure(
                        status = "payment_in_progress",
                        message = "Another payment is already in progress.",
                    ),
                )
                return@suspendCancellableCoroutine
            }

            val requestId = NSUUID().UUIDString()
            pendingRequestId = requestId
            pendingContinuation = continuation

            val userInfo = buildMap<Any?, Any?> {
                put(TradePaymentRequestIdKey, requestId)
                put(TradePaymentPayloadJsonKey, request.payloadJson)
                put(TradePaymentContextKey, request.context.name)
                request.preferredUpiPackage?.takeIf { it.isNotBlank() }?.let {
                    put(TradePaymentPreferredUpiPackageKey, it)
                }
            }

            NSNotificationCenter.defaultCenter.postNotificationName(
                TradePaymentLaunchNotificationName,
                null,
                userInfo,
            )

            continuation.invokeOnCancellation {
                if (pendingContinuation === continuation) {
                    pendingContinuation = null
                    pendingRequestId = null
                }
            }
        }
    }

    fun completeSuccess(requestId: String, status: String) {
        complete(
            requestId = requestId,
            result = TradePaymentLaunchResult.Success(status = status),
        )
    }

    fun completeFailure(
        requestId: String,
        status: String,
        message: String,
        shouldPollOrderStatus: Boolean,
    ) {
        complete(
            requestId = requestId,
            result = TradePaymentLaunchResult.Failure(
                status = status,
                message = message,
                shouldPollOrderStatus = shouldPollOrderStatus,
            ),
        )
    }

    fun completeBackPressed(requestId: String) {
        complete(requestId = requestId, result = TradePaymentLaunchResult.BackPressed)
    }

    private fun complete(
        requestId: String,
        result: TradePaymentLaunchResult,
    ) {
        if (pendingRequestId != requestId) return
        val continuation = pendingContinuation ?: return
        pendingContinuation = null
        pendingRequestId = null
        continuation.resume(result)
    }
}

object IosTradePaymentBridgeApi {
    fun completeSuccess(requestId: String, status: String) {
        IosTradePaymentBridge.completeSuccess(requestId = requestId, status = status)
    }

    fun completeFailure(
        requestId: String,
        status: String,
        message: String,
        shouldPollOrderStatus: Boolean,
    ) {
        IosTradePaymentBridge.completeFailure(
            requestId = requestId,
            status = status,
            message = message,
            shouldPollOrderStatus = shouldPollOrderStatus,
        )
    }

    fun completeBackPressed(requestId: String) {
        IosTradePaymentBridge.completeBackPressed(requestId = requestId)
    }
}
