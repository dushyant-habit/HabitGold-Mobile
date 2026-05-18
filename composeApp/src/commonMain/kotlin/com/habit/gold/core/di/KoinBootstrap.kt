package com.habit.gold.core.di

import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.platformAppConfig
import com.habit.gold.feature.alerts.di.alertsModule
import com.habit.gold.feature.auth.di.authModule
import com.habit.gold.feature.home.di.homeModule
import com.habit.gold.feature.profile.di.profileModule
import com.habit.gold.feature.rewards.di.rewardsModule
import com.habit.gold.feature.savings.di.savingsModule
import com.habit.gold.feature.trade.di.tradeModule
import com.habit.gold.getPlatformInfo
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun startKoinIfNeeded(
    appConfig: AppConfig = platformAppConfig(),
    platformInfo: PlatformInfo = getPlatformInfo(),
): Koin {
    KoinPlatform.getKoinOrNull()?.let { return it }

    return startKoin {
        modules(
            coreModule(appConfig, platformInfo),
            networkModule,
            alertsModule,
            authModule,
            homeModule,
            profileModule,
            rewardsModule,
            savingsModule,
            tradeModule,
        )
    }.koin
}
