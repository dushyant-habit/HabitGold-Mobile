package com.habit.gold.core.config

actual fun platformAppConfig(): AppConfig = AppConfig(
    appName = "HabitGold",
    bundleId = "com.habit.gold",
    environment = AppEnvironment.Production,
    baseUrl = "https://api.habitgold.com/v1/",
    enableNetworkLogs = false,
)
