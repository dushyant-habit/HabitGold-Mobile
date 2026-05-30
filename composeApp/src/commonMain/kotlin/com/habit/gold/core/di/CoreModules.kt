package com.habit.gold.core.di

import com.habit.gold.PlatformInfo
import com.habit.gold.app.AuthenticatedSessionResetManager
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.SessionExpiryHandler
import com.habit.gold.core.network.createHttpClient
import com.habit.gold.core.platform.PlatformBridgeStore
import com.habit.gold.core.platform.notifications.DeviceTokenSyncManager
import com.habit.gold.core.storage.KeyValueStorage
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.core.storage.AuthTokenStorage
import com.habit.gold.core.storage.JsonAppPreferencesStorage
import com.habit.gold.core.storage.JsonSessionMetadataStorage
import com.habit.gold.core.storage.JsonUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.core.storage.SecureStorage
import com.habit.gold.core.storage.SessionMetadataStorage
import com.habit.gold.core.storage.UserProfileStorage
import com.habit.gold.core.storage.createPlatformPreferencesStorage
import com.habit.gold.core.storage.createPlatformSecureStorage
import com.habit.gold.core.util.AppDispatchers
import io.ktor.client.HttpClient
import org.koin.dsl.module

fun coreModule(
    appConfig: AppConfig,
    platformInfo: PlatformInfo,
) = module {
    single { appConfig }
    single { platformInfo }
    single { AppDispatchers() }
    single<SecureStorage> { createPlatformSecureStorage() }
    single<KeyValueStorage> { createPlatformPreferencesStorage() }
    single<AuthTokenStorage> { SecureAuthTokenStorage(get()) }
    single<UserProfileStorage> { JsonUserProfileStorage(get()) }
    single<SessionMetadataStorage> { JsonSessionMetadataStorage(get()) }
    single<AppPreferencesStorage> { JsonAppPreferencesStorage(get()) }
    single { PlatformBridgeStore(get()) }
    single { SessionStore(get(), get(), get()) }
    single { DeviceTokenSyncManager(get(), get(), get(), get()) }
    single { AuthenticatedSessionResetManager(get(), get(), get(), get(), get()) }
    single<AuthTokenProvider> {
        object : AuthTokenProvider {
            override fun getAccessToken(): String? = get<SessionStore>().state.value.accessToken
            override fun getRefreshToken(): String? = get<SessionStore>().state.value.refreshToken
        }
    }
    single<SessionExpiryHandler> {
        object : SessionExpiryHandler {
            override suspend fun onSessionExpired() {
                get<AuthenticatedSessionResetManager>().reset()
            }
        }
    }
}

val networkModule = module {
    single<HttpClient> { createHttpClient(get(), get(), get(), get()) }
}
