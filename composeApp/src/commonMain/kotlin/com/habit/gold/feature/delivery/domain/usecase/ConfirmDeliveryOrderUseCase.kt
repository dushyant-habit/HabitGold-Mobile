package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderConfirmResponseDto

class ConfirmDeliveryOrderUseCase(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(idempotencyKey: String, body: ConfirmDeliveryOrderDto): Result<DeliveryOrderConfirmResponseDto> {
        return repository.confirmDeliveryOrder(idempotencyKey, body)
    }
}
