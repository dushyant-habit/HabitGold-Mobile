package com.habit.gold.app

import com.habit.gold.core.platform.notifications.DeviceTokenSyncManager
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.feature.alerts.data.local.AlertsStorage
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStore

class AuthenticatedSessionResetManager(
    private val deviceTokenSyncManager: DeviceTokenSyncManager,
    private val sessionStore: SessionStore,
    private val appPreferencesStorage: AppPreferencesStorage,
    private val alertsStorage: AlertsStorage,
    private val pendingDeliveryCheckoutStore: PendingDeliveryCheckoutStore,
) {
    suspend fun reset() {
        deviceTokenSyncManager.unregisterCurrentTokenBeforeLogout()
        sessionStore.clear()
        appPreferencesStorage.clearPreferences()
        alertsStorage.clearAlerts()
        pendingDeliveryCheckoutStore.clear()
    }
}
