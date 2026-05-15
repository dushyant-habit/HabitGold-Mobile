package com.habit.gold.feature.auth.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.VerifyOtpResult

class VerifyOtpUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        phoneNumber: String,
        otp: String,
    ): ApiResult<VerifyOtpResult> {
        return authRepository.verifyOtp(phoneNumber, otp)
    }
}
