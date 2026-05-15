package com.habit.gold.core.session

import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionStoreTest {

    @Test
    fun `restores authenticated ready session from storage`() = runBlocking {
        val sessionStore = SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        )
        val user = AuthenticatedUser(
            id = "user-1",
            phoneNumber = "9876543210",
            name = "Habit Gold",
            email = "team@habitgold.com",
            pinCode = "560001",
        )

        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = user,
            isProfileComplete = true,
            isPinCodeRequired = true,
        )

        val restored = sessionStore.restore()

        assertTrue(restored.isLoggedIn)
        assertEquals(SessionAuthState.AuthenticatedReady, restored.authState)
        assertEquals(AppStartupDestination.Home, restored.startupDestination)
        assertEquals("access-token", restored.accessToken)
        assertEquals(user, restored.user)
        assertTrue(restored.isPinCodeRequired)
    }

    @Test
    fun `restores incomplete profile destination when profile is not complete`() = runBlocking {
        val sessionStore = SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        )

        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = AuthenticatedUser(phoneNumber = "9876543210"),
            isProfileComplete = false,
            isPinCodeRequired = false,
        )

        val restored = sessionStore.restore()

        assertTrue(restored.isLoggedIn)
        assertEquals(SessionAuthState.AuthenticatedProfileIncomplete, restored.authState)
        assertEquals(AppStartupDestination.BasicInfo, restored.startupDestination)
        assertFalse(restored.isPinCodeRequired)
    }

    @Test
    fun `clears storage and returns to login state`() = runBlocking {
        val sessionStore = SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        )

        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = AuthenticatedUser(phoneNumber = "9876543210"),
            isProfileComplete = false,
            isPinCodeRequired = true,
        )

        sessionStore.clear()

        val restored = sessionStore.restore()

        assertFalse(restored.isLoggedIn)
        assertEquals(SessionAuthState.LoggedOut, restored.authState)
        assertEquals(AppStartupDestination.Login, restored.startupDestination)
        assertNull(restored.accessToken)
        assertNull(restored.user)
    }

    @Test
    fun `updates tokens without losing current user snapshot`() = runBlocking {
        val sessionStore = SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        )

        val user = AuthenticatedUser(
            id = "user-1",
            phoneNumber = "9876543210",
            name = "Habit Gold",
        )
        sessionStore.saveAuthenticatedUser(
            accessToken = "old-access",
            refreshToken = "old-refresh",
            user = user,
            isProfileComplete = true,
            isPinCodeRequired = false,
        )

        sessionStore.updateTokens(
            accessToken = "new-access",
            refreshToken = "new-refresh",
        )

        assertEquals("new-access", sessionStore.state.value.accessToken)
        assertEquals("new-refresh", sessionStore.state.value.refreshToken)
        assertEquals(user, sessionStore.state.value.user)
        assertTrue(sessionStore.state.value.isProfileComplete)
    }
}
