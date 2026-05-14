package com.habit.gold.feature.auth.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.core.network.skipAuthentication
import com.habit.gold.feature.auth.data.model.RequestOtpRequestDto
import com.habit.gold.feature.auth.data.model.RequestOtpResponseDto
import com.habit.gold.feature.auth.data.model.UpdateBasicInfoRequestDto
import com.habit.gold.feature.auth.data.model.UserProfileDto
import com.habit.gold.feature.auth.data.model.VerifyOtpRequestDto
import com.habit.gold.feature.auth.data.model.VerifyOtpResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class AuthRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun requestOtp(
        mobileNumber: String,
    ): ApiResult<RequestOtpResponseDto> = safeApiCall {
        httpClient.post("auth/send-otp") {
            skipAuthentication()
            setBody(
                RequestOtpRequestDto(
                    mobileNumber = mobileNumber.toOtpRequestMobileNumber(),
                )
            )
        }.body()
    }

    suspend fun verifyOtp(
        mobileNumber: String,
        otp: String,
    ): ApiResult<VerifyOtpResponseDto> = safeApiCall {
        httpClient.post("auth/verify-otp") {
            skipAuthentication()
            setBody(
                VerifyOtpRequestDto(
                    mobileNumber = mobileNumber,
                    otp = otp,
                )
            )
        }.body()
    }

    suspend fun getProfile(): ApiResult<UserProfileDto> = safeApiCall {
        httpClient.get("user/profile").body()
    }

    suspend fun updateBasicInfo(
        name: String,
        email: String,
        pinCode: String,
    ): ApiResult<UserProfileDto> = safeApiCall {
        httpClient.put("user/profile") {
            setBody(
                UpdateBasicInfoRequestDto(
                    name = name,
                    email = email,
                    pinCode = pinCode,
                )
            )
        }.body()
    }
}

private fun String.toOtpRequestMobileNumber(): String {
    val digits = filter(Char::isDigit).takeLast(10)
    return "+91$digits"
}
