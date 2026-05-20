package com.habit.gold.core.platform.notifications

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.platform.PlatformBridgeStore
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.KeyValueStorage
import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeviceTokenSyncManagerTest {

    @Test
    fun `register token while logged out stores current token without backend call`() = runTest {
        val capturedRequests = mutableListOf<HttpRequestData>()
        val store = PlatformBridgeStore(InMemoryTestKeyValueStorage())
        val manager = DeviceTokenSyncManager(
            httpClient = testClient(capturedRequests) {
                respondOk()
            },
            appConfig = testAppConfig(),
            sessionStore = testSessionStore(loggedIn = false),
            platformBridgeStore = store,
        )

        manager.registerTokenIfLoggedIn("token-1")

        assertEquals("token-1", store.readCurrentDeviceToken())
        assertNull(store.readLastRegisteredDeviceToken())
        assertEquals(0, capturedRequests.size)
    }

    @Test
    fun `register current token after login posts backend and tracks last registered token`() = runTest {
        val capturedRequests = mutableListOf<HttpRequestData>()
        val store = PlatformBridgeStore(InMemoryTestKeyValueStorage()).also {
            it.writeCurrentDeviceToken("token-2")
        }
        val manager = DeviceTokenSyncManager(
            httpClient = testClient(capturedRequests) {
                respondOk()
            },
            appConfig = testAppConfig(),
            sessionStore = testSessionStore(loggedIn = true),
            platformBridgeStore = store,
        )

        manager.registerCurrentTokenAfterLogin()

        assertEquals(1, capturedRequests.size)
        assertTrue(capturedRequests.single().url.encodedPath.endsWith("/user/device-token"))
        assertEquals("token-2", store.readLastRegisteredDeviceToken())
    }

    @Test
    fun `unregister failure keeps last registered token for retry`() = runTest {
        val store = PlatformBridgeStore(InMemoryTestKeyValueStorage()).also {
            it.writeLastRegisteredDeviceToken("token-3")
        }
        val manager = DeviceTokenSyncManager(
            httpClient = testClient(mutableListOf()) {
                respond(
                    content = ByteReadChannel("""{"message":"server error"}"""),
                    status = HttpStatusCode.InternalServerError,
                    headers = io.ktor.http.headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
            appConfig = testAppConfig(),
            sessionStore = testSessionStore(loggedIn = true),
            platformBridgeStore = store,
        )

        manager.unregisterCurrentTokenBeforeLogout()

        assertEquals("token-3", store.readLastRegisteredDeviceToken())
    }

    private fun testClient(
        capturedRequests: MutableList<HttpRequestData>,
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): HttpClient {
        return HttpClient(MockEngine { request ->
            capturedRequests += request
            handler(request)
        }) {
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                url("https://staging.habitgold.com/v1/")
            }
        }
    }

    private fun MockRequestHandleScope.respondOk() = respond(
        content = ByteReadChannel("""{"ok":true}"""),
        status = HttpStatusCode.OK,
        headers = io.ktor.http.headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

    private fun testAppConfig() = AppConfig(
        appName = "HabitGold",
        bundleId = "com.habit.gold",
        appVersion = "1.0.19",
        appPlatform = "ios",
        environment = AppEnvironment.Staging,
        baseUrl = "https://staging.habitgold.com/v1/",
        enableNetworkLogs = false,
    )

    private suspend fun testSessionStore(loggedIn: Boolean): SessionStore {
        return SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        ).also { store ->
            if (loggedIn) {
                store.saveAuthenticatedUser(
                    accessToken = "access-token",
                    refreshToken = "refresh-token",
                    user = AuthenticatedUser(
                        id = "user-1",
                        phoneNumber = "9876543210",
                        name = "Dushyant",
                        email = "d@example.com",
                        pinCode = "110001",
                    ),
                    isProfileComplete = true,
                    isPinCodeRequired = false,
                )
            } else {
                store.restore()
            }
        }
    }
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
