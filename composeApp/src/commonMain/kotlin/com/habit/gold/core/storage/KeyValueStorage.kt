package com.habit.gold.core.storage

interface KeyValueStorage {
    suspend fun read(key: String): String?
    suspend fun write(key: String, value: String)
    suspend fun delete(key: String)
    suspend fun clear()
}
