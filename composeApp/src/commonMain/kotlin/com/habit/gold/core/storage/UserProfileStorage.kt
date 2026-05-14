package com.habit.gold.core.storage

import com.habit.gold.feature.auth.domain.AuthenticatedUser
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface UserProfileStorage {
    suspend fun readUser(): AuthenticatedUser?
    suspend fun writeUser(user: AuthenticatedUser)
    suspend fun clearUser()
}

class InMemoryUserProfileStorage : UserProfileStorage {
    private val mutex = Mutex()
    private var user: AuthenticatedUser? = null

    override suspend fun readUser(): AuthenticatedUser? = mutex.withLock {
        user
    }

    override suspend fun writeUser(user: AuthenticatedUser) {
        mutex.withLock {
            this.user = user
        }
    }

    override suspend fun clearUser() {
        mutex.withLock {
            user = null
        }
    }
}
