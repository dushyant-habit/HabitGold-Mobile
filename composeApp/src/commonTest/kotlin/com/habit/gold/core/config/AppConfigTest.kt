package com.habit.gold.core.config

import kotlin.test.Test
import kotlin.test.assertEquals

class AppConfigTest {

    @Test
    fun `normalizes trailing slash`() {
        val config = AppConfig(
            appName = "HabitGold",
            bundleId = "com.habit.gold",
            appVersion = "1.0",
            appPlatform = "android",
            environment = AppEnvironment.Production,
            baseUrl = "https://api.habitgold.com/v1/",
            enableNetworkLogs = false,
        )

        assertEquals("https://api.habitgold.com/v1/", config.normalizedBaseUrl)
    }

    @Test
    fun `maps common environment aliases`() {
        assertEquals(AppEnvironment.Development, AppEnvironment.from("debug"))
        assertEquals(AppEnvironment.Preprod, AppEnvironment.from("preprod"))
        assertEquals(AppEnvironment.Production, AppEnvironment.from("release"))
    }
}
