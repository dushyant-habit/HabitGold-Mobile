package com.habit.gold.feature.rewards.domain.model

data class RewardsMilestonesSummary(
    val totalPaidGoldGrams: String?,
    val milestonesActive: Boolean,
    val milestones: List<RewardMilestone>,
    val ongoingPercent: String?,
    val totalEarnedInr: String?,
    val goldCashbackInr: String?,
    val referralCashbackInr: String?,
    val redeemableInr: String?,
    val redeemedInr: String?,
    val boosterActive: Boolean,
)

data class RewardMilestone(
    val key: String,
    val name: String,
    val thresholdGrams: String,
    val rewardInr: String,
    val status: String,
)

data class RewardsFeatureFlags(
    val rewardsActive: Boolean,
)

data class RewardHistoryEntry(
    val id: String,
    val kind: String,
    val amountInr: String,
    val direction: String,
    val createdAt: String,
    val source: String?,
    val sourceRef: String?,
    val debitType: String?,
    val orderId: String?,
    val withdrawalId: String?,
    val expiresAt: String?,
    val expired: Boolean,
    val remaining: String?,
)

data class ReferDetails(
    val lifetimeEarnings: String?,
    val activeReferrals: Int,
    val boosterIsActive: Boolean,
    val currentPercentage: String?,
    val daysLeft: Int?,
    val totalDaysCap: Int?,
    val referralFirstBuyDays: Int,
    val buyThresholdInr: String?,
    val buyBonusDays: Int,
    val sipMinAmountInr: String?,
    val sipBonusDays: Int,
    val referralCode: String?,
    val referralList: List<ReferDetailsReferralCode>,
)

data class ReferDetailsReferralCode(
    val code: String,
    val isDefault: Boolean,
    val isActive: Boolean,
)
