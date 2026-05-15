package com.habit.gold.core.storage

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class SessionMetadata(
    val isProfileComplete: Boolean = false,
    val isPinCodeRequired: Boolean = true,
)

interface SessionMetadataStorage {
    suspend fun readMetadata(): SessionMetadata?
    suspend fun writeMetadata(metadata: SessionMetadata)
    suspend fun clearMetadata()
}

class InMemorySessionMetadataStorage : SessionMetadataStorage {
    private val mutex = Mutex()
    private var metadata: SessionMetadata? = null

    override suspend fun readMetadata(): SessionMetadata? = mutex.withLock {
        metadata
    }

    override suspend fun writeMetadata(metadata: SessionMetadata) {
        mutex.withLock {
            this.metadata = metadata
        }
    }

    override suspend fun clearMetadata() {
        mutex.withLock {
            metadata = null
        }
    }
}
