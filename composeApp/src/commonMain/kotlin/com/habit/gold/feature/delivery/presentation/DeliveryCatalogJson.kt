package com.habit.gold.feature.delivery.presentation

import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal fun parseDeliveryProducts(payload: JsonElement): List<PhysicalCoin> {
    val array: JsonArray? = when (payload) {
        is JsonArray -> payload
        is JsonObject -> payload["data"] as? JsonArray
        else -> null
    }

    val list = array
        ?.mapNotNull { it as? JsonObject }
        ?.mapNotNull { obj ->
            val id = obj.string("productId")
                ?: obj.string("id")
                ?: obj.string("safeGoldProductId")
                ?: return@mapNotNull null
            val sku = obj.string("sku").orEmpty()
            val productName = obj.string("productName").orEmpty()
            val weight = obj.double("weight")
                ?: obj.double("weightGrams")
                ?: obj.double("productWeightGrams")
                ?: obj.double("weightGm")
                ?: return@mapNotNull null
            val making = obj.double("makingCharge") ?: 0.0
            val image = obj.string("image").orEmpty()
            val stamp = obj.string("metalStamp").orEmpty()
            val dispatchDays = obj.optInt("estimatedDispatchDays")
            PhysicalCoin(
                id = id,
                sku = sku,
                productName = productName.ifBlank { "${weight}g product" },
                weightGm = weight,
                makingCharge = making,
                imageUrl = image,
                metalStamp = stamp,
                estimatedDispatchDays = dispatchDays,
            )
        }
        ?: emptyList()

    return list.sortedWith(compareBy({ it.weightGm }, { it.productName }))
}

internal fun JsonObject.string(key: String): String? = (this[key] as? JsonPrimitive)?.content

private fun JsonObject.double(key: String): Double? {
    val primitive = this[key] as? JsonPrimitive ?: return null
    return primitive.content.toDoubleOrNull()
}

private fun JsonObject.optInt(key: String): Int? {
    val primitive = this[key] as? JsonPrimitive ?: return null
    return primitive.content.toIntOrNull()
}

internal fun JsonObject.dataPayloadOrSelf(): JsonObject =
    (this["data"] as? JsonObject) ?: this

internal fun JsonObject.extractAddressId(): String? {
    fun pick(payload: JsonObject): String? =
        (payload["id"] as? JsonPrimitive)?.content?.takeIf { it.isNotBlank() }
            ?: (payload["addressId"] as? JsonPrimitive)?.content?.takeIf { it.isNotBlank() }
    val root = dataPayloadOrSelf()
    return pick(root) ?: pick(this)
}

internal fun JsonObject.pincodeAccepted(): Boolean {
    val serviceable = this["serviceable"]
    if (serviceable is JsonPrimitive) {
        if (serviceable.isString) return serviceable.content.equals("true", ignoreCase = true)
        return serviceable.content.toBooleanStrictOrNull() ?: false
    }
    val status = (this["status"] as? JsonPrimitive)?.content
    return status?.equals("SERVICEABLE", ignoreCase = true) == true
        || status?.equals("SUCCESS", ignoreCase = true) == true
}
