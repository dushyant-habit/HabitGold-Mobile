package com.habit.gold.payments.juspay

import org.json.JSONObject

fun mergePreferredUpiIntoSdkPayload(
    sdkPayload: JSONObject,
    preferredPackage: String?,
): JSONObject {
    if (preferredPackage.isNullOrBlank()) return sdkPayload
    return try {
        val copy = JSONObject(sdkPayload.toString())
        val payload = copy.optJSONObject("payload") ?: JSONObject()
        payload.put("payWithApp", preferredPackage)
        copy.put("payload", payload)
        copy
    } catch (_: Exception) {
        sdkPayload
    }
}
