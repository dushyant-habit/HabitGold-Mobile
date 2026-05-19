package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.PostalPincodeRepository

class LookupPostalPincodeUseCase(
    private val repository: PostalPincodeRepository
) {
    suspend operator fun invoke(pincode: String): Result<Pair<String, String>> {
        return repository.lookupDistrictAndState(pincode)
    }
}
