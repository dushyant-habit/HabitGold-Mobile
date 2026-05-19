package com.habit.gold.feature.delivery.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.habit.gold.BuildConfig
import com.habit.gold.feature.delivery.domain.DeliveryPaymentLauncher
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchRequest
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchResult
import com.habit.gold.payments.juspay.EmbeddedJuspayCheckoutHost
import com.habit.gold.payments.juspay.JuspayPaymentResult
import com.habit.gold.payments.juspay.mergePreferredUpiIntoSdkPayload
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume

@Composable
actual fun rememberPlatformDeliveryPaymentLauncher(): DeliveryPaymentLauncher {
    val context = LocalContext.current
    val pendingContinuationState = remember {
        mutableStateOf<CancellableContinuation<DeliveryPaymentLaunchResult>?>(null)
    }

    DisposableEffect(Unit) {
        onDispose {
            pendingContinuationState.value?.cancel()
            pendingContinuationState.value = null
        }
    }

    return remember(context) {
        object : DeliveryPaymentLauncher {
            override suspend fun launch(request: DeliveryPaymentLaunchRequest): DeliveryPaymentLaunchResult {
                val juspayRequest = request as? DeliveryPaymentLaunchRequest.Juspay
                    ?: return DeliveryPaymentLaunchResult.Failure(
                        status = "unsupported_request",
                        message = "Unsupported payment request for this build.",
                    )

                if (!BuildConfig.JUSPAY_ENABLED) {
                    return DeliveryPaymentLaunchResult.Failure(
                        status = "juspay_disabled",
                        message = "Juspay disabled in this build.",
                    )
                }

                if (BuildConfig.JUSPAY_CLIENT_ID.isBlank() || BuildConfig.JUSPAY_MERCHANT_ID.isBlank()) {
                    return DeliveryPaymentLaunchResult.Failure(
                        status = "juspay_not_configured",
                        message = "Juspay merchant configuration is missing in this build.",
                    )
                }

                val payloadJson = try {
                    val parsedPayload = JSONObject(juspayRequest.payloadJson)
                    mergePreferredUpiIntoSdkPayload(parsedPayload, juspayRequest.preferredUpiPackage)
                } catch (_: Exception) {
                    return DeliveryPaymentLaunchResult.Failure(
                        status = "invalid_payload",
                        message = "Invalid payment payload."
                    )
                }

                return suspendCancellableCoroutine { continuation ->
                    val existingContinuation = pendingContinuationState.value
                    if (existingContinuation != null && existingContinuation.isActive) {
                        continuation.resume(
                            DeliveryPaymentLaunchResult.Failure(
                                status = "payment_in_progress",
                                message = "Another payment is already in progress."
                            )
                        )
                        return@suspendCancellableCoroutine
                    }

                    pendingContinuationState.value = continuation
                    val host = context.findDeliveryCheckoutHost()
                    if (host == null) {
                        pendingContinuationState.value = null
                        continuation.resume(
                            DeliveryPaymentLaunchResult.Failure(
                                status = "payment_host_unavailable",
                                message = "Payment host unavailable."
                            )
                        )
                        return@suspendCancellableCoroutine
                    }

                    val started = host.startEmbeddedJuspayCheckout(payloadJson) { result ->
                        val currentContinuation = pendingContinuationState.value ?: return@startEmbeddedJuspayCheckout
                        pendingContinuationState.value = null
                        val mapped = result.toDeliveryResult()
                        currentContinuation.resume(mapped)
                    }

                    if (!started) {
                        pendingContinuationState.value = null
                        continuation.resume(
                            DeliveryPaymentLaunchResult.Failure(
                                status = "checkout_start_failed",
                                message = "Could not start embedded Juspay checkout."
                            )
                        )
                        return@suspendCancellableCoroutine
                    }

                    continuation.invokeOnCancellation {
                        if (pendingContinuationState.value === continuation) {
                            pendingContinuationState.value = null
                        }
                    }
                }
            }
        }
    }
}

private fun Context.findDeliveryCheckoutHost(): EmbeddedJuspayCheckoutHost? {
    var current: Context? = this
    while (current != null) {
        if (current is EmbeddedJuspayCheckoutHost) return current
        current = (current as? ContextWrapper)?.baseContext
    }
    return null
}

private fun JuspayPaymentResult.toDeliveryResult(): DeliveryPaymentLaunchResult {
    return when (this) {
        is JuspayPaymentResult.Success -> DeliveryPaymentLaunchResult.Success(status)
        is JuspayPaymentResult.Failure -> DeliveryPaymentLaunchResult.Failure(
            status = status,
            message = message,
            shouldPollOrderStatus = true
        )
        JuspayPaymentResult.BackPressed -> DeliveryPaymentLaunchResult.BackPressed
    }
}
