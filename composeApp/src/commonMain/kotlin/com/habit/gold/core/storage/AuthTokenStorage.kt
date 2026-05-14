package com.habit.gold.core.storage

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

interface AuthTokenStorage {
    suspend fun readTokens(): AuthTokens?
    suspend fun writeTokens(tokens: AuthTokens)
    suspend fun clearTokens()
}

class SecureAuthTokenStorage(
    private val secureStorage: SecureStorage,
) : AuthTokenStorage {

    override suspend fun readTokens(): AuthTokens? {
        val accessToken = secureStorage.read(ACCESS_TOKEN_KEY)
        val refreshToken = secureStorage.read(REFRESH_TOKEN_KEY)
        return if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            null
        } else {
            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }
    }

    override suspend fun writeTokens(tokens: AuthTokens) {
        secureStorage.write(ACCESS_TOKEN_KEY, tokens.accessToken)
        secureStorage.write(REFRESH_TOKEN_KEY, tokens.refreshToken)
    }

    override suspend fun clearTokens() {
        secureStorage.delete(ACCESS_TOKEN_KEY)
        secureStorage.delete(REFRESH_TOKEN_KEY)
    }

    private companion object {
        const val ACCESS_TOKEN_KEY = "auth.access_token"
        const val REFRESH_TOKEN_KEY = "auth.refresh_token"
    }
}
