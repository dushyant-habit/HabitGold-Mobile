package com.habit.gold

import com.habit.gold.core.di.startKoinIfNeeded
import com.habit.gold.core.platform.PlatformBridgeStore
import com.habit.gold.core.platform.extractReferralCodeFromUrl
import com.habit.gold.core.platform.notifications.DeviceTokenSyncManager
import com.habit.gold.core.platform.notifications.PlatformAlertRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private object IosRuntimeScope {
    val value = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

object IosPlatformRuntimeBridge {
    fun registerCurrentFcmToken(token: String) {
        val normalized = token.trim()
        if (normalized.isEmpty()) return
        IosRuntimeScope.value.launch {
            startKoinIfNeeded().get<DeviceTokenSyncManager>().registerTokenIfLoggedIn(normalized)
        }
    }

    fun persistReferralUrl(rawUrl: String) {
        val referralCode = extractReferralCodeFromUrl(rawUrl) ?: return
        IosRuntimeScope.value.launch {
            startKoinIfNeeded().get<PlatformBridgeStore>().writePendingReferralCode(referralCode)
        }
    }

    fun recordAlert(title: String, description: String) {
        IosRuntimeScope.value.launch {
            startKoinIfNeeded().get<PlatformAlertRecorder>().record(title, description)
        }
    }
}
