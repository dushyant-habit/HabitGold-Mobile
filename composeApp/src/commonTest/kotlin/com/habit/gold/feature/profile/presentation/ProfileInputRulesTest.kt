package com.habit.gold.feature.profile.presentation

import com.habit.gold.core.storage.InMemorySecureStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfileInputRulesTest {

    @Test
    fun `normalizePan uppercases and trims to ten characters`() {
        assertEquals("ABCDE1234F", ProfileInputRules.normalizePan("ab-cde1234f999"))
    }

    @Test
    fun `nominee phone validation requires ten digits`() {
        assertTrue(ProfileInputRules.isNomineePhoneValid("9876543210"))
        assertFalse(ProfileInputRules.isNomineePhoneValid("987654321"))
    }

    @Test
    fun `profile security store persists biometric flag`() = runTest {
        val store = ProfileSecurityStore(InMemorySecureStorage())

        store.save(biometricEnabled = true)

        val stored = store.read()
        assertTrue(stored.biometricEnabled)
    }
}
