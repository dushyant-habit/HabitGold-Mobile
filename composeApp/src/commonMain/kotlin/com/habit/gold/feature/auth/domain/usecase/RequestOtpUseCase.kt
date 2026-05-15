package com.habit.gold.feature.auth.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.OtpRequestResult

class RequestOtpUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(phoneNumber: String): ApiResult<OtpRequestResult> {
        return authRepository.requestOtp(phoneNumber)
    }
}
