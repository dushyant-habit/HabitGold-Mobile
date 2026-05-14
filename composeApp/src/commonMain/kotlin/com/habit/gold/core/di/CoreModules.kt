package com.habit.gold.core.di

import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.SessionExpiryHandler
import com.habit.gold.core.network.TokenRefreshHandler
import com.habit.gold.core.network.UnsupportedTokenRefreshHandler
import com.habit.gold.core.network.createHttpClient
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.core.storage.AuthTokenStorage
import com.habit.gold.core.storage.InMemoryAppPreferencesStorage
import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.core.storage.SecureStorage
import com.habit.gold.core.storage.SessionMetadataStorage
import com.habit.gold.core.storage.UserProfileStorage
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
    single<SecureStorage> { InMemorySecureStorage() }
    single<AuthTokenStorage> { SecureAuthTokenStorage(get()) }
    single<UserProfileStorage> { InMemoryUserProfileStorage() }
    single<SessionMetadataStorage> { InMemorySessionMetadataStorage() }
    single<AppPreferencesStorage> { InMemoryAppPreferencesStorage() }
    single { SessionStore(get(), get(), get()) }
    single<AuthTokenProvider> {
        object : AuthTokenProvider {
            override fun getAccessToken(): String? = get<SessionStore>().state.value.accessToken
            override fun getRefreshToken(): String? = get<SessionStore>().state.value.refreshToken
        }
    }
    single<SessionExpiryHandler> {
        object : SessionExpiryHandler {
            override suspend fun onSessionExpired() {
                get<SessionStore>().clear()
            }
        }
    }
    single<TokenRefreshHandler> { UnsupportedTokenRefreshHandler() }
}

val networkModule = module {
    single<HttpClient> { createHttpClient(get(), get(), get(), get()) }
}
