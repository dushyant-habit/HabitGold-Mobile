package com.habit.gold.feature.auth.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.core.network.skipAuthentication
import com.habit.gold.feature.auth.data.model.RequestOtpRequestDto
import com.habit.gold.feature.auth.data.model.RequestOtpResponseDto
import com.habit.gold.feature.auth.data.model.SubmitReferralCodeRequestDto
import com.habit.gold.feature.auth.data.model.UpdateBasicInfoRequestDto
import com.habit.gold.feature.auth.data.model.UserProfileDto
import com.habit.gold.feature.auth.data.model.VerifyOtpRequestDto
import com.habit.gold.feature.auth.data.model.VerifyOtpResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class AuthRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun requestOtp(
        mobileNumber: String,
    ): ApiResult<RequestOtpResponseDto> = safeApiCall {
        httpClient.post("auth/send-otp") {
            skipAuthentication()
            contentType(ContentType.Application.Json)
            setBody(
                RequestOtpRequestDto(
                    mobileNumber = mobileNumber.toAndroidOtpRequestMobileNumber(),
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
            contentType(ContentType.Application.Json)
            setBody(
                VerifyOtpRequestDto(
                    mobileNumber = mobileNumber,
                    otp = otp,
                    referralCode = null,
                )
            )
        }.body()
    }

    suspend fun getProfile(
        accessToken: String? = null,
    ): ApiResult<UserProfileDto> = safeApiCall {
        httpClient.get("user/profile") {
            if (!accessToken.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }.body()
    }

    suspend fun updateBasicInfo(
        name: String,
        email: String?,
        pinCode: String?,
    ): ApiResult<UserProfileDto> = safeApiCall {
        httpClient.put("user/profile") {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateBasicInfoRequestDto(
                    name = name,
                    email = email,
                    pinCode = pinCode,
                )
            )
        }.body()
    }

    suspend fun submitReferralCode(
        referralCode: String,
    ): ApiResult<Unit> = safeApiCall {
        httpClient.post("user/referral") {
            contentType(ContentType.Application.Json)
            setBody(
                SubmitReferralCodeRequestDto(
                    referralCode = referralCode,
                )
            )
        }
        Unit
    }
}

private fun String.toAndroidOtpRequestMobileNumber(): String {
    return filter(Char::isDigit).takeLast(10)
}
