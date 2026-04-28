package com.habit.gold.core.di

import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.platformAppConfig
import com.habit.gold.core.network.BootstrapRepository
import com.habit.gold.core.network.BootstrapRepositoryImpl
import com.habit.gold.core.network.createHttpClient
import com.habit.gold.core.util.AppDispatchers
import com.habit.gold.getPlatformInfo
import io.ktor.client.HttpClient
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform
import org.koin.dsl.module

fun startKoinIfNeeded(
    appConfig: AppConfig = platformAppConfig(),
    platformInfo: PlatformInfo = getPlatformInfo(),
): Koin {
    KoinPlatform.getKoinOrNull()?.let { return it }

    return startKoin {
        modules(
            module {
                single { appConfig }
                single { platformInfo }
                single { AppDispatchers() }
                single<HttpClient> { createHttpClient(get()) }
                single<BootstrapRepository> { BootstrapRepositoryImpl(get(), get(), get()) }
            }
        )
    }.koin
}
