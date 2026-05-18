package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.AddressRepository
import kotlinx.serialization.json.JsonObject

class CheckAddressServiceabilityUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(id: String): Result<JsonObject> {
        return repository.checkAddressServiceability(id)
    }
}
