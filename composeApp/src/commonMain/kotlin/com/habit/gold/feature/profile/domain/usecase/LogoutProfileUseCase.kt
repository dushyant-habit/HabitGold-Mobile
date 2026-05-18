package com.habit.gold.feature.profile.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.profile.domain.ProfileRepository

class LogoutProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): ApiResult<Unit> = repository.logout()
}

