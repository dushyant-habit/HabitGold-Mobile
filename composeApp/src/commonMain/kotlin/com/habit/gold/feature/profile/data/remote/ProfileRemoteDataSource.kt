package com.habit.gold.feature.profile.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.profile.data.model.ProfileKycVerifyRequestDto
import com.habit.gold.feature.profile.data.model.ProfilePortfolioDashboardDto
import com.habit.gold.feature.profile.data.model.ProfileUpdateRequestDto
import com.habit.gold.feature.profile.data.model.ProfileUserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ProfileRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getProfile(): ApiResult<ProfileUserDto> = safeApiCall {
        httpClient.get("user/profile").body()
    }

    suspend fun getPortfolioDashboard(): ApiResult<ProfilePortfolioDashboardDto> = safeApiCall {
        httpClient.get("portfolio").body()
    }

    suspend fun updateProfile(
        request: ProfileUpdateRequestDto,
    ): ApiResult<ProfileUserDto> = safeApiCall {
        httpClient.put("user/profile") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun verifyKyc(
        request: ProfileKycVerifyRequestDto,
    ): ApiResult<Unit> = safeApiCall {
        httpClient.post("kyc/verify") {
            setBody(request)
        }
        Unit
    }

    suspend fun logout(): ApiResult<Unit> = safeApiCall {
        httpClient.post("auth/logout")
        Unit
    }

    suspend fun requestDeleteAccount(): ApiResult<Unit> = safeApiCall {
        httpClient.post("user/account/delete-request")
        Unit
    }
}
