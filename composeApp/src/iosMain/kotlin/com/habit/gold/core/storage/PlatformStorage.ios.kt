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
import platform.Foundation.NSUserDefaults
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

private const val SECURE_SERVICE_NAME = "com.habit.gold.secure"
private const val APP_SUITE_NAME = "com.habit.gold.app"
private const val KEY_REGISTRY_STORAGE_KEY = "secure.storage.keys"
private typealias CFDictionaryPointer = CPointer<__CFDictionary>
private typealias CFStringPointer = CPointer<__CFString>

actual fun createPlatformSecureStorage(): SecureStorage {
    return AppleKeychainStorage(
        registryDefaults = NSUserDefaults(suiteName = APP_SUITE_NAME),
    )
}

actual fun createPlatformPreferencesStorage(): KeyValueStorage {
    return AppleUserDefaultsStorage(
        userDefaults = NSUserDefaults(suiteName = APP_SUITE_NAME),
    )
}

private class AppleKeychainStorage(
    private val registryDefaults: NSUserDefaults,
) : SecureStorage {

    override suspend fun read(key: String): String? {
        val query = keychainQuery(
            kSecClass to cfObject(kSecClassGenericPassword),
            kSecAttrService to SECURE_SERVICE_NAME,
            kSecAttrAccount to key,
            kSecReturnData to cfObject(platform.CoreFoundation.kCFBooleanTrue),
            kSecMatchLimit to cfObject(kSecMatchLimitOne),
        )
        return memScoped {
            val result = alloc<COpaquePointerVar>()
            val status = SecItemCopyMatching(query, result.ptr.reinterpret())
            when (status) {
                errSecSuccess -> result.ptr.pointed.value
                    ?.let { interpretObjCPointerOrNull<NSData>(it.rawValue) }
                    ?.toUtf8String()
                errSecItemNotFound -> null
                else -> null
            }
        }
    }

    override suspend fun write(key: String, value: String) {
        deleteKeychainValue(key)
        val query = keychainQuery(
            kSecClass to cfObject(kSecClassGenericPassword),
            kSecAttrService to SECURE_SERVICE_NAME,
            kSecAttrAccount to key,
            kSecValueData to value.toNSData(),
            kSecAttrAccessible to cfObject(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly),
        )
        if (SecItemAdd(query, null) == errSecSuccess) {
            persistKnownKeys(knownKeys() + key)
        }
    }

    override suspend fun delete(key: String) {
        deleteKeychainValue(key)
        persistKnownKeys(knownKeys() - key)
    }

    override suspend fun clear() {
        val keys = knownKeys()
        keys.forEach(::deleteKeychainValue)
        persistKnownKeys(emptySet())
    }

    private fun deleteKeychainValue(key: String) {
        val query = keychainQuery(
            kSecClass to cfObject(kSecClassGenericPassword),
            kSecAttrService to SECURE_SERVICE_NAME,
            kSecAttrAccount to key,
        )
        SecItemDelete(query)
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

private fun keychainQuery(vararg entries: Pair<CFStringPointer?, Any?>): CFDictionaryPointer {
    val dictionary = NSMutableDictionary()
    entries.forEach { (key, value) ->
        val keyObject = cfStringObject(key)
        if (value != null) {
            dictionary.setObject(value, forKey = keyObject)
        }
    }
    return interpretCPointer(dictionary.objcPtr())
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
