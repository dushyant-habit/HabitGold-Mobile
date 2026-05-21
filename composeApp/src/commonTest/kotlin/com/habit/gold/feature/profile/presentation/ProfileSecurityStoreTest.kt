package com.habit.gold.feature.profile.presentation

import com.habit.gold.core.storage.InMemorySecureStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileSecurityStoreTest {

    @Test
    fun `set biometric enabled persists secure flag`() = runTest {
        val storage = InMemorySecureStorage()
        val store = ProfileSecurityStore(storage)

        store.setBiometricEnabled(true)

        assertEquals(
            ProfileSecuritySettings(biometricEnabled = true),
            store.read(),
        )
    }

    @Test
    fun `clear removes biometric flag`() = runTest {
        val storage = InMemorySecureStorage()
        val store = ProfileSecurityStore(storage)

        store.save(biometricEnabled = true)
        store.clear()

        assertEquals(
            ProfileSecuritySettings(biometricEnabled = false),
            store.read(),
        )
    }
}
