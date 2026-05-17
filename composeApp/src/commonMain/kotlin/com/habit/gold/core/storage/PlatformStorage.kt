package com.habit.gold.core.storage

expect fun createPlatformSecureStorage(): SecureStorage

expect fun createPlatformPreferencesStorage(): KeyValueStorage
