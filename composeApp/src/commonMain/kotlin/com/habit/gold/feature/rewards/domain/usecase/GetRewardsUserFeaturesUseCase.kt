package com.habit.gold.feature.rewards.domain.usecase

import com.habit.gold.feature.rewards.domain.RewardsRepository

class GetRewardsUserFeaturesUseCase(
    private val repository: RewardsRepository,
) {
    suspend operator fun invoke() = repository.getUserFeatures()
}
