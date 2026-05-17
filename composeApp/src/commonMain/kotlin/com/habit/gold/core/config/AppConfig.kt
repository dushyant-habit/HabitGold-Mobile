package com.habit.gold.core.config

enum class AppEnvironment {
    Development,
    Staging,
    Production,
    Custom,
    ;

    companion object {
        fun from(rawValue: String): AppEnvironment {
            return when (rawValue.trim().lowercase()) {
                "dev", "debug", "development" -> Development
                "stage", "staging", "qa", "preprod" -> Staging
                "prod", "production", "release" -> Production
                else -> Custom
            }
        }
    }
}

data class AppConfig(
    val appName: String,
    val bundleId: String,
    val appVersion: String,
    val appPlatform: String,
    val environment: AppEnvironment,
    val baseUrl: String,
    val enableNetworkLogs: Boolean,
) {
    val normalizedBaseUrl: String = normalizeBaseUrl(baseUrl)
    val normalizedAppVersion: String = appVersion.trim().substringBefore("-")
    val normalizedAppPlatform: String = appPlatform.trim().lowercase()

    init {
        require(normalizedBaseUrl.startsWith("https://")) {
            "AppConfig.baseUrl must use HTTPS. Found: $normalizedBaseUrl"
        }
        require(normalizedAppVersion.isNotBlank()) {
            "AppConfig.appVersion must not be blank."
        }
        require(normalizedAppPlatform.isNotBlank()) {
            "AppConfig.appPlatform must not be blank."
        }
    }
}

fun normalizeBaseUrl(rawValue: String): String {
    return rawValue.trim().removeSuffix("/") + "/"
}

expect fun platformAppConfig(): AppConfig
