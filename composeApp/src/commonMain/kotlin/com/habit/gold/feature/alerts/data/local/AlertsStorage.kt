package com.habit.gold.feature.alerts.data.local

import com.habit.gold.core.storage.KeyValueStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private const val ALERTS_STORAGE_KEY = "alerts.items"

@Serializable
data class StoredAlert(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val isRead: Boolean = false,
)

interface AlertsStorage {
    suspend fun readAlerts(): List<StoredAlert>
    suspend fun writeAlerts(alerts: List<StoredAlert>)
    suspend fun clearAlerts()
}

class JsonAlertsStorage(
    private val storage: KeyValueStorage,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    },
) : AlertsStorage {
    override suspend fun readAlerts(): List<StoredAlert> {
        val raw = storage.read(ALERTS_STORAGE_KEY) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(StoredAlert.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    override suspend fun writeAlerts(alerts: List<StoredAlert>) {
        storage.write(
            key = ALERTS_STORAGE_KEY,
            value = json.encodeToString(ListSerializer(StoredAlert.serializer()), alerts),
        )
    }

    override suspend fun clearAlerts() {
        storage.delete(ALERTS_STORAGE_KEY)
    }
}
