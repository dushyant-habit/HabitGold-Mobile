package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.model.DeliveryAddressDto

class ListUserAddressesUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(): Result<List<DeliveryAddressDto>> {
        return repository.listAddresses()
    }
}
