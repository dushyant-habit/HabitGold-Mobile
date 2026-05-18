package com.habit.gold.feature.profile.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.profile.domain.ProfileRepository
import com.habit.gold.feature.profile.domain.model.ProfileSummary

class GetProfileSummaryUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): ApiResult<ProfileSummary> = repository.getProfileSummary()
}

