package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import kotlinx.serialization.json.JsonObject

class CreateUserAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(body: CreateAddressDto): Result<JsonObject> {
        return repository.createAddress(body)
    }
}
