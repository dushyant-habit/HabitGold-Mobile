@file:OptIn(ExperimentalForeignApi::class)

package com.habit.gold.core.storage

import cnames.structs.__CFDictionary
import cnames.structs.__CFString
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.interpretObjCPointerOrNull
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeNullPtr
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataCreate
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSCopyingProtocol
import platform.Foundation.NSBundle
import platform.Foundation.NSLog
import platform.Foundation.NSUserDefaults
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecDuplicateItem
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

private const val SECURE_SERVICE_NAME = "com.habit.gold.secure"
private const val SECURE_SUITE_NAME = "com.habit.gold.secure"
private const val APP_SUITE_NAME = "com.habit.gold.app"
private const val KEY_REGISTRY_STORAGE_KEY = "secure.storage.keys"
private typealias CFStringPointer = CPointer<__CFString>
private val shouldLogKeychainDiagnostics: Boolean
    get() = ((NSBundle.mainBundle.objectForInfoDictionaryKey("APP_ENV") as? String) ?: "prod")
        .trim()
        .lowercase() != "prod"

actual fun createPlatformSecureStorage(): SecureStorage {
    return AppleKeychainStorage(
        registryDefaults = resolveUserDefaults(APP_SUITE_NAME),
        legacySecureDefaults = resolveUserDefaults(SECURE_SUITE_NAME),
    )
}

actual fun createPlatformPreferencesStorage(): KeyValueStorage {
    return AppleUserDefaultsStorage(userDefaults = resolveUserDefaults(APP_SUITE_NAME))
}

private class AppleKeychainStorage(
    private val registryDefaults: NSUserDefaults,
    private val legacySecureDefaults: NSUserDefaults,
) : SecureStorage {

    override suspend fun read(key: String): String? {
        val legacyValue = legacySecureDefaults.stringForKey(key)
        return when (val result = readKeychainValue(key)) {
            is KeychainReadResult.Success -> result.value ?: legacyValue
            KeychainReadResult.Missing -> legacyValue
            is KeychainReadResult.Failure -> {
                if (legacyValue == null && shouldLogKeychainDiagnostics) {
                    NSLog("Keychain read failed for key=$key status=${result.status}")
                }
                legacyValue
            }
        }
    }

    override suspend fun write(key: String, value: String) {
        legacySecureDefaults.setObject(value, forKey = key)
        when (val status = upsertKeychainValue(key, value.toNSData())) {
            errSecSuccess -> persistKnownKeys(knownKeys() + key)
            else -> if (shouldLogKeychainDiagnostics) {
                NSLog("Keychain write failed for key=$key status=$status")
            }
        }
    }

    override suspend fun delete(key: String) {
        deleteKeychainValue(key)
        legacySecureDefaults.removeObjectForKey(key)
        persistKnownKeys(knownKeys() - key)
    }

    override suspend fun clear() {
        val keys = knownKeys()
        keys.forEach(::deleteKeychainValue)
        val legacyKeys = legacySecureDefaults.dictionaryRepresentation().keys
        for (legacyKey in legacyKeys) {
            val stringKey = legacyKey as? String ?: continue
            legacySecureDefaults.removeObjectForKey(stringKey)
        }
        persistKnownKeys(emptySet())
    }

    private fun deleteKeychainValue(key: String) {
        val query = keychainItemQuery(key)
        SecItemDelete(query.asCFDictionary())
    }

    private fun readKeychainValue(key: String): KeychainReadResult = memScoped {
        val result = alloc<COpaquePointerVar>()
        val query = keychainItemQuery(
            key = key,
            kSecReturnData to cfObject(platform.CoreFoundation.kCFBooleanTrue),
            kSecMatchLimit to cfObject(kSecMatchLimitOne),
        )
        when (val status = SecItemCopyMatching(query.asCFDictionary(), result.ptr.reinterpret())) {
            errSecSuccess -> {
                val data = result.ptr.pointed.value
                    ?.let { interpretObjCPointerOrNull<NSData>(it.rawValue) }
                KeychainReadResult.Success(data?.toUtf8String())
            }
            errSecItemNotFound -> KeychainReadResult.Missing
            else -> KeychainReadResult.Failure(status)
        }
    }

    private fun upsertKeychainValue(key: String, value: NSData): Int {
        val lookupQuery = keychainItemQuery(key)
        val addQuery = keychainItemQuery(
            key = key,
            kSecValueData to value,
            kSecAttrAccessible to cfObject(kSecAttrAccessibleAfterFirstUnlock),
        )
        return when (val addStatus = SecItemAdd(addQuery.asCFDictionary(), null)) {
            errSecSuccess -> errSecSuccess
            errSecDuplicateItem -> {
                val updateAttributes = keychainQuery(
                    kSecValueData to value,
                    kSecAttrAccessible to cfObject(kSecAttrAccessibleAfterFirstUnlock),
                )
                SecItemUpdate(
                    lookupQuery.asCFDictionary(),
                    updateAttributes.asCFDictionary(),
                )
            }
            else -> addStatus
        }
    }

    private sealed interface KeychainReadResult {
        data class Success(val value: String?) : KeychainReadResult
        data object Missing : KeychainReadResult
        data class Failure(val status: Int) : KeychainReadResult
    }

    private fun knownKeys(): Set<String> {
        val raw = registryDefaults.stringForKey(KEY_REGISTRY_STORAGE_KEY).orEmpty()
        if (raw.isBlank()) return emptySet()
        return raw.split('|')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .toSet()
    }

    private fun persistKnownKeys(keys: Set<String>) {
        val encoded = keys.sorted().joinToString("|")
        registryDefaults.setObject(encoded, forKey = KEY_REGISTRY_STORAGE_KEY)
    }
}

private fun resolveUserDefaults(suiteName: String): NSUserDefaults {
    return try {
        val suite = NSUserDefaults(suiteName = suiteName)
        suite.dictionaryRepresentation()
        suite
    } catch (e: Throwable) {
        NSUserDefaults.standardUserDefaults
    }
}

private fun keychainItemQuery(
    key: String,
    vararg entries: Pair<CFStringPointer?, Any?>,
): NSMutableDictionary {
    return keychainQuery(
        kSecClass to cfObject(kSecClassGenericPassword),
        kSecAttrService to SECURE_SERVICE_NAME,
        kSecAttrAccount to key,
        *entries,
    )
}

private fun keychainQuery(vararg entries: Pair<CFStringPointer?, Any?>): NSMutableDictionary {
    val dictionary = NSMutableDictionary()
    entries.forEach { (key, value) ->
        val keyObject = cfStringObject(key)
        if (value != null) {
            dictionary.setObject(value, forKey = keyObject)
        }
    }
    return dictionary
}

private fun NSMutableDictionary.asCFDictionary(): CPointer<__CFDictionary> {
    return interpretCPointer(objcPtr())
        ?: error("Failed to bridge NSMutableDictionary to CFDictionary for Keychain.")
}

private fun cfObject(pointer: CPointer<*>?): Any {
    return interpretObjCPointerOrNull<Any>(pointer?.rawValue ?: nativeNullPtr)
        ?: error("Failed to bridge CoreFoundation constant into Objective-C object.")
}

private fun cfStringObject(pointer: CFStringPointer?): NSCopyingProtocol {
    return interpretObjCPointerOrNull<NSCopyingProtocol>(pointer?.rawValue ?: nativeNullPtr)
        ?: error("Failed to bridge CoreFoundation string key into Objective-C object.")
}

private class AppleUserDefaultsStorage(
    private val userDefaults: NSUserDefaults,
) : KeyValueStorage {

    override suspend fun read(key: String): String? = userDefaults.stringForKey(key)

    override suspend fun write(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    override suspend fun delete(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    override suspend fun clear() {
        val keys = userDefaults.dictionaryRepresentation().keys
        for (key in keys) {
            val stringKey = key as? String ?: continue
            userDefaults.removeObjectForKey(stringKey)
        }
    }
}

private fun String.toNSData(): NSData {
    val bytes = encodeToByteArray()
    return bytes.usePinned { pinned ->
        val cfData = CFDataCreate(
            allocator = null,
            bytes = pinned.addressOf(0).reinterpret(),
            length = bytes.size.toLong(),
        )
        interpretObjCPointerOrNull<NSData>(cfData!!.rawValue)
            ?: error("Failed to create CFData for Keychain storage.")
    }
}

private fun NSData.toUtf8String(): String? {
    val rawBytes = bytes?.reinterpret<ByteVar>() ?: return null
    return rawBytes.readBytes(length.toInt()).decodeToString()
}
