package com.habit.gold.feature.trade.domain.model

data class TradeBuyOrderRequest(
    val amount: Double? = null,
    val grams: Double? = null,
    val buyRateId: String,
    val couponCode: String? = null,
    val useRewardsInr: Double? = null,
)

data class TradeSellOrderRequest(
    val grams: Double,
    val sellRateId: String,
)

data class TradeBuyPrice(
    val price: Double,
    val rateId: String,
    val validUntil: String,
    val taxPc: Double,
    val sourceTimestamp: String,
)

data class TradeSellPrice(
    val price: Double,
    val rateId: String,
    val validUntil: String,
    val sourceTimestamp: String,
)

data class TradeLivePrice(
    val buy: Double,
    val sell: Double,
    val buyRateId: String = "",
    val sellRateId: String = "",
    val taxPc: Double = 0.0,
    val sourceTimestamp: String = "",
    val buyValidUntil: String = "",
    val sellValidUntil: String = "",
)

data class TradeBuyOrder(
    val orderId: String,
    val status: String,
    val paymentProvider: String,
    val paymentProviderOrderId: String,
    val priceLockId: String,
    val priceLockExpiresAt: String,
    val goldQuantityGrams: Double,
    val goldPricePerGram: Double,
    val gstGrossAmount: Double,
    val gstNetAmount: Double,
    val gstAmount: Double,
    val sdkPayloadJson: String? = null,
)

data class TradeSellOrder(
    val orderId: String,
    val status: String,
    val priceLockId: String,
    val priceLockExpiresAt: String,
    val goldQuantityGrams: Double,
    val goldPricePerGram: Double,
    val payoutAmount: Double,
    val transactionId: String? = null,
)

data class TradeStatus(
    val orderId: String,
    val status: String,
    val message: String? = null,
)

data class TradeInvoice(
    val invoiceUrl: String,
)

data class TradeTransactionPreview(
    val id: String,
    val type: String,
    val status: String,
    val amount: String,
    val gstAmount: String,
    val netAmount: String,
    val goldQuantity: String,
    val goldPrice: String,
    val createdAt: String,
    val isSip: Boolean,
    val sipMandateId: String? = null,
    val sipExecutionId: String? = null,
    val sipName: String? = null,
    val sipFrequency: String? = null,
)

data class TradeTransactionsPage(
    val data: List<TradeTransactionPreview>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
)

data class TradeSellAvailability(
    val totalGoldBalanceGrams: Double,
    val sellableGoldBalanceGrams: Double,
    val lockedGoldBalanceGrams: Double,
    val nextSellableAt: String? = null,
)

data class TradeUserVpa(
    val id: String,
    val address: String,
    val holderName: String?,
    val isVerified: Boolean,
    val isDefault: Boolean,
)

data class TradeVpaVerification(
    val message: String,
    val vpaId: String? = null,
    val isVerified: Boolean = true,
)

enum class TradeCouponOrderType {
    BUY,
    SIP,
    DELIVERY,
}

enum class TradeCouponType {
    CASHBACK,
    DISCOUNT,
    EXTRA_GOLD,
    DELIVERY_DISCOUNT,
    FREE_DELIVERY,
}

data class TradeCouponValidationRequest(
    val orderType: TradeCouponOrderType,
    val code: String,
    val amount: Double? = null,
    val grams: Double? = null,
    val deliveryFeeInr: Double? = null,
    val deliveryGrams: Double? = null,
)

data class TradeCouponValidation(
    val code: String?,
    val promoRuleId: String?,
    val promotionalDiscount: String,
    val promotionalCashback: String,
    val promotionalExtraGold: String,
    val promotionalDeliveryDiscount: String,
    val netOrderAmount: String,
    val netDeliveryFeeInr: String? = null,
)

data class TradeAvailableCoupon(
    val code: String,
    val description: String?,
    val type: TradeCouponType,
    val estimatedSaving: String,
    val maxDiscountAmount: String?,
    val minOrderValue: String?,
    val expiresAt: String,
    val applicableOrderTypes: List<TradeCouponOrderType>,
    val isAssigned: Boolean,
)
