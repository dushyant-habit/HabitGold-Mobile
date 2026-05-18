package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.DeliveryInvoiceResponseDto

class GetDeliveryOrderInvoiceUseCase(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: String): Result<DeliveryInvoiceResponseDto> {
        return repository.getDeliveryOrderInvoice(id)
    }
}
