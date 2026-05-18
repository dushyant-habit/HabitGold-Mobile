package com.habit.gold.feature.delivery.domain

import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.model.DeliveryInvoiceResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderConfirmResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface DeliveryRepository {
    suspend fun getDeliveryProducts(): Result<JsonElement>
    suspend fun validatePincode(pinCode: String, productWeightGrams: Double): Result<JsonObject>
    suspend fun createDeliveryQuote(body: CreateDeliveryQuoteDto): Result<DeliveryQuoteResponseDto>
    suspend fun confirmDeliveryOrder(
        idempotencyKey: String,
        body: ConfirmDeliveryOrderDto
    ): Result<DeliveryOrderConfirmResponseDto>
    suspend fun listDeliveryOrders(): Result<JsonElement>
    suspend fun getDeliveryOrderDetails(id: String): Result<DeliveryOrderDto>
    suspend fun getDeliveryOrderInvoice(id: String): Result<DeliveryInvoiceResponseDto>
}
