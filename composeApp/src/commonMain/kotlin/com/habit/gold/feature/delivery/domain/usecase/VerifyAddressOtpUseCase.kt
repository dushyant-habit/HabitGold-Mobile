package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.model.VerifyAddressOtpDto
import kotlinx.serialization.json.JsonObject

class VerifyAddressOtpUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(id: String, body: VerifyAddressOtpDto): Result<JsonObject> {
        return repository.verifyAddressOtp(id, body)
    }
}
