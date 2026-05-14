package com.habit.gold.core.session

import com.habit.gold.core.storage.AuthTokenStorage
import com.habit.gold.core.storage.AuthTokens
import com.habit.gold.core.storage.SessionMetadata
import com.habit.gold.core.storage.SessionMetadataStorage
import com.habit.gold.core.storage.UserProfileStorage
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SessionStore(
    private val authTokenStorage: AuthTokenStorage,
    private val userProfileStorage: UserProfileStorage,
    private val sessionMetadataStorage: SessionMetadataStorage,
) {
    private val _state = MutableStateFlow(AuthSession())
    val state: StateFlow<AuthSession> = _state.asStateFlow()

    /**
     * Rehydrates the shared session snapshot before app-shell routing decides which flow to show.
     */
    suspend fun restore(): AuthSession {
        val restoredSession = SessionSnapshot(
            tokens = authTokenStorage.readTokens(),
            user = userProfileStorage.readUser(),
            metadata = sessionMetadataStorage.readMetadata() ?: SessionMetadata(),
        ).toAuthSession()
        _state.value = restoredSession
        return restoredSession
    }

    fun createStartupState(): AppStartupState {
        return AppStartupState.Ready(
            destination = state.value.startupDestination,
            session = state.value,
        )
    }

    /**
     * Persists the authenticated session atomically so both Android and iOS see the same auth state.
     */
    suspend fun saveAuthenticatedUser(
        accessToken: String,
        refreshToken: String,
        user: AuthenticatedUser,
        isProfileComplete: Boolean,
    ) {
        val session = AuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user,
            isLoggedIn = true,
            isProfileComplete = isProfileComplete,
        )
        authTokenStorage.writeTokens(
            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        )
        userProfileStorage.writeUser(user)
        sessionMetadataStorage.writeMetadata(SessionMetadata(isProfileComplete = isProfileComplete))
        _state.value = session
    }

    suspend fun updateProfile(
        user: AuthenticatedUser,
        isProfileComplete: Boolean,
    ) {
        userProfileStorage.writeUser(user)
        sessionMetadataStorage.writeMetadata(SessionMetadata(isProfileComplete = isProfileComplete))
        _state.update { current ->
            current.copy(
                user = user,
                isLoggedIn = true,
                isProfileComplete = isProfileComplete,
            )
        }
    }

    /**
     * Clears every persisted auth artifact when logout or session expiry invalidates the account.
     */
    suspend fun clear() {
        authTokenStorage.clearTokens()
        userProfileStorage.clearUser()
        sessionMetadataStorage.clearMetadata()
        _state.value = AuthSession()
    }
}
