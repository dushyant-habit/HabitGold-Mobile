package com.habit.gold.feature.rewards.presentation

enum class RewardsLevelState {
    Completed,
    Active,
    Locked,
}

data class RewardsMilestoneData(
    val level: Int,
    val name: String,
    val targetGold: String,
    val rewardAmount: String,
    val completedSubtitle: String,
    val key: String = "",
)

data class RewardsMilestoneRowUi(
    val data: RewardsMilestoneData,
    val state: RewardsLevelState,
    val progressFraction: Float = 0f,
    val totalPaidGoldGramsForUi: Float? = null,
)

data class RewardsLifetimeBoosterRowUi(
    val state: RewardsLevelState,
    val boosterRateLabel: String,
)

data class RewardsHomeUi(
    val rows: List<RewardsMilestoneRowUi>,
    val lifetimeBooster: RewardsLifetimeBoosterRowUi,
    val milestonesActive: Boolean,
    val usePostJourneyHeader: Boolean,
    val totalEarnedDisplay: String,
    val goldCashbackDisplay: String,
    val referralCashbackDisplay: String,
    val redeemableDisplay: String,
    val redeemedDisplay: String,
)

data class RewardsHomeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val homeUi: RewardsHomeUi? = null,
    val rewardsFeatureActive: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface RewardsHomeIntent {
    data object LoadIfNeeded : RewardsHomeIntent
    data object Visible : RewardsHomeIntent
    data object Refresh : RewardsHomeIntent
}

data class RewardHistoryRowUi(
    val id: String,
    val title: String,
    val dateLabel: String,
    val expiryLabel: String?,
    val amountLabel: String,
    val isCredit: Boolean,
    val sourceChip: String,
    val expired: Boolean,
)

data class RewardsHistoryState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val items: List<RewardHistoryRowUi> = emptyList(),
    val errorMessage: String? = null,
)

data class RewardsReferDetailUi(
    val lifetimeEarningsDisplay: String = "₹0",
    val activeFriendsCount: Int = 0,
    val boosterIsActive: Boolean = true,
    val cashbackPercentLabel: String = "0.5%",
    val daysLeft: Int? = null,
    val totalDaysCap: Int? = null,
    val estimateCashbackFraction: Float = 0.005f,
    val referralCode: String = "SAVEGOLD20",
    val buyExtensionTitle: String = "Extend for +7 days",
    val buyExtensionSubtitle: String = "With every ₹10,000 purchase",
    val referralExtensionTitle: String = "Extend for +14 days",
    val referralExtensionSubtitle: String = "Refer a friend who buys gold",
    val sipExtensionTitle: String = "Extend for +7 days",
    val sipExtensionSubtitle: String = "Weekly ₹2,500 SIP = +7 days",
)

data class RewardsReferDetailState(
    val isLoading: Boolean = true,
    val ui: RewardsReferDetailUi = RewardsReferDetailUi(),
    val errorMessage: String? = null,
)
