package com.habit.gold.feature.delivery.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.habit.gold.feature.delivery.domain.DeliveryPaymentLauncher
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchRequest
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchResult
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUUID
import kotlin.coroutines.resume

private const val DeliveryPaymentLaunchNotificationName = "HabitGoldDeliveryPaymentLaunch"
private const val DeliveryPaymentRequestIdKey = "requestId"
private const val DeliveryPaymentPayloadJsonKey = "payloadJson"
private const val DeliveryPaymentUpiPackageKey = "preferredUpiPackage"

@Composable
actual fun rememberPlatformDeliveryPaymentLauncher(): DeliveryPaymentLauncher {
    return remember {
        object : DeliveryPaymentLauncher {
            override suspend fun launch(request: DeliveryPaymentLaunchRequest): DeliveryPaymentLaunchResult {
                val juspayRequest = request as? DeliveryPaymentLaunchRequest.Juspay
                    ?: return DeliveryPaymentLaunchResult.Failure(
                        status = "unsupported_request",
                        message = "Unsupported payment request for this build.",
                        shouldPollOrderStatus = false
                    )
                return IosDeliveryPaymentBridge.launch(juspayRequest)
            }
        }
    }
}

private object IosDeliveryPaymentBridge {
    private var pendingRequestId: String? = null
    private var pendingContinuation: kotlinx.coroutines.CancellableContinuation<DeliveryPaymentLaunchResult>? = null

    suspend fun launch(request: DeliveryPaymentLaunchRequest.Juspay): DeliveryPaymentLaunchResult {
        return suspendCancellableCoroutine { continuation ->
            val existing = pendingContinuation
            if (existing != null && existing.isActive) {
                continuation.resume(
                    DeliveryPaymentLaunchResult.Failure(
                        status = "payment_in_progress",
                        message = "Another delivery payment is in progress.",
                        shouldPollOrderStatus = false
                    )
                )
                return@suspendCancellableCoroutine
            }

            val requestId = NSUUID().UUIDString()
            pendingRequestId = requestId
            pendingContinuation = continuation

            val userInfo = buildMap<Any?, Any?> {
                put(DeliveryPaymentRequestIdKey, requestId)
                put(DeliveryPaymentPayloadJsonKey, request.payloadJson)
                if (request.preferredUpiPackage != null) {
                    put(DeliveryPaymentUpiPackageKey, request.preferredUpiPackage)
                }
            }

            NSNotificationCenter.defaultCenter.postNotificationName(
                DeliveryPaymentLaunchNotificationName,
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
        complete(requestId, DeliveryPaymentLaunchResult.Success(status = status))
    }

    fun completeFailure(requestId: String, status: String, message: String) {
        complete(
            requestId,
            DeliveryPaymentLaunchResult.Failure(
                status = status,
                message = message,
                shouldPollOrderStatus = true
            )
        )
    }

    fun completeBackPressed(requestId: String) {
        complete(requestId, DeliveryPaymentLaunchResult.BackPressed)
    }

    private fun complete(requestId: String, result: DeliveryPaymentLaunchResult) {
        if (pendingRequestId != requestId) return
        val continuation = pendingContinuation ?: return
        pendingContinuation = null
        pendingRequestId = null
        continuation.resume(result)
    }
}

/**
 * Public API exposed to Swift for completing a Delivery payment.
 *
 * Called by AppDelegate or SceneDelegate after Juspay checkout completes.
 */
object IosDeliveryPaymentBridgeApi {
    fun completeSuccess(requestId: String, status: String) {
        IosDeliveryPaymentBridge.completeSuccess(requestId, status)
    }

    fun completeFailure(requestId: String, status: String, message: String) {
        IosDeliveryPaymentBridge.completeFailure(requestId, status, message)
    }

    fun completeBackPressed(requestId: String) {
        IosDeliveryPaymentBridge.completeBackPressed(requestId)
    }
}
