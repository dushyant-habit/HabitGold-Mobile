package com.habit.gold.core.network

import com.habit.gold.core.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.takeFrom
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpHeaders
import io.ktor.util.AttributeKey
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val SkipAuthenticationKey = AttributeKey<Boolean>("skip-authentication")
private val RefreshAttemptedKey = AttributeKey<Boolean>("refresh-attempted")

fun HttpRequestBuilder.skipAuthentication() {
    attributes.put(SkipAuthenticationKey, true)
}

fun createHttpClient(
    appConfig: AppConfig,
    authTokenProvider: AuthTokenProvider,
    sessionExpiryHandler: SessionExpiryHandler,
    tokenRefreshHandler: TokenRefreshHandler,
): HttpClient {
    return HttpClient(platformHttpClientEngineFactory()) {
        applyHabitGoldHttpClientConfig(
            appConfig = appConfig,
            authTokenProvider = authTokenProvider,
            sessionExpiryHandler = sessionExpiryHandler,
            tokenRefreshHandler = tokenRefreshHandler,
        )
    }
}

/**
 * Applies the single shared Ktor policy for serialization, auth headers, timeouts, logging, and 401 handling.
 */
fun HttpClientConfig<*>.applyHabitGoldHttpClientConfig(
    appConfig: AppConfig,
    authTokenProvider: AuthTokenProvider,
    sessionExpiryHandler: SessionExpiryHandler,
    tokenRefreshHandler: TokenRefreshHandler,
) {
    installHabitGoldBaseConfig(appConfig)
    installAuthenticatedRequestPolicy(
        authTokenProvider = authTokenProvider,
        sessionExpiryHandler = sessionExpiryHandler,
        tokenRefreshHandler = tokenRefreshHandler,
    )
    expectSuccess = true
}

internal fun HttpClientConfig<*>.installHabitGoldBaseConfig(
    appConfig: AppConfig,
) {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                isLenient = true
                coerceInputValues = true
            },
        )
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println(message)
            }
        }
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
        level = if (appConfig.enableNetworkLogs) LogLevel.ALL else LogLevel.NONE
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
    }
    install(DefaultRequest) {
        url(appConfig.normalizedBaseUrl)
        header("x-app-version", appConfig.normalizedAppVersion)
        header("x-app-platform", appConfig.normalizedAppPlatform)
    }
}

private fun HttpClientConfig<*>.installAuthenticatedRequestPolicy(
    authTokenProvider: AuthTokenProvider,
    sessionExpiryHandler: SessionExpiryHandler,
    tokenRefreshHandler: TokenRefreshHandler,
) {
    val authRefreshPlugin = createClientPlugin("HabitGoldAuthRefresh") {
        onRequest { request, _ ->
            val shouldSkipAuthentication = request.attributes.getOrNull(SkipAuthenticationKey) == true
            val hasAuthorization = request.headers[HttpHeaders.Authorization] != null
            val accessToken = authTokenProvider.getAccessToken()
            if (!shouldSkipAuthentication && !hasAuthorization && !accessToken.isNullOrBlank()) {
                request.header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }
        on(Send) { request ->
            val originalCall = proceed(request)
            val shouldSkipAuthentication = request.attributes.getOrNull(SkipAuthenticationKey) == true
            val alreadyRetriedAfterRefresh = request.attributes.getOrNull(RefreshAttemptedKey) == true
            val isUnauthorized = originalCall.response.status == HttpStatusCode.Unauthorized

            if (shouldSkipAuthentication || !isUnauthorized) {
                return@on originalCall
            }

            if (alreadyRetriedAfterRefresh) {
                sessionExpiryHandler.onSessionExpired()
                return@on originalCall
            }

            val requestAccessToken = request.headers[HttpHeaders.Authorization]
                ?.removePrefix("Bearer ")
            val latestAccessToken = authTokenProvider.getAccessToken()
            val latestRefreshToken = authTokenProvider.getRefreshToken()

            if (!latestAccessToken.isNullOrBlank() && latestAccessToken != requestAccessToken) {
                proceed(
                    HttpRequestBuilder().apply {
                        takeFrom(request)
                        attributes.put(RefreshAttemptedKey, true)
                        headers.remove(HttpHeaders.Authorization)
                        header(HttpHeaders.Authorization, "Bearer $latestAccessToken")
                    }
                )
            } else {
                if (latestRefreshToken.isNullOrBlank()) {
                    sessionExpiryHandler.onSessionExpired()
                    return@on originalCall
                }
                when (
                    val refreshResult = tokenRefreshHandler.refreshTokens(
                        refreshToken = latestRefreshToken,
                        accessToken = requestAccessToken,
                    )
                ) {
                    is ApiResult.Success -> proceed(
                        HttpRequestBuilder().apply {
                            takeFrom(request)
                            attributes.put(RefreshAttemptedKey, true)
                            headers.remove(HttpHeaders.Authorization)
                            header(HttpHeaders.Authorization, "Bearer ${refreshResult.value.accessToken}")
                        }
                    )
                    is ApiResult.Failure -> {
                        sessionExpiryHandler.onSessionExpired()
                        originalCall
                    }
                }
            }
        }
    }
    install(authRefreshPlugin)
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, request ->
            val responseException = cause as? ResponseException ?: return@handleResponseExceptionWithRequest
            val isFinalUnauthorized = request.attributes.getOrNull(SkipAuthenticationKey) != true &&
                request.attributes.getOrNull(RefreshAttemptedKey) == true &&
                responseException.response.status == HttpStatusCode.Unauthorized
            if (isFinalUnauthorized) {
                sessionExpiryHandler.onSessionExpired()
            }
        }
    }
}

expect fun platformHttpClientEngineFactory(): HttpClientEngineFactory<*>
