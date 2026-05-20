package com.habit.gold.feature.profile.data.repository

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.SessionExpiryHandler
import com.habit.gold.core.network.UnsupportedTokenRefreshHandler
import com.habit.gold.core.network.applyHabitGoldHttpClientConfig
import com.habit.gold.core.platform.PlatformBridgeStore
import com.habit.gold.core.platform.notifications.DeviceTokenSyncManager
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.KeyValueStorage
import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.feature.profile.data.remote.ProfileRemoteDataSource
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

class ProfileRepositoryImplTest {

    @Test
    fun `getProfileSummary maps dto and refreshes session snapshot`() = runBlocking {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = com.habit.gold.feature.auth.domain.AuthenticatedUser(
                id = "seed-user",
                phoneNumber = "9876543210",
                name = "Seed Name",
            ),
            isProfileComplete = true,
            isPinCodeRequired = false,
        )
        val repository = createRepository(sessionStore) { request ->
            when {
                request.url.encodedPath.endsWith("/user/profile") -> jsonResponse(
                    """
                    {
                      "id":"user-1",
                      "name":"Dushyant Mainwal",
                      "email":"dushyant@habitgold.com",
                      "mobileNumber":"9876543210",
                      "pinCode":"110001",
                      "kycStatus":"VERIFIED",
                      "nominee":{"name":"Aarav","relation":"Brother","mobileNumber":"9876500000"},
                      "vpas":[{"id":"vpa-1","vpa":"dushyant@upi","isDefault":true,"verified":true}]
                    }
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/portfolio") -> jsonResponse(
                    """{"totalGoldBalanceGrams":"1.2500"}"""
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.getProfileSummary()

        val summary = assertIs<ApiResult.Success<*>>(result).value as com.habit.gold.feature.profile.domain.model.ProfileSummary
        assertEquals("Dushyant Mainwal", summary.user.name)
        assertEquals("VERIFIED", summary.user.kycStatus)
        assertEquals(1.25, summary.totalGoldBalanceGrams)
        assertEquals("Dushyant Mainwal", sessionStore.state.value.user?.name)
        assertEquals("dushyant@habitgold.com", sessionStore.state.value.user?.email)
    }

    @Test
    fun `logout clears session even when backend request fails`() = runBlocking {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = com.habit.gold.feature.auth.domain.AuthenticatedUser(phoneNumber = "9876543210"),
            isProfileComplete = true,
            isPinCodeRequired = false,
        )
        val repository = createRepository(sessionStore) { request ->
            when {
                request.url.encodedPath.endsWith("/auth/logout") -> respond(
                    content = ByteReadChannel("""{"message":"Server unavailable"}"""),
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.logout()

        assertIs<ApiResult.Success<*>>(result)
        assertTrue(!sessionStore.state.value.isLoggedIn)
    }

    @Test
    fun `requestDeleteAccount clears session only on success`() = runBlocking {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = com.habit.gold.feature.auth.domain.AuthenticatedUser(phoneNumber = "9876543210"),
            isProfileComplete = true,
            isPinCodeRequired = false,
        )
        val repository = createRepository(sessionStore) { request ->
            when {
                request.url.encodedPath.endsWith("/user/account/delete-request") -> jsonResponse("""{"ok":true}""")
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.requestDeleteAccount()

        assertIs<ApiResult.Success<*>>(result)
        assertTrue(!sessionStore.state.value.isLoggedIn)
    }

    @Test
    fun `updateProfile succeeds with normalized date of birth`() {
        runBlocking {
            val sessionStore = createSessionStore()
            val repository = createRepository(sessionStore) { request ->
                when {
                    request.url.encodedPath.endsWith("/user/profile") -> {
                        jsonResponse(
                            """
                            {
                              "id":"user-1",
                              "name":"Dushyant Mainwal",
                              "email":"dushyant@habitgold.com",
                              "mobileNumber":"9876543210",
                              "dateOfBirth":"1994-07-03",
                              "gender":"MALE"
                            }
                            """.trimIndent(),
                        )
                    }
                    else -> error("Unexpected path: ${request.url.encodedPath}")
                }
            }

            val result = repository.updateProfile(
                name = "Dushyant Mainwal",
                email = "dushyant@habitgold.com",
                dateOfBirth = "03/07/1994",
                gender = "male",
                nominee = null,
            )

            assertIs<ApiResult.Success<*>>(result)
        }
    }

    private fun createRepository(
        sessionStore: SessionStore,
        engineHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): ProfileRepositoryImpl {
        val client = HttpClient(MockEngine { request -> engineHandler(request) }) {
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
        return ProfileRepositoryImpl(
            remoteDataSource = ProfileRemoteDataSource(client),
            sessionStore = sessionStore,
            deviceTokenSyncManager = DeviceTokenSyncManager(
                httpClient = client,
                appConfig = AppConfig(
                    appName = "HabitGold",
                    bundleId = "com.habit.gold",
                    appVersion = "1.0-debug",
                    appPlatform = "android",
                    environment = AppEnvironment.Staging,
                    baseUrl = "https://api.habitgold.com/v1/",
                    enableNetworkLogs = false,
                ),
                sessionStore = sessionStore,
                platformBridgeStore = PlatformBridgeStore(InMemoryTestKeyValueStorage()),
            ),
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

private class InMemoryTestKeyValueStorage : KeyValueStorage {
    private val values = mutableMapOf<String, String>()

    override suspend fun read(key: String): String? = values[key]

    override suspend fun write(key: String, value: String) {
        values[key] = value
    }

    override suspend fun delete(key: String) {
        values.remove(key)
    }

    override suspend fun clear() {
        values.clear()
    }
}
