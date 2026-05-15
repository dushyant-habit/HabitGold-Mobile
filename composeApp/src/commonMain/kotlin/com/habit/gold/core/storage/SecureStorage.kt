package com.habit.gold.core.storage

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface SecureStorage : KeyValueStorage

class InMemorySecureStorage : SecureStorage {
    private val mutex = Mutex()
    private val values = mutableMapOf<String, String>()

    override suspend fun read(key: String): String? = mutex.withLock {
        values[key]
    }

    override suspend fun write(key: String, value: String) {
        mutex.withLock {
            values[key] = value
        }
    }

    override suspend fun delete(key: String) {
        mutex.withLock {
            values.remove(key)
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            values.clear()
        }
    }
}
