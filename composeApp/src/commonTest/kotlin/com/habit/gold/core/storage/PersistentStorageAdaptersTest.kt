package com.habit.gold.core.storage

import com.habit.gold.feature.auth.domain.AuthenticatedUser
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersistentStorageAdaptersTest {

    @Test
    fun jsonUserProfileStorage_roundTrips_user_snapshot() = runTest {
        val storage = JsonUserProfileStorage(TestKeyValueStorage())
        val expected = AuthenticatedUser(
            id = "user-1",
            phoneNumber = "9876543210",
            name = "Dushyant Mainwal",
            email = "dushyant@habit.gold",
            pinCode = "110001",
        )

        storage.writeUser(expected)

        assertEquals(expected, storage.readUser())
        storage.clearUser()
        assertNull(storage.readUser())
    }

    @Test
    fun jsonSessionMetadataStorage_roundTrips_metadata() = runTest {
        val storage = JsonSessionMetadataStorage(TestKeyValueStorage())
        val expected = SessionMetadata(
            isProfileComplete = true,
            isPinCodeRequired = false,
        )

        storage.writeMetadata(expected)

        assertEquals(expected, storage.readMetadata())
        storage.clearMetadata()
        assertNull(storage.readMetadata())
    }

    @Test
    fun jsonAppPreferencesStorage_returns_defaults_when_empty_or_invalid() = runTest {
        val backend = TestKeyValueStorage()
        val storage = JsonAppPreferencesStorage(backend)

        assertEquals(AppPreferences(), storage.readPreferences())

        backend.write("app.preferences", "not-json")

        assertEquals(AppPreferences(), storage.readPreferences())
    }

    private class TestKeyValueStorage : KeyValueStorage {
        private val values = mutableMapOf<String, String>()

        override suspend fun read(key: String): String? = values[key]

        override suspend fun write(key: String, value: String) {
            values[key] = value
        }

        override suspend fun delete(key: String) {
            values.remove(key)
        }

        override suspend fun clear() {
            values.clear()
        }
    }
}
