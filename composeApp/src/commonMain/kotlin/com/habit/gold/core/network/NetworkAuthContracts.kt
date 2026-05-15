package com.habit.gold.core.network

import com.habit.gold.core.storage.AuthTokens

interface AuthTokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
}

interface SessionExpiryHandler {
    suspend fun onSessionExpired()
}

interface TokenRefreshHandler {
    suspend fun refreshTokens(
        refreshToken: String,
        accessToken: String?,
    ): ApiResult<AuthTokens>
}

class UnsupportedTokenRefreshHandler : TokenRefreshHandler {
    override suspend fun refreshTokens(
        refreshToken: String,
        accessToken: String?,
    ): ApiResult<AuthTokens> {
        return ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Unauthorized,
                message = "Your session has expired. Please log in again.",
            )
        )
    }
}
