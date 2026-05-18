package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import kotlinx.serialization.json.JsonElement

class GetDeliveryProductsUseCase(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(): Result<JsonElement> {
        return repository.getDeliveryProducts()
    }
}
