package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class ListDeliveryOrdersUseCase(
    private val repository: DeliveryRepository,
    private val json: Json = Json { ignoreUnknownKeys = true; isLenient = true }
) {
    suspend operator fun invoke(): Result<List<DeliveryOrderDto>> {
        return repository.listDeliveryOrders().mapCatching { element ->
            val array = when (element) {
                is JsonArray -> element
                is JsonObject -> element["data"] as? JsonArray ?: JsonArray(emptyList())
                else -> JsonArray(emptyList())
            }
            array.map { json.decodeFromJsonElement<DeliveryOrderDto>(it) }
        }
    }
}
