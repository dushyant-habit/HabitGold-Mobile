package com.habit.gold.feature.home.domain.model

data class HomeDashboardSummary(
    val totalGoldBalanceGrams: Double,
    val investedValue: Double,
    val rewardsApplied: Double?,
    val gstPaid: Double,
    val totalCost: Double,
    val averageBuyPricePerGram: Double,
    val currentValue: Double,
    val liveBuyPricePerGram: Double,
    val liveSellPricePerGram: Double,
    val finalPayoutAmount: Double,
    val buySellPriceDifference: Double,
)

data class HomeRecentTransactionPreview(
    val id: String,
    val type: String,
    val status: String,
    val amount: String,
    val goldQuantity: String,
    val createdAt: String,
    val isSip: Boolean,
    val sipName: String?,
    val sipFrequency: String?,
)

data class HomeForceUpdate(
    val title: String?,
    val message: String?,
    val ctaText: String?,
    val updateUrl: String?,
    val storeUrl: String?,
    val minVersion: String?,
    val latestVersion: String?,
    val isForced: Boolean,
)

data class HomeSipMandateBilling(
    val nextExecutionAmount: String?,
    val currentAmount: String?,
    val needsAttention: Boolean,
)

data class HomeSipMandate(
    val id: String,
    val name: String,
    val amount: String,
    val frequency: String,
    val startDate: String,
    val status: String,
    val promoCode: String?,
    val nextExecutionDate: String?,
    val billingCurrentAmount: String?,
    val billingNextExecutionAmount: String?,
    val billing: HomeSipMandateBilling?,
)

data class HomeGoldPricePoint(
    val timestampMillis: Long,
    val price: Double,
)

data class HomeSummary(
    val dashboard: HomeDashboardSummary,
    val recentTransactions: List<HomeRecentTransactionPreview>,
    val forceUpdate: HomeForceUpdate?,
    val sipMandates: List<HomeSipMandate>,
)
