package com.habit.gold.feature.profile.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.profile.domain.ProfileRepository

class VerifyProfileKycUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(
        pan: String,
        name: String,
    ): ApiResult<Unit> = repository.verifyKyc(pan = pan, name = name)
}

