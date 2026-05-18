package com.habit.gold.feature.rewards.domain

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.rewards.domain.model.ReferDetails
import com.habit.gold.feature.rewards.domain.model.RewardHistoryEntry
import com.habit.gold.feature.rewards.domain.model.RewardsFeatureFlags
import com.habit.gold.feature.rewards.domain.model.RewardsMilestonesSummary

interface RewardsRepository {
    suspend fun getRewardsMilestones(): ApiResult<RewardsMilestonesSummary>
    suspend fun getRewardsHistory(): ApiResult<List<RewardHistoryEntry>>
    suspend fun getReferDetails(): ApiResult<ReferDetails>
    suspend fun getUserFeatures(): ApiResult<RewardsFeatureFlags>
}
