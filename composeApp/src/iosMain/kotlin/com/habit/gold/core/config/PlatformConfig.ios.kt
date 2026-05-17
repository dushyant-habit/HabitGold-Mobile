package com.habit.gold.core.config

import platform.Foundation.NSBundle

actual fun platformAppConfig(): AppConfig = AppConfig(
    appName = NSBundle.mainBundle.objectForInfoDictionaryKey("APP_NAME") as? String
        ?: NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleDisplayName") as? String
        ?: NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleName") as? String
        ?: "HabitGold",
    bundleId = NSBundle.mainBundle.bundleIdentifier ?: "com.habit.gold",
    appVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        ?: "1.0",
    appPlatform = "ios",
    environment = AppEnvironment.from(
        NSBundle.mainBundle.objectForInfoDictionaryKey("APP_ENV") as? String ?: "prod"
    ),
    baseUrl = NSBundle.mainBundle.objectForInfoDictionaryKey("API_BASE_URL") as? String
        ?: "https://api.habitgold.com/v1/",
    enableNetworkLogs = (NSBundle.mainBundle.objectForInfoDictionaryKey("ENABLE_NETWORK_LOGS") as? String)
        ?.equals("YES", ignoreCase = true)
        ?: false,
)
