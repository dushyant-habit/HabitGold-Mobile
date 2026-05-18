package com.habit.gold.feature.alerts.data.repository

import com.habit.gold.core.storage.InMemoryAppPreferencesStorage
import com.habit.gold.feature.alerts.data.local.AlertsStorage
import com.habit.gold.feature.alerts.data.local.StoredAlert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AlertsRepositoryImplTest {
    @Test
    fun `mark all alerts read updates stored alerts and unread preference`() = runTest {
        val alertsStorage = InMemoryAlertsStorage(
            alerts = listOf(
                StoredAlert(
                    id = "a1",
                    title = "Buy complete",
                    description = "Order settled.",
                    createdAt = "2026-05-18T10:00:00Z",
                    isRead = false,
                ),
            ),
        )
        val preferencesStorage = InMemoryAppPreferencesStorage().also {
            it.writePreferences(it.readPreferences().copy(hasUnreadAlerts = true))
        }
        val repository = AlertsRepositoryImpl(
            alertsStorage = alertsStorage,
            appPreferencesStorage = preferencesStorage,
        )

        repository.markAllAlertsRead()

        assertTrue(alertsStorage.readAlerts().all { it.isRead })
        assertFalse(preferencesStorage.readPreferences().hasUnreadAlerts)
    }

    @Test
    fun `get alerts sorts newest first`() = runTest {
        val alertsStorage = InMemoryAlertsStorage(
            alerts = listOf(
                StoredAlert("old", "Old", "First", "2026-05-17T10:00:00Z", true),
                StoredAlert("new", "New", "Second", "2026-05-18T10:00:00Z", false),
            ),
        )
        val repository = AlertsRepositoryImpl(
            alertsStorage = alertsStorage,
            appPreferencesStorage = InMemoryAppPreferencesStorage(),
        )

        val alerts = repository.getAlerts()

        assertEquals(listOf("new", "old"), alerts.map { it.id })
    }
}

private class InMemoryAlertsStorage(
    private var alerts: List<StoredAlert>,
) : AlertsStorage {
    override suspend fun readAlerts(): List<StoredAlert> = alerts

    override suspend fun writeAlerts(alerts: List<StoredAlert>) {
        this.alerts = alerts
    }

    override suspend fun clearAlerts() {
        alerts = emptyList()
    }
}
