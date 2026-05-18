package com.habit.gold.feature.delivery.data

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.DeliveryAddressDto
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import com.habit.gold.feature.delivery.domain.model.VerifyAddressOtpDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

private val addressJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

internal fun parseAddressList(element: JsonElement): List<DeliveryAddressDto> {
    val array: JsonArray = when (element) {
        is JsonArray -> element
        is JsonObject -> {
            val data = element["data"] ?: element["addresses"] ?: element["items"] ?: element["results"]
            (data as? JsonArray) ?: return emptyList()
        }
        else -> return emptyList()
    }

    return array.mapNotNull { item ->
        runCatching {
            addressJson.decodeFromJsonElement(DeliveryAddressDto.serializer(), item)
        }.getOrNull()
    }
}

class AddressRepositoryImpl(
    private val httpClient: HttpClient
) : AddressRepository {

    override suspend fun listAddresses(): Result<List<DeliveryAddressDto>> =
        safeApiCall {
            val response = httpClient.get("user/addresses").body<JsonElement>()
            parseAddressList(response)
        }.toResult("Could not load addresses")

    override suspend fun createAddress(body: CreateAddressDto): Result<JsonObject> =
        safeApiCall {
            httpClient.post("user/addresses") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<JsonObject>()
        }.toResult("Could not save address")

    override suspend fun getAddress(id: String): Result<DeliveryAddressDto> =
        safeApiCall {
            val obj = httpClient.get("user/addresses/$id").body<JsonObject>()
            val payload = obj["data"] ?: obj
            addressJson.decodeFromJsonElement(DeliveryAddressDto.serializer(), payload)
        }.toResult("Could not load address")

    override suspend fun updateAddress(id: String, body: UpdateAddressDto): Result<JsonObject> =
        safeApiCall {
            httpClient.patch("user/addresses/$id") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<JsonObject>()
        }.toResult("Could not update address")

    override suspend fun deleteAddress(id: String): Result<Unit> =
        safeApiCall {
            httpClient.delete("user/addresses/$id")
            Unit
        }.toResult("Could not delete address")

    override suspend fun sendAddressOtp(id: String): Result<JsonObject> =
        safeApiCall {
            httpClient.post("user/addresses/$id/send-otp").body<JsonObject>()
        }.toResult("Could not send OTP")

    override suspend fun verifyAddressOtp(id: String, body: VerifyAddressOtpDto): Result<JsonObject> =
        safeApiCall {
            httpClient.post("user/addresses/$id/verify-otp") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<JsonObject>()
        }.toResult("OTP verification failed")

    override suspend fun checkAddressServiceability(id: String): Result<JsonObject> =
        safeApiCall {
            httpClient.post("user/addresses/$id/check-serviceability").body<JsonObject>()
        }.toResult("Could not verify delivery serviceability")

    private fun <T> ApiResult<T>.toResult(fallbackMessage: String): Result<T> =
        when (this) {
            is ApiResult.Success -> Result.success(value)
            is ApiResult.Failure -> {
                val errorMessage = error.message.takeIf { it.isNotBlank() } ?: fallbackMessage
                Result.failure(Exception(errorMessage, error.cause))
            }
        }
}
