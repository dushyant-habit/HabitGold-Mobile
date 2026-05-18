package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.model.DeliveryAddressDto

class GetUserAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(id: String): Result<DeliveryAddressDto> {
        return repository.getAddress(id)
    }
}
