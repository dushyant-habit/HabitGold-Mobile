package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import kotlinx.serialization.json.JsonObject

class UpdateUserAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(id: String, body: UpdateAddressDto): Result<JsonObject> {
        return repository.updateAddress(id, body)
    }
}
