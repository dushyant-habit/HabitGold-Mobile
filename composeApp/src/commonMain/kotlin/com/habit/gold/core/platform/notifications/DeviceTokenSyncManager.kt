package com.habit.gold.core.platform.notifications

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.platform.PlatformBridgeStore
import com.habit.gold.core.session.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DeviceTokenSyncManager(
    private val httpClient: HttpClient,
    private val appConfig: AppConfig,
    private val sessionStore: SessionStore,
    private val platformBridgeStore: PlatformBridgeStore,
) {
    suspend fun registerCurrentTokenAfterLogin() {
        val token = platformBridgeStore.readCurrentDeviceToken() ?: return
        registerWithBackend(token = token, force = true)
    }

    suspend fun registerTokenIfLoggedIn(token: String) {
        platformBridgeStore.writeCurrentDeviceToken(token)
        if (!sessionStore.state.value.isLoggedIn) return
        registerWithBackend(token = token, force = false)
    }

    suspend fun unregisterCurrentTokenBeforeLogout() {
        val token = platformBridgeStore.readLastRegisteredDeviceToken()
            ?: platformBridgeStore.readCurrentDeviceToken()
            ?: return
        runCatching {
            httpClient.delete("user/device-token") {
                contentType(ContentType.Application.Json)
                setBody(UnregisterDeviceTokenDto(token))
            }
        }
        platformBridgeStore.writeLastRegisteredDeviceToken(null)
    }

    private suspend fun registerWithBackend(
        token: String,
        force: Boolean,
    ) {
        if (!force && platformBridgeStore.readLastRegisteredDeviceToken() == token) return
        runCatching {
            httpClient.post("user/device-token") {
                contentType(ContentType.Application.Json)
                setBody(
                    RegisterDeviceTokenDto(
                        token = token,
                        platform = appConfig.appPlatform.uppercase(),
                    )
                )
            }
        }.onSuccess {
            platformBridgeStore.writeLastRegisteredDeviceToken(token)
        }
    }
}

data class RegisterDeviceTokenDto(
    val token: String,
    val platform: String,
)

data class UnregisterDeviceTokenDto(
    val token: String,
)
