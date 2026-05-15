package com.habit.gold.feature.auth.data.repository

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.TokenRefreshHandler
import com.habit.gold.core.network.installHabitGoldBaseConfig
import com.habit.gold.core.network.platformHttpClientEngineFactory
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.AuthTokens
import com.habit.gold.feature.auth.data.remote.AuthRefreshRemoteDataSource
import io.ktor.client.HttpClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AuthTokenRefreshHandler(
    private val appConfig: AppConfig,
    private val authTokenProvider: AuthTokenProvider,
    private val sessionStore: SessionStore,
) : TokenRefreshHandler {

    private val refreshMutex = Mutex()

    private val refreshRemoteDataSource by lazy {
        AuthRefreshRemoteDataSource(
            httpClient = HttpClient(platformHttpClientEngineFactory()) {
                installHabitGoldBaseConfig(appConfig)
                expectSuccess = true
            }
        )
    }

    /**
     * Mirrors the Android authenticator flow by serializing refresh attempts and reusing newer tokens when available.
     */
    override suspend fun refreshTokens(
        refreshToken: String,
        accessToken: String?,
    ): ApiResult<AuthTokens> {
        return refreshMutex.withLock {
            val latestAccessToken = authTokenProvider.getAccessToken()
            val latestRefreshToken = authTokenProvider.getRefreshToken()

            if (
                !latestAccessToken.isNullOrBlank() &&
                latestAccessToken != accessToken &&
                !latestRefreshToken.isNullOrBlank()
            ) {
                return@withLock ApiResult.Success(
                    AuthTokens(
                        accessToken = latestAccessToken,
                        refreshToken = latestRefreshToken,
                    )
                )
            }

            when (
                val refreshResult = refreshRemoteDataSource.refreshTokens(
                    refreshToken = latestRefreshToken ?: refreshToken,
                    accessToken = latestAccessToken ?: accessToken,
                )
            ) {
                is ApiResult.Success -> {
                    val refreshedTokens = AuthTokens(
                        accessToken = refreshResult.value.accessToken,
                        refreshToken = refreshResult.value.refreshToken,
                    )
                    sessionStore.updateTokens(
                        accessToken = refreshedTokens.accessToken,
                        refreshToken = refreshedTokens.refreshToken,
                    )
                    ApiResult.Success(refreshedTokens)
                }
                is ApiResult.Failure -> refreshResult
            }
        }
    }
}
