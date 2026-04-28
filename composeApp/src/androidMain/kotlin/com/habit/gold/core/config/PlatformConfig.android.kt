package com.habit.gold.core.config

import com.habit.gold.BuildConfig

actual fun platformAppConfig(): AppConfig = AppConfig(
    appName = BuildConfig.APP_NAME,
    bundleId = BuildConfig.APPLICATION_ID,
    environment = AppEnvironment.from(BuildConfig.APP_ENV),
    baseUrl = BuildConfig.API_BASE_URL,
    enableNetworkLogs = BuildConfig.ENABLE_NETWORK_LOGS,
)
