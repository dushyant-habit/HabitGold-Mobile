package com.habit.gold.feature.rewards.data.repository

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.rewards.data.model.ReferDetailsResponseDto
import com.habit.gold.feature.rewards.data.model.RewardHistoryItemDto
import com.habit.gold.feature.rewards.data.model.RewardMilestoneDto
import com.habit.gold.feature.rewards.data.model.RewardsMilestonesResponseDto
import com.habit.gold.feature.rewards.data.remote.RewardsRemoteDataSource
import com.habit.gold.feature.rewards.domain.RewardsRepository
import com.habit.gold.feature.rewards.domain.model.ReferDetails
import com.habit.gold.feature.rewards.domain.model.ReferDetailsReferralCode
import com.habit.gold.feature.rewards.domain.model.RewardHistoryEntry
import com.habit.gold.feature.rewards.domain.model.RewardMilestone
import com.habit.gold.feature.rewards.domain.model.RewardsFeatureFlags
import com.habit.gold.feature.rewards.domain.model.RewardsMilestonesSummary

class RewardsRepositoryImpl(
    private val remoteDataSource: RewardsRemoteDataSource,
) : RewardsRepository {
    override suspend fun getRewardsMilestones(): ApiResult<RewardsMilestonesSummary> {
        return when (val result = remoteDataSource.getRewardsMilestones()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun getRewardsHistory(): ApiResult<List<RewardHistoryEntry>> {
        return when (val result = remoteDataSource.getRewardsHistory()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.items.map(RewardHistoryItemDto::toDomain))
        }
    }

    override suspend fun getReferDetails(): ApiResult<ReferDetails> {
        return when (val result = remoteDataSource.getReferDetails()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun getUserFeatures(): ApiResult<RewardsFeatureFlags> {
        return when (val result = remoteDataSource.getUserFeatures()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(
                RewardsFeatureFlags(
                    rewardsActive = result.value.rewards?.isActive == true,
                )
            )
        }
    }
}

private fun RewardsMilestonesResponseDto.toDomain(): RewardsMilestonesSummary {
    return RewardsMilestonesSummary(
        totalPaidGoldGrams = totalPaidGoldGrams,
        milestonesActive = milestonesActive == true,
        milestones = milestones.map(RewardMilestoneDto::toDomain),
        ongoingPercent = ongoingPercent,
        totalEarnedInr = totalEarnedInr,
        goldCashbackInr = goldCashbackInr,
        referralCashbackInr = referralCashbackInr,
        redeemableInr = redeemableInr,
        redeemedInr = redeemedInr,
        boosterActive = boosterActive == true,
    )
}

private fun RewardMilestoneDto.toDomain(): RewardMilestone {
    return RewardMilestone(
        key = key,
        name = name,
        thresholdGrams = thresholdGrams,
        rewardInr = rewardInr,
        status = status,
    )
}

private fun RewardHistoryItemDto.toDomain(): RewardHistoryEntry {
    return RewardHistoryEntry(
        id = id,
        kind = kind,
        amountInr = amountInr,
        direction = direction,
        createdAt = createdAt,
        source = source,
        sourceRef = sourceRef,
        debitType = debitType,
        orderId = orderId,
        withdrawalId = withdrawalId,
        expiresAt = expiresAt,
        expired = expired,
        remaining = remaining,
    )
}

private fun ReferDetailsResponseDto.toDomain(): ReferDetails {
    return ReferDetails(
        lifetimeEarnings = lifetimeEarnings,
        activeReferrals = activeReferrals,
        boosterIsActive = boosterStatus?.isActive ?: false,
        currentPercentage = boosterStatus?.currentPercentage,
        daysLeft = boosterStatus?.daysLeft,
        totalDaysCap = boosterStatus?.totalDaysCap,
        referralFirstBuyDays = waysToExtend?.referralFirstBuyDays ?: 0,
        buyThresholdInr = waysToExtend?.buyThresholdInr,
        buyBonusDays = waysToExtend?.buyBonusDays ?: 0,
        sipMinAmountInr = waysToExtend?.sipMinAmountInr,
        sipBonusDays = waysToExtend?.sipBonusDays ?: 0,
        referralCode = referralCode,
        referralList = referralList.map {
            ReferDetailsReferralCode(
                code = it.code,
                isDefault = it.isDefault,
                isActive = it.isActive,
            )
        },
    )
}
