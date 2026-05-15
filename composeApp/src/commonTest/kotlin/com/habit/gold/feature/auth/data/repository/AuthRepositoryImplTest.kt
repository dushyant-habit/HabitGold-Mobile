package com.habit.gold.feature.auth.data.repository

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.SessionExpiryHandler
import com.habit.gold.core.network.UnsupportedTokenRefreshHandler
import com.habit.gold.core.network.applyHabitGoldHttpClientConfig
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.feature.auth.data.remote.AuthRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AuthRepositoryImplTest {

    @Test
    fun `verifyOtp persists incomplete onboarding session with backend pincode rule`() = runBlocking {
        val sessionStore = createSessionStore()
        val repository = createRepository(sessionStore) { request ->
            when {
                request.url.encodedPath.endsWith("/auth/verify-otp") -> jsonResponse(
                    """
                    {
                      "accessToken":"access-token",
                      "refreshToken":"refresh-token",
                      "user":{"id":"user-1","mobileNumber":"9876543210"},
                      "newUser":true,
                      "showOnboarding":true,
                      "pincodeRequired":false
                    }
                    """.trimIndent(),
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.verifyOtp("9876543210", "123456")

        val success = assertIs<ApiResult.Success<*>>(result)
        val verifyResult = success.value as com.habit.gold.feature.auth.domain.VerifyOtpResult
        assertTrue(verifyResult.requiresBasicDetails)
        assertEquals(false, verifyResult.isPinCodeRequired)
        assertTrue(sessionStore.state.value.isLoggedIn)
        assertEquals(false, sessionStore.state.value.isProfileComplete)
        assertEquals(false, sessionStore.state.value.isPinCodeRequired)
    }

    @Test
    fun `verifyOtp uses fresh token for profile fetch and falls back safely on profile failure`() = runBlocking {
        val sessionStore = createSessionStore()
        val capturedRequests = mutableListOf<HttpRequestData>()
        val repository = createRepository(sessionStore) { request ->
            capturedRequests += request
            when {
                request.url.encodedPath.endsWith("/auth/verify-otp") -> jsonResponse(
                    """
                    {
                      "accessToken":"access-token",
                      "refreshToken":"refresh-token",
                      "user":{"id":"user-1","mobileNumber":"9876543210"},
                      "newUser":false,
                      "showOnboarding":false,
                      "pincodeRequired":true
                    }
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/user/profile") -> respond(
                    content = ByteReadChannel("""{"message":"Profile unavailable"}"""),
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.verifyOtp("9876543210", "123456")

        val success = assertIs<ApiResult.Success<*>>(result)
        val verifyResult = success.value as com.habit.gold.feature.auth.domain.VerifyOtpResult
        assertEquals(false, verifyResult.requiresBasicDetails)
        assertEquals("Bearer access-token", capturedRequests.last().headers[HttpHeaders.Authorization])
        assertTrue(sessionStore.state.value.isProfileComplete)
        assertEquals("9876543210", sessionStore.state.value.user?.phoneNumber)
    }

    @Test
    fun `submitBasicDetails updates session after successful referral submit`() = runBlocking {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = com.habit.gold.feature.auth.domain.AuthenticatedUser(phoneNumber = "9876543210"),
            isProfileComplete = false,
            isPinCodeRequired = true,
        )
        var referralCalls = 0
        val repository = createRepository(sessionStore) { request ->
            when {
                request.url.encodedPath.endsWith("/user/profile") -> jsonResponse(
                    """
                    {
                      "id":"user-1",
                      "name":"Dushyant Mainwal",
                      "mobileNumber":"9876543210",
                      "pinCode":"110001"
                    }
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/user/referral") -> {
                    referralCalls += 1
                    jsonResponse("""{"ok":true}""")
                }
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.submitBasicDetails(
            name = "Dushyant Mainwal",
            pinCode = "110001",
            referralCode = "friend1",
        )

        assertIs<ApiResult.Success<*>>(result)
        assertEquals(1, referralCalls)
        assertTrue(sessionStore.state.value.isProfileComplete)
        assertEquals("Dushyant Mainwal", sessionStore.state.value.user?.name)
        assertEquals("110001", sessionStore.state.value.user?.pinCode)
    }

    @Test
    fun `submitBasicDetails fails when referral submit fails and does not mark profile complete`() = runBlocking {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = com.habit.gold.feature.auth.domain.AuthenticatedUser(phoneNumber = "9876543210"),
            isProfileComplete = false,
            isPinCodeRequired = true,
        )
        val repository = createRepository(sessionStore) { request ->
            when {
                request.url.encodedPath.endsWith("/user/profile") -> jsonResponse(
                    """
                    {
                      "id":"user-1",
                      "name":"Dushyant Mainwal",
                      "mobileNumber":"9876543210",
                      "pinCode":"110001"
                    }
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/user/referral") -> respond(
                    content = ByteReadChannel("""{"message":"Referral code invalid"}"""),
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.submitBasicDetails(
            name = "Dushyant Mainwal",
            pinCode = "110001",
            referralCode = "friend1",
        )

        val failure = assertIs<ApiResult.Failure>(result)
        assertEquals("Referral code invalid", failure.error.message)
        assertEquals(false, sessionStore.state.value.isProfileComplete)
    }

    private fun createRepository(
        sessionStore: SessionStore,
        engineHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): AuthRepositoryImpl {
        val client = HttpClient(
            MockEngine { request ->
                engineHandler(request)
            },
        ) {
            applyHabitGoldHttpClientConfig(
                appConfig = AppConfig(
                    appName = "HabitGold",
                    bundleId = "com.habit.gold",
                    appVersion = "1.0-debug",
                    appPlatform = "android",
                    environment = AppEnvironment.Staging,
                    baseUrl = "https://api.habitgold.com/v1/",
                    enableNetworkLogs = false,
                ),
                authTokenProvider = object : AuthTokenProvider {
                    override fun getAccessToken(): String? = sessionStore.state.value.accessToken
                    override fun getRefreshToken(): String? = sessionStore.state.value.refreshToken
                },
                sessionExpiryHandler = object : SessionExpiryHandler {
                    override suspend fun onSessionExpired() {
                        sessionStore.clear()
                    }
                },
                tokenRefreshHandler = UnsupportedTokenRefreshHandler(),
            )
        }
        return AuthRepositoryImpl(
            remoteDataSource = AuthRemoteDataSource(client),
            sessionStore = sessionStore,
        )
    }

    private fun createSessionStore(): SessionStore {
        return SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        )
    }

    private fun MockRequestHandleScope.jsonResponse(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) = respond(
        content = ByteReadChannel(body),
        status = status,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )
}
