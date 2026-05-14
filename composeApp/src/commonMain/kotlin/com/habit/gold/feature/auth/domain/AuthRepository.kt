package com.habit.gold.feature.auth.domain

import com.habit.gold.core.network.ApiResult

interface AuthRepository {
    suspend fun requestOtp(phoneNumber: String): ApiResult<OtpRequestResult>
    suspend fun verifyOtp(phoneNumber: String, otp: String): ApiResult<VerifyOtpResult>
    suspend fun submitBasicInfo(name: String, email: String, pinCode: String): ApiResult<AuthenticatedUser>
}
