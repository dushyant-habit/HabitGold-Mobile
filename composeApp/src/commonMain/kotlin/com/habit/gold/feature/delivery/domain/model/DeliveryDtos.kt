package com.habit.gold.feature.delivery.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject

@Serializable
data class CreateDeliveryQuoteDto(
    val addressId: String,
    val productId: String,
    val couponCode: String? = null
)

@Serializable
data class DeliveryQuoteResponseDto(
    val quoteId: String,
    val verifyQuote: DeliveryVerifyQuoteDto
)

@Serializable
data class DeliveryVerifyQuoteDto(
    val mintingChargeInr: String,
    val payableChargeInr: String,
    val goldWeightGrams: String,
    val verifyExpiresAt: String,
    val estimatedDispatchDays: Int? = null
)

@Serializable
data class ConfirmDeliveryOrderDto(
    val quoteId: String
)

@Serializable
data class DeliveryOrderConfirmResponseDto(
    val order: DeliveryOrderDto,
    val payment: DeliveryPaymentDto? = null,
    val sdkPayload: JsonObject? = null
)

@Serializable
data class DeliveryPaymentDto(
    val provider: String,
    val orderId: String,
    val payload: JsonObject
)

@Serializable
data class DeliveryOrderDto(
    val id: String? = null,
    val userId: String? = null,
    val sgUserId: String? = null,
    val sgTxId: String? = null,
    val sgInvoiceId: String? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("invoice_url", "invoiceUrl", "url", "sgInvoiceUrl")
    val invoiceUrl: String? = null,
    val productId: String? = null,
    val productSku: String? = null,
    val productDescription: String? = null,
    val metalWeightGrams: String? = null,
    val metalStamp: String? = null,
    val makingChargeInr: String? = null,
    val makingChargeWaived: Boolean? = null,
    val actualChargeInr: String? = null,
    val appliedPromoCode: String? = null,
    val promoRuleId: String? = null,
    val promotionalDeliveryDiscount: String? = null,
    val totalGoldDebitGrams: String? = null,
    val addressId: String? = null,
    val recipientName: String? = null,
    val phoneNumber: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val landmark: String? = null,
    val juspayOrderId: String? = null,
    val paymentStatus: String? = null,
    val status: String? = null,
    val courierTrackingId: String? = null,
    val courierCompany: String? = null,
    val estimatedDispatchDays: Int? = null,
    val verifyExpiresAt: String? = null,
    val metadata: DeliveryMetadataDto? = null,
    val idempotencyKey: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val confirmedAt: String? = null
)

@Serializable
data class DeliveryInvoiceResponseDto(
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("invoice_url", "invoiceUrl", "url")
    val invoiceUrl: String? = null
)

@Serializable
data class DeliveryMetadataDto(
    val latestDispatchStatus: DispatchStatusDto? = null,
    val deliveryFailureCompensation: DeliveryFailureCompensationDto? = null
)

@Serializable
data class DispatchStatusDto(
    val deliveryStatus: Int? = null,
    val message: String? = null,
    val trackingLink: String? = null
)

@Serializable
data class DeliveryFailureCompensationDto(
    val reconciledAt: String? = null,
    val source: String? = null,
    val goldRecreditedGrams: String? = null
)

@Serializable
data class CreateAddressDto(
    val type: AddressType = AddressType.HOME,
    val recipientName: String,
    val phoneNumber: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val state: String,
    val pincode: String,
    val landmark: String? = null,
    val isDefault: Boolean = false
)

@Serializable
data class UpdateAddressDto(
    val type: AddressType? = null,
    val recipientName: String? = null,
    val phoneNumber: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val landmark: String? = null,
    val isDefault: Boolean? = null
)

@Serializable
data class VerifyAddressOtpDto(
    val otp: String
)

@Serializable
data class DeliveryAddressDto(
    val id: String? = null,
    val type: String? = null,
    val recipientName: String? = null,
    val phoneNumber: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val landmark: String? = null,
    val isDefault: Boolean? = null,
    val verificationStatus: String? = null
)

fun DeliveryAddressDto.toDomain(): SavedAddress {
    val fullAddr = buildList {
        addressLine1?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        addressLine2?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        city?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        state?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        pincode?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
    }.joinToString(", ")

    return SavedAddress(
        id = id.orEmpty(),
        name = recipientName.orEmpty(),
        fullAddress = fullAddr.ifBlank { "${addressLine1.orEmpty()}, ${city.orEmpty()}" },
        phoneNo = phoneNumber.orEmpty(),
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        city = city,
        state = state,
        pincode = pincode,
        verificationStatus = verificationStatus
    )
}


fun normalizeIndianMobileForApi(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    val trimmed = raw.trim()
    return when {
        trimmed.startsWith("+") && digits.length >= 10 -> trimmed
        digits.length == 10 -> "+91$digits"
        digits.length == 12 && digits.startsWith("91") -> "+$digits"
        digits.length > 10 -> "+91${digits.takeLast(10)}"
        else -> trimmed.ifBlank { "+91" }
    }
}

fun indianMobileNumbersMatch(first: String?, second: String?): Boolean {
    fun String?.normalizedLastTenDigits(): String {
        val digits = this.orEmpty().filter { it.isDigit() }.takeLast(10)
        return digits.takeIf { it.length == 10 }.orEmpty()
    }

    val left = first.normalizedLastTenDigits()
    val right = second.normalizedLastTenDigits()
    return left.isNotEmpty() && left == right
}
