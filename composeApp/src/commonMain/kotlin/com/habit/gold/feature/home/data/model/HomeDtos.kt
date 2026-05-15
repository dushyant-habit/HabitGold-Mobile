package com.habit.gold.feature.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioDashboardDto(
    val totalGoldBalanceGrams: String = "0",
    val investedValue: String = "0",
    val rewardsApplied: String? = null,
    val gstPaid: String = "0",
    val totalCost: String = "0",
    val averageBuyPricePerGram: String = "0",
    val currentValue: String = "0",
    val liveBuyPricePerGram: String = "0",
    val liveSellPricePerGram: String = "0",
    val finalPayoutAmount: String = "0",
    val buySellPriceDifference: String = "0",
)

@Serializable
data class TransactionRewardsDto(
    val used: Boolean = false,
    val couponCode: String? = null,
    val discountAmount: String = "0",
    val extraGold: String = "0",
    val cashback: String = "0",
)

@Serializable
data class TransactionSipDto(
    val mandateId: String,
    val executionId: String? = null,
    val frequency: String? = null,
    val mandateName: String? = null,
)

@Serializable
data class TransactionDto(
    val id: String,
    val type: String,
    val status: String,
    val amount: String,
    val gstAmount: String,
    val netAmount: String,
    val goldQuantity: String,
    val goldPrice: String,
    val createdAt: String,
    val rewards: TransactionRewardsDto = TransactionRewardsDto(),
    val isSip: Boolean = false,
    val sip: TransactionSipDto? = null,
)

@Serializable
data class MetaDto(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
)

@Serializable
data class TransactionsResponseDto(
    val data: List<TransactionDto>,
    val meta: MetaDto,
)

@Serializable
data class UserFeaturesResponseDto(
    @SerialName("rewards") val rewards: UserFeatureDto? = null,
    @SerialName("referrals") val referrals: UserFeatureDto? = null,
    @SerialName("goldAllotmentRecovery") val goldAllotmentRecovery: UserFeatureDto? = null,
    @SerialName("appUpdate") val appUpdate: AppUpdateFeatureDto? = null,
    @SerialName("forceUpdate") val forceUpdate: AppUpdateFeatureDto? = null,
)

@Serializable
data class UserFeatureDto(
    @SerialName("isActive") val isActive: Boolean = false,
    @SerialName("reason") val reason: String? = null,
)

@Serializable
data class AppUpdateFeatureDto(
    @SerialName("isActive") val isActive: Boolean = false,
    @SerialName("reason") val reason: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("ctaText") val ctaText: String? = null,
    @SerialName("updateUrl") val updateUrl: String? = null,
    @SerialName("storeUrl") val storeUrl: String? = null,
    @SerialName("minVersion") val minVersion: String? = null,
    @SerialName("minimumVersion") val minimumVersion: String? = null,
    @SerialName("latestVersion") val latestVersion: String? = null,
    @SerialName("force") val force: Boolean? = null,
    @SerialName("isForceUpdate") val isForceUpdate: Boolean? = null,
)

@Serializable
data class SipMandateBillingDto(
    val executionId: String? = null,
    val executionStatus: String? = null,
    val nextExecutionOrderId: String? = null,
    val nextExecutionAmount: String? = null,
    val currentAmount: String? = null,
    val amountUpdatedAt: String? = null,
    val needsAttention: Boolean = false,
)

@Serializable
data class SipMandateDto(
    val id: String,
    val userId: String? = null,
    val name: String,
    val amount: String,
    val frequency: String,
    val startDate: String,
    val juspayPlanId: String? = null,
    val status: String,
    val juspayMandateId: String? = null,
    val juspayOrderId: String? = null,
    val juspayBillingExecutionId: String? = null,
    val juspayBillingExecutionStatus: String? = null,
    val billingLastEventName: String? = null,
    val billingLastEventAt: String? = null,
    val billingLastScheduledExecutionOrderId: String? = null,
    val billingLastRetryScheduledExecutionOrderId: String? = null,
    val billingNextExecutionOrderId: String? = null,
    val billingNextExecutionAmount: String? = null,
    val billingCurrentAmount: String? = null,
    val billingAmountUpdatedAt: String? = null,
    val billingRetryExceededAt: String? = null,
    val billingDeactivatedAt: String? = null,
    val promoCode: String? = null,
    val consecutiveFailures: Int = 0,
    val nextExecutionDate: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastSyncedAt: String? = null,
    val syncPending: Boolean = false,
    val billing: SipMandateBillingDto? = null,
)

@Serializable
data class PriceHistoryDto(
    val days: Int,
    val points: Int,
    val data: List<PricePointDto>,
)

@Serializable
data class PricePointDto(
    val date: String,
    val price: String,
)
