package com.habit.gold.feature.rewards.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RewardsMilestonesResponseDto(
    @SerialName("totalPaidGoldGrams") val totalPaidGoldGrams: String? = null,
    @SerialName("milestonesActive") val milestonesActive: Boolean? = null,
    @SerialName("milestones") val milestones: List<RewardMilestoneDto> = emptyList(),
    @SerialName("ongoingPercent") val ongoingPercent: String? = null,
    @SerialName("totalEarnedInr") val totalEarnedInr: String? = null,
    @SerialName("goldCashbackInr") val goldCashbackInr: String? = null,
    @SerialName("referralCashbackInr") val referralCashbackInr: String? = null,
    @SerialName("redeemableInr") val redeemableInr: String? = null,
    @SerialName("redeemedInr") val redeemedInr: String? = null,
    @SerialName("boosterActive") val boosterActive: Boolean? = null,
)

@Serializable
data class RewardMilestoneDto(
    @SerialName("key") val key: String,
    @SerialName("name") val name: String,
    @SerialName("thresholdGrams") val thresholdGrams: String,
    @SerialName("rewardInr") val rewardInr: String,
    @SerialName("status") val status: String,
)

@Serializable
data class RewardsHistoryResponseDto(
    @SerialName("items") val items: List<RewardHistoryItemDto> = emptyList(),
    @SerialName("nextCursor") val nextCursor: String? = null,
    @SerialName("hasMore") val hasMore: Boolean = false,
)

@Serializable
data class RewardHistoryItemDto(
    @SerialName("id") val id: String,
    @SerialName("kind") val kind: String,
    @SerialName("amountInr") val amountInr: String,
    @SerialName("direction") val direction: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("source") val source: String? = null,
    @SerialName("sourceRef") val sourceRef: String? = null,
    @SerialName("debitType") val debitType: String? = null,
    @SerialName("orderId") val orderId: String? = null,
    @SerialName("withdrawalId") val withdrawalId: String? = null,
    @SerialName("expiresAt") val expiresAt: String? = null,
    @SerialName("expired") val expired: Boolean = false,
    @SerialName("remaining") val remaining: String? = null,
)

@Serializable
data class ReferDetailsBoosterStatusDto(
    @SerialName("isActive") val isActive: Boolean = false,
    @SerialName("currentPercentage") val currentPercentage: String? = null,
    @SerialName("daysLeft") val daysLeft: Int? = null,
    @SerialName("totalDaysCap") val totalDaysCap: Int? = null,
)

@Serializable
data class ReferDetailsWaysToExtendDto(
    @SerialName("referralFirstBuyDays") val referralFirstBuyDays: Int = 0,
    @SerialName("buyThresholdInr") val buyThresholdInr: String? = null,
    @SerialName("buyBonusDays") val buyBonusDays: Int = 0,
    @SerialName("sipMinAmountInr") val sipMinAmountInr: String? = null,
    @SerialName("sipBonusDays") val sipBonusDays: Int = 0,
)

@Serializable
data class ReferralListItemDto(
    @SerialName("code") val code: String = "",
    @SerialName("isDefault") val isDefault: Boolean = false,
    @SerialName("isActive") val isActive: Boolean = false,
)

@Serializable
data class ReferDetailsResponseDto(
    @SerialName("lifetimeEarnings") val lifetimeEarnings: String? = null,
    @SerialName("activeReferrals") val activeReferrals: Int = 0,
    @SerialName("boosterStatus") val boosterStatus: ReferDetailsBoosterStatusDto? = null,
    @SerialName("waysToExtend") val waysToExtend: ReferDetailsWaysToExtendDto? = null,
    @SerialName("referralCode") val referralCode: String? = null,
    @SerialName("referralList") val referralList: List<ReferralListItemDto> = emptyList(),
)
