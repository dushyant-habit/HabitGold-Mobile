@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.habit.gold.feature.trade.data.model

import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradeCouponType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject

@Serializable
data class TradeBuyOrderRequestDto(
    val amount: Double? = null,
    val grams: Double? = null,
    val buyRateId: String,
    val couponCode: String? = null,
    val useRewardsInr: Double? = null,
)

@Serializable
data class TradeBuyPriceDto(
    val price: Double,
    val rateId: String,
    val validUntil: String,
    val taxPc: Double,
    val sourceTimestamp: String,
)

@Serializable
data class TradeSellPriceDto(
    val price: Double,
    val rateId: String,
    val validUntil: String,
    val sourceTimestamp: String,
)

@Serializable
data class TradeSellOrderRequestDto(
    val grams: Double,
    val sellRateId: String,
)

@Serializable
data class TradeExecuteSellOrderRequestDto(
    val orderId: String,
    val vpaId: String,
)

@Serializable
data class TradeGstDetailsDto(
    val grossAmount: String,
    val netAmount: String,
    val gstAmount: String,
)

@Serializable
data class TradeBuyOrderResponseDto(
    val orderId: String,
    val status: String,
    @JsonNames("collectionProvider", "paymentProvider")
    val paymentProvider: String,
    @JsonNames("collectionProviderOrderId", "paymentProviderOrderId")
    val paymentProviderOrderId: String,
    val priceLockId: String = "",
    val priceLockExpiresAt: String = "",
    @JsonNames("gold_quantity", "goldQuantity", "grams")
    val goldQuantity: String = "",
    val goldPrice: String,
    val gst: TradeGstDetailsDto,
    val sdkPayload: JsonObject? = null,
)

@Serializable
data class TradeSellOrderResponseDto(
    val orderId: String,
    val status: String,
    val priceLockId: String = "",
    val priceLockExpiresAt: String = "",
    @JsonNames("goldQuantity")
    val grams: String = "",
    val goldPrice: String = "",
    val amount: String = "",
    val transactionId: String? = null,
)

@Serializable
data class TradeStatusResponseDto(
    val orderId: String,
    val status: String,
    val message: String? = null,
)

@Serializable
data class TradeInvoiceResponseDto(
    val invoiceUrl: String,
)

@Serializable
data class TradeRewardsDto(
    val used: Boolean,
    val couponCode: String? = null,
    val discountAmount: String,
    val extraGold: String,
    val cashback: String,
)

@Serializable
data class TradeTransactionSipDto(
    val mandateId: String,
    val executionId: String? = null,
    val frequency: String? = null,
    val mandateName: String? = null,
)

@Serializable
data class TradeTransactionDto(
    val id: String,
    val type: String,
    val status: String,
    val amount: String,
    val gstAmount: String,
    val netAmount: String,
    val goldQuantity: String,
    val goldPrice: String,
    val createdAt: String,
    val rewards: TradeRewardsDto,
    val paymentDetails: JsonObject? = null,
    val isSip: Boolean = false,
    val sip: TradeTransactionSipDto? = null,
)

@Serializable
data class TradeMetaDto(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
)

@Serializable
data class TradeTransactionsResponseDto(
    val data: List<TradeTransactionDto>,
    val meta: TradeMetaDto,
)

@Serializable
data class TradeSellAvailabilityDto(
    val totalGoldBalanceGrams: String,
    val sellableGoldBalanceGrams: String,
    val lockedGoldBalanceGrams: String = "0",
    val nextSellableAt: String? = null,
)

@Serializable
data class TradeUserVpaDto(
    val id: String,
    val address: String,
    val holderName: String? = null,
    val isVerified: Boolean = false,
    val isDefault: Boolean = false,
)

@Serializable
data class TradeDefaultVpaResponseDto(
    val success: Boolean = true,
)

@Serializable
data class TradeVerifyVpaRequestDto(
    val vpa: String,
)

@Serializable
data class TradeVerifyVpaResponseDto(
    val message: String,
    @JsonNames("vpaId", "id")
    val vpaId: String? = null,
    val isVerified: Boolean = true,
)

@Serializable
data class TradeCouponValidationRequestDto(
    val orderType: TradeCouponOrderType,
    val code: String,
    val amount: Double? = null,
    val grams: Double? = null,
    val deliveryFeeInr: Double? = null,
    val deliveryGrams: Double? = null,
)

@Serializable
data class TradeCouponValidationResponseDto(
    val code: String? = null,
    val promoRuleId: String? = null,
    val promotionalDiscount: String,
    val promotionalCashback: String,
    val promotionalExtraGold: String,
    val promotionalDeliveryDiscount: String,
    val netOrderAmount: String,
    val netDeliveryFeeInr: String? = null,
)

@Serializable
data class TradeAvailableCouponDto(
    val code: String,
    val description: String? = null,
    val type: TradeCouponType,
    val estimatedSaving: String,
    val maxDiscountAmount: String? = null,
    val minOrderValue: String? = null,
    val expiresAt: String,
    val applicableOrderTypes: List<TradeCouponOrderType>,
    @SerialName("isAssigned")
    val isAssigned: Boolean,
)
