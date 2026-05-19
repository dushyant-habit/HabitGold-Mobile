package com.habit.gold.feature.delivery.presentation

import com.habit.gold.feature.delivery.data.PendingDeliveryCheckout
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStage
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryVerifyQuoteDto
import kotlinx.serialization.json.JsonObject
import kotlin.time.Instant

internal fun DeliveryQuoteResponseDto.toUiQuote(productId: String, addressId: String): DeliveryCheckoutQuote =
    DeliveryCheckoutQuote(
        quoteId = quoteId,
        productId = productId,
        addressId = addressId,
        goldWeightGrams = verifyQuote.goldWeightGrams.toDoubleOrNull() ?: 0.0,
        mintingChargeInr = verifyQuote.mintingChargeInr.toDoubleOrNull() ?: 0.0,
        payableChargeInr = verifyQuote.payableChargeInr.toDoubleOrNull() ?: 0.0,
        estimatedDispatchDays = verifyQuote.estimatedDispatchDays,
        verifyExpiresAt = verifyQuote.verifyExpiresAt,
    )

internal fun DeliveryCheckoutQuote.toPendingCheckout(
    couponCode: String? = null,
    confirmIdempotencyKey: String? = null,
    orderId: String? = null,
    sdkPayload: JsonObject? = null,
    stage: PendingDeliveryCheckoutStage,
): PendingDeliveryCheckout =
    PendingDeliveryCheckout(
        quoteId = quoteId,
        productId = productId.orEmpty(),
        addressId = addressId.orEmpty(),
        couponCode = couponCode,
        verifyQuote = DeliveryVerifyQuoteDto(
            mintingChargeInr = mintingChargeInr.toString(),
            payableChargeInr = payableChargeInr.toString(),
            goldWeightGrams = goldWeightGrams.toString(),
            verifyExpiresAt = verifyExpiresAt.orEmpty(),
            estimatedDispatchDays = estimatedDispatchDays,
        ),
        confirmIdempotencyKey = confirmIdempotencyKey,
        orderId = orderId,
        sdkPayload = sdkPayload,
        stage = stage,
    )

internal fun PendingDeliveryCheckout.toUiQuote(): DeliveryCheckoutQuote =
    DeliveryCheckoutQuote(
        quoteId = quoteId,
        productId = productId,
        addressId = addressId,
        goldWeightGrams = verifyQuote.goldWeightGrams.toDoubleOrNull() ?: 0.0,
        mintingChargeInr = verifyQuote.mintingChargeInr.toDoubleOrNull() ?: 0.0,
        payableChargeInr = verifyQuote.payableChargeInr.toDoubleOrNull() ?: 0.0,
        estimatedDispatchDays = verifyQuote.estimatedDispatchDays,
        verifyExpiresAt = verifyQuote.verifyExpiresAt,
    )

internal fun DeliveryCheckoutQuote.isExpired(nowMillis: Long): Boolean {
    val expiresAt = verifyExpiresAt?.parseIsoInstantToMillis() ?: return false
    return expiresAt <= nowMillis
}

internal fun PendingDeliveryCheckout.isExpired(nowMillis: Long): Boolean {
    val expiresAt = verifyQuote.verifyExpiresAt.parseIsoInstantToMillis() ?: return false
    return expiresAt <= nowMillis
}

internal fun DeliveryOrderDto.resolveCheckoutState(): CheckoutOrderState {
    val payment = paymentStatus?.trim()?.uppercase()
    val status = status?.trim()?.uppercase()
    return when {
        payment == "SUCCESS" -> CheckoutOrderState.SUCCESS
        payment == "FAILED" || payment == "FAILURE" || payment == "CANCELLED" -> CheckoutOrderState.FAILURE
        payment == "PAYMENT_SESSION_PENDING" -> CheckoutOrderState.RECOVERABLE_PENDING
        confirmedAt != null && payment.isNullOrBlank() -> CheckoutOrderState.SUCCESS
        status == "CONFIRMED" || status == "DISPATCHED" || status == "IN_TRANSIT" || status == "DELIVERED" -> CheckoutOrderState.SUCCESS
        else -> CheckoutOrderState.RECOVERABLE_PENDING
    }
}

internal enum class CheckoutOrderState {
    SUCCESS,
    FAILURE,
    RECOVERABLE_PENDING,
}

private fun String.parseIsoInstantToMillis(): Long? =
    runCatching { Instant.parse(this).toEpochMilliseconds() }.getOrNull()
