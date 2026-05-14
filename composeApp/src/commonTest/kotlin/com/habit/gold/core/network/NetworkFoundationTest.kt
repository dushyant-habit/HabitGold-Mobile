package com.habit.gold.core.network

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NetworkFoundationTest {

    @Test
    fun `maps malformed and structured backend error payloads`() {
        val objectPayload = mapHttpFailure(
            statusCode = 400,
            rawBody = """{"message":["Invalid OTP"],"code":"OTP_INVALID"}""",
            fallbackMessage = "Request failed",
        )
        val primitivePayload = mapHttpFailure(
            statusCode = 429,
            rawBody = """"Too many attempts"""",
            fallbackMessage = "Request failed",
        )
        val malformedPayload = mapHttpFailure(
            statusCode = 500,
            rawBody = "<<<bad-json>>>",
            fallbackMessage = "Request failed",
        )

        assertEquals(NetworkErrorKind.Validation, objectPayload.kind)
        assertEquals("Invalid OTP", objectPayload.message)
        assertEquals("OTP_INVALID", objectPayload.backendCode)
        assertEquals(NetworkErrorKind.RateLimited, primitivePayload.kind)
        assertEquals("Too many attempts", primitivePayload.message)
        assertEquals(NetworkErrorKind.Server, malformedPayload.kind)
        assertEquals("Something went wrong on our side. Please try again.", malformedPayload.message)
    }

    @Test
    fun `maps connectivity and timeout failures consistently`() = runBlocking {
        val connectivity = safeApiCall<String> { throw IOException("offline") }
        val timeout = safeApiCall<String> {
            throw HttpRequestTimeoutException("https://example.com", 15_000, null)
        }

        assertIs<ApiResult.Failure>(connectivity)
        assertEquals(NetworkErrorKind.Connectivity, connectivity.error.kind)
        assertTrue(connectivity.error.isRetryable)

        assertIs<ApiResult.Failure>(timeout)
        assertEquals(NetworkErrorKind.Timeout, timeout.error.kind)
        assertTrue(timeout.error.isRetryable)
    }

    @Test
    fun `adds auth header unless request explicitly skips authentication`() = runBlocking {
        val capturedRequests = mutableListOf<HttpRequestData>()
        val client = testClient(
            authTokenProvider = FakeAuthTokenProvider(
                accessToken = "access-token",
                refreshToken = "refresh-token",
            ),
            engine = MockEngine { request ->
                capturedRequests += request
                respond(
                    content = ByteReadChannel("""{"ok":true}"""),
                    status = HttpStatusCode.OK,
                    headers = io.ktor.http.headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        client.get("private/resource").bodyAsText()
        client.post("auth/send-otp") {
            skipAuthentication()
            contentType(ContentType.Application.Json)
            setBody("""{"mobileNumber":"+919876543210"}""")
        }.bodyAsText()

        assertEquals("Bearer access-token", capturedRequests[0].headers[HttpHeaders.Authorization])
        assertEquals(null, capturedRequests[1].headers[HttpHeaders.Authorization])
    }

    @Test
    fun `clears session when authenticated request returns unauthorized and refresh is unsupported`() = runBlocking {
        var expiredCalls = 0
        val client = testClient(
            authTokenProvider = FakeAuthTokenProvider(
                accessToken = "access-token",
                refreshToken = "refresh-token",
            ),
            sessionExpiryHandler = object : SessionExpiryHandler {
                override suspend fun onSessionExpired() {
                    expiredCalls += 1
                }
            },
            engine = MockEngine {
                respond(
                    content = ByteReadChannel("""{"message":"Unauthorized"}"""),
                    status = HttpStatusCode.Unauthorized,
                    headers = io.ktor.http.headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        val result = safeApiCall<String> { client.get("user/profile").body() }

        assertIs<ApiResult.Failure>(result)
        assertEquals(NetworkErrorKind.Unauthorized, result.error.kind)
        assertTrue(expiredCalls >= 1)
    }

    private fun testClient(
        authTokenProvider: AuthTokenProvider = FakeAuthTokenProvider(),
        sessionExpiryHandler: SessionExpiryHandler = object : SessionExpiryHandler {
            override suspend fun onSessionExpired() = Unit
        },
        tokenRefreshHandler: TokenRefreshHandler = UnsupportedTokenRefreshHandler(),
        engine: MockEngine,
    ): HttpClient {
        return HttpClient(engine) {
            applyHabitGoldHttpClientConfig(
                appConfig = AppConfig(
                    appName = "HabitGold",
                    bundleId = "com.habit.gold",
                    environment = AppEnvironment.Staging,
                    baseUrl = "https://api.habitgold.com/v1/",
                    enableNetworkLogs = false,
                ),
                authTokenProvider = authTokenProvider,
                sessionExpiryHandler = sessionExpiryHandler,
                tokenRefreshHandler = tokenRefreshHandler,
            )
        }
    }

    private data class FakeAuthTokenProvider(
        private val accessToken: String? = null,
        private val refreshToken: String? = null,
    ) : AuthTokenProvider {
        override fun getAccessToken(): String? = accessToken
        override fun getRefreshToken(): String? = refreshToken
    }
}
