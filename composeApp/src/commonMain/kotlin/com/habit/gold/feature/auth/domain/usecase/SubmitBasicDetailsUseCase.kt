package com.habit.gold.feature.auth.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.AuthenticatedUser

class SubmitBasicDetailsUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        legalName: String,
        pinCode: String?,
        referralCode: String?,
    ): ApiResult<AuthenticatedUser> {
        return authRepository.submitBasicDetails(
            name = legalName,
            pinCode = pinCode,
            referralCode = referralCode,
        )
    }
}
