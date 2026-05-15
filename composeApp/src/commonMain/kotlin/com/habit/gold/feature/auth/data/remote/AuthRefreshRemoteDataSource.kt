package com.habit.gold.feature.auth.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.auth.data.model.RefreshTokenRequestDto
import com.habit.gold.feature.auth.data.model.RefreshTokenResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class AuthRefreshRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun refreshTokens(
        refreshToken: String,
        accessToken: String?,
    ): ApiResult<RefreshTokenResponseDto> = safeApiCall {
        httpClient.post("auth/refresh") {
            contentType(ContentType.Application.Json)
            if (!accessToken.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            setBody(RefreshTokenRequestDto(refreshToken = refreshToken))
        }.body()
    }
}
