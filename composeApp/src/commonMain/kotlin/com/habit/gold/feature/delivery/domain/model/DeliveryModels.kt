package com.habit.gold.feature.delivery.domain.model

data class PhysicalCoin(
    val id: String,
    val sku: String,
    val productName: String,
    val weightGm: Double,
    /** SafeGold making charge for this SKU (INR). */
    val makingCharge: Double,
    val imageUrl: String,
    val metalStamp: String,
    val estimatedDispatchDays: Int? = null
) {
    /** Gold metal value at live buy rate + making charge (per unit, before cart-level GST). */
    fun estimatedUnitSubtotalInr(liveBuyPricePerGram: Double): Double {
        val metal = if (liveBuyPricePerGram > 0) liveBuyPricePerGram * weightGm else 0.0
        return metal + makingCharge
    }
}

data class CartItem(
    val coin: PhysicalCoin,
    val quantity: Int
)

data class SavedAddress(
    val id: String,
    val name: String,
    val fullAddress: String,
    val phoneNo: String,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    /** Server-side verification, e.g. UNVERIFIED / VERIFIED — from delivery address APIs. */
    val verificationStatus: String? = null
)

/** Get Coin checkout requires OTP + serviceability; backend sets this when delivery is allowed. */
fun SavedAddress.isPincodeServiceable(): Boolean =
    verificationStatus?.equals("PINCODE_SERVICEABLE", ignoreCase = true) == true

fun SavedAddress.compactAddressLine(): String {
    val parts = buildList {
        addressLine1?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        addressLine2?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        city?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        state?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        pincode?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
    }
    return if (parts.isNotEmpty()) parts.joinToString(", ") else fullAddress.replace('\n', ',')
}

data class DeliveryCheckoutQuote(
    val quoteId: String,
    val productId: String? = null,
    val addressId: String? = null,
    val goldWeightGrams: Double = 0.0,
    val mintingChargeInr: Double = 0.0,
    val payableChargeInr: Double = 0.0,
    val estimatedDispatchDays: Int? = null,
    val verifyExpiresAt: String? = null
)

enum class DeliveryCheckoutPhase {
    IDLE,
    REVIEW_READY,
    CONFIRMING,
    PAYMENT_LAUNCH_READY,
    PAYMENT_IN_PROGRESS,
    VERIFYING_ORDER,
    COMPLETED
}

enum class AddressType {
    HOME,
    WORK,
    OTHER
}

data class TrackingMilestone(
    val title: String,
    val subtitle: String,
    val isComplete: Boolean
)
