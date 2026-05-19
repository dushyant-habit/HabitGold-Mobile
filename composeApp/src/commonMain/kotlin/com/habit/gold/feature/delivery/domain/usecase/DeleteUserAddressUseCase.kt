package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.AddressRepository

class DeleteUserAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteAddress(id)
    }
}
