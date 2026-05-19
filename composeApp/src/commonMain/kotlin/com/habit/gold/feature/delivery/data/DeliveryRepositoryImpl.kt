package com.habit.gold.feature.delivery.data

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.model.DeliveryInvoiceResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderConfirmResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class DeliveryRepositoryImpl(
    private val httpClient: HttpClient
) : DeliveryRepository {

    override suspend fun getDeliveryProducts(): Result<JsonElement> =
        safeApiCall {
            httpClient.get("delivery/products").body<JsonElement>()
        }.toResult()

    override suspend fun validatePincode(pinCode: String, productWeightGrams: Double): Result<JsonObject> =
        safeApiCall {
            httpClient.get("delivery/validate-pincode") {
                parameter("pinCode", pinCode)
                parameter("productWeightGrams", productWeightGrams)
            }.body<JsonObject>()
        }.toResult("Failed to validate pincode")

    override suspend fun createDeliveryQuote(body: CreateDeliveryQuoteDto): Result<DeliveryQuoteResponseDto> =
        safeApiCall {
            httpClient.post("delivery/quotes") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<DeliveryQuoteResponseDto>()
        }.toResult()

    override suspend fun confirmDeliveryOrder(
        idempotencyKey: String,
        body: ConfirmDeliveryOrderDto
    ): Result<DeliveryOrderConfirmResponseDto> =
        safeApiCall {
            httpClient.post("delivery/orders") {
                header("Idempotency-Key", idempotencyKey)
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<DeliveryOrderConfirmResponseDto>()
        }.toResult()

    override suspend fun listDeliveryOrders(): Result<JsonElement> =
        safeApiCall {
            httpClient.get("delivery/orders").body<JsonElement>()
        }.toResult()

    override suspend fun getDeliveryOrderDetails(id: String): Result<DeliveryOrderDto> =
        safeApiCall {
            httpClient.get("delivery/orders/$id").body<DeliveryOrderDto>()
        }.toResult()

    override suspend fun getDeliveryOrderInvoice(id: String): Result<DeliveryInvoiceResponseDto> =
        safeApiCall {
            httpClient.get("delivery/orders/$id/invoice").body<DeliveryInvoiceResponseDto>()
        }.toResult()

    private fun <T> ApiResult<T>.toResult(fallbackMessage: String? = null): Result<T> =
        when (this) {
            is ApiResult.Success -> Result.success(value)
            is ApiResult.Failure -> {
                val errorMessage = fallbackMessage ?: error.message
                Result.failure(Exception(errorMessage, error.cause))
            }
        }
}
