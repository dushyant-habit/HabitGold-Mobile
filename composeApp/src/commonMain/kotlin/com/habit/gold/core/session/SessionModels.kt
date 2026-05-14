package com.habit.gold.core.session

import com.habit.gold.core.storage.AuthTokens
import com.habit.gold.feature.auth.domain.AuthenticatedUser

enum class SessionAuthState {
    LoggedOut,
    AuthenticatedProfileIncomplete,
    AuthenticatedReady,
}

enum class AppStartupDestination {
    Login,
    BasicInfo,
    Home,
}

sealed interface AppStartupState {
    data object Loading : AppStartupState
    data class Ready(
        val destination: AppStartupDestination,
        val session: AuthSession,
    ) : AppStartupState
}

data class AuthSession(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val user: AuthenticatedUser? = null,
    val isLoggedIn: Boolean = false,
    val isProfileComplete: Boolean = false,
) {
    val authState: SessionAuthState
        get() = when {
            !isLoggedIn || accessToken.isNullOrBlank() || refreshToken.isNullOrBlank() -> SessionAuthState.LoggedOut
            isProfileComplete -> SessionAuthState.AuthenticatedReady
            else -> SessionAuthState.AuthenticatedProfileIncomplete
        }

    val startupDestination: AppStartupDestination
        get() = when (authState) {
            SessionAuthState.LoggedOut -> AppStartupDestination.Login
            SessionAuthState.AuthenticatedProfileIncomplete -> AppStartupDestination.BasicInfo
            SessionAuthState.AuthenticatedReady -> AppStartupDestination.Home
        }
}

data class SessionSnapshot(
    val tokens: AuthTokens? = null,
    val user: AuthenticatedUser? = null,
    val metadata: com.habit.gold.core.storage.SessionMetadata = com.habit.gold.core.storage.SessionMetadata(),
)

fun SessionSnapshot.toAuthSession(): AuthSession {
    return AuthSession(
        accessToken = tokens?.accessToken,
        refreshToken = tokens?.refreshToken,
        user = user,
        isLoggedIn = tokens != null,
        isProfileComplete = metadata.isProfileComplete,
    )
}
