package com.habit.gold.core.network

import com.habit.gold.core.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val SkipAuthenticationKey = AttributeKey<Boolean>("skip-authentication")
private const val SkipAuthenticationHeader = "X-HabitGold-Skip-Authentication"

fun HttpRequestBuilder.skipAuthentication() {
    attributes.put(SkipAuthenticationKey, true)
    headers.append(SkipAuthenticationHeader, "true")
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
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                isLenient = true
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
        level = if (appConfig.enableNetworkLogs) LogLevel.INFO else LogLevel.NONE
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
    }
    install(DefaultRequest) {
        url(appConfig.normalizedBaseUrl)
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        header(HttpHeaders.UserAgent, "${appConfig.appName}/${appConfig.environment.name}")
        val shouldSkipAuthentication = attributes.getOrNull(SkipAuthenticationKey) == true ||
            headers[SkipAuthenticationHeader] == "true"
        headers.remove(SkipAuthenticationHeader)
        val accessToken = authTokenProvider.getAccessToken()
        if (!shouldSkipAuthentication && !accessToken.isNullOrBlank()) {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
    }
    @Suppress("UNUSED_VARIABLE")
    val refreshBoundary = tokenRefreshHandler
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, request ->
            val responseException = cause as? ResponseException ?: return@handleResponseExceptionWithRequest
            val isAuthenticatedRequest = request.attributes.getOrNull(SkipAuthenticationKey) != true
            if (isAuthenticatedRequest && responseException.response.status == HttpStatusCode.Unauthorized) {
                sessionExpiryHandler.onSessionExpired()
            }
        }
    }
    expectSuccess = true
}

expect fun platformHttpClientEngineFactory(): HttpClientEngineFactory<*>
