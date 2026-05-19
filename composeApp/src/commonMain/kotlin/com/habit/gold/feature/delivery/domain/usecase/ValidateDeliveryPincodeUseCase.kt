package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import kotlinx.serialization.json.JsonObject

class ValidateDeliveryPincodeUseCase(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(pinCode: String, productWeightGrams: Double): Result<JsonObject> {
        return repository.validatePincode(pinCode, productWeightGrams)
    }
}
