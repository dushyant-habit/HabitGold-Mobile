package com.habit.gold.feature.delivery.domain

import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.DeliveryAddressDto
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import com.habit.gold.feature.delivery.domain.model.VerifyAddressOtpDto
import kotlinx.serialization.json.JsonObject

interface AddressRepository {
    suspend fun listAddresses(): Result<List<DeliveryAddressDto>>
    suspend fun createAddress(body: CreateAddressDto): Result<JsonObject>
    suspend fun getAddress(id: String): Result<DeliveryAddressDto>
    suspend fun updateAddress(id: String, body: UpdateAddressDto): Result<JsonObject>
    suspend fun deleteAddress(id: String): Result<Unit>
    suspend fun sendAddressOtp(id: String): Result<JsonObject>
    suspend fun verifyAddressOtp(id: String, body: VerifyAddressOtpDto): Result<JsonObject>
    suspend fun checkAddressServiceability(id: String): Result<JsonObject>
}
