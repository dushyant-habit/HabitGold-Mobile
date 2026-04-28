package com.habit.gold.core.network

import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.BootstrapCheck
import com.habit.gold.core.config.BootstrapInfo
import io.ktor.client.HttpClient

interface BootstrapRepository {
    suspend fun loadBootstrapInfo(): BootstrapInfo
}

class BootstrapRepositoryImpl(
    private val appConfig: AppConfig,
    private val platformInfo: PlatformInfo,
    private val httpClient: HttpClient,
) : BootstrapRepository {

    override suspend fun loadBootstrapInfo(): BootstrapInfo {
        return BootstrapInfo(
            appConfig = appConfig,
            platformInfo = platformInfo,
            checks = listOf(
                BootstrapCheck(
                    title = "Shared UI and state",
                    detail = "Compose Multiplatform screens, view models, and state live in common code.",
                ),
                BootstrapCheck(
                    title = "Dependency graph",
                    detail = "Koin is initialized once per platform and provides shared services cleanly.",
                ),
                BootstrapCheck(
                    title = "HTTP client",
                    detail = "Ktor is configured with platform engines, JSON serialization, and request defaults.",
                ),
                BootstrapCheck(
                    title = "Environment config",
                    detail = "Android build types and iOS defaults expose a single AppConfig contract.",
                ),
                BootstrapCheck(
                    title = "Release posture",
                    detail = "Android release builds now enable shrinking, backup hardening, and network security defaults.",
                ),
            ),
            nextSteps = listOf(
                "Add feature modules for auth, portfolio, trading, and notifications.",
                "Introduce secure token storage for Keychain and Android Keystore-backed persistence.",
                "Connect repositories to real backend endpoints and add integration tests.",
                "Add CI for Android compile, iOS framework build, unit tests, and static checks.",
            ),
        )
    }
}
