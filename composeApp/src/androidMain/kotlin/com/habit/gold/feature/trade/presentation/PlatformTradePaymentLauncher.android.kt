package com.habit.gold.feature.trade.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.habit.gold.BuildConfig
import com.habit.gold.feature.trade.domain.TradePaymentLauncher
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult
import com.habit.gold.payments.juspay.EmbeddedJuspayCheckoutHost
import com.habit.gold.payments.juspay.JuspayPaymentResult
import com.habit.gold.payments.juspay.mergePreferredUpiIntoSdkPayload
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume

@Composable
actual fun rememberPlatformTradePaymentLauncher(): TradePaymentLauncher {
    val context = LocalContext.current
    val pendingContinuationState = remember {
        mutableStateOf<CancellableContinuation<TradePaymentLaunchResult>?>(null)
    }

    DisposableEffect(Unit) {
        onDispose {
            pendingContinuationState.value?.cancel()
            pendingContinuationState.value = null
        }
    }

    return remember(context) {
        object : TradePaymentLauncher {
            override suspend fun launch(request: TradePaymentLaunchRequest): TradePaymentLaunchResult {
                val juspayRequest = request as? TradePaymentLaunchRequest.Juspay
                    ?: return TradePaymentLaunchResult.Failure(
                        status = "unsupported_request",
                        message = "Unsupported payment request for this build.",
                    )

                if (!BuildConfig.JUSPAY_ENABLED) {
                    return TradePaymentLaunchResult.Failure(
                        status = "juspay_disabled",
                        message = "Juspay is disabled in this build.",
                    )
                }

                if (BuildConfig.JUSPAY_CLIENT_ID.isBlank() || BuildConfig.JUSPAY_MERCHANT_ID.isBlank()) {
                    return TradePaymentLaunchResult.Failure(
                        status = "juspay_not_configured",
                        message = "Juspay merchant configuration is missing in this build.",
                    )
                }

                val payloadJson = try {
                    val parsedPayload = JSONObject(juspayRequest.payloadJson)
                    mergePreferredUpiIntoSdkPayload(parsedPayload, juspayRequest.preferredUpiPackage)
                } catch (_: Exception) {
                    return TradePaymentLaunchResult.Failure(
                        status = "invalid_payload",
                        message = "Invalid payment payload.",
                    )
                }

                return suspendCancellableCoroutine { continuation ->
                    val existingContinuation = pendingContinuationState.value
                    if (existingContinuation != null && existingContinuation.isActive) {
                        continuation.resume(
                            TradePaymentLaunchResult.Failure(
                                status = "payment_in_progress",
                                message = "Another payment is already in progress.",
                            ),
                        )
                        return@suspendCancellableCoroutine
                    }

                    pendingContinuationState.value = continuation
                    val embeddedCheckoutHost = context.findEmbeddedCheckoutHost()
                    val startedEmbedded = embeddedCheckoutHost?.startEmbeddedJuspayCheckout(payloadJson) { result ->
                        val currentContinuation = pendingContinuationState.value ?: return@startEmbeddedJuspayCheckout
                        pendingContinuationState.value = null
                        currentContinuation.resume(result.toTradePaymentLaunchResult())
                    } == true

                    if (!startedEmbedded) {
                        pendingContinuationState.value = null
                        continuation.resume(
                            TradePaymentLaunchResult.Failure(
                                status = "embedded_checkout_unavailable",
                                message = "Embedded payment checkout is unavailable in this screen.",
                            ),
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

private fun Context.findEmbeddedCheckoutHost(): EmbeddedJuspayCheckoutHost? {
    var current: Context? = this
    while (current != null) {
        if (current is EmbeddedJuspayCheckoutHost) return current
        current = (current as? ContextWrapper)?.baseContext
    }
    return null
}

private fun JuspayPaymentResult.toTradePaymentLaunchResult(): TradePaymentLaunchResult {
    return when (this) {
        is JuspayPaymentResult.Success -> TradePaymentLaunchResult.Success(status = status)
        is JuspayPaymentResult.Failure -> TradePaymentLaunchResult.Failure(
            status = status,
            message = message,
            shouldPollOrderStatus = true,
        )
        JuspayPaymentResult.BackPressed -> TradePaymentLaunchResult.BackPressed
    }
}
