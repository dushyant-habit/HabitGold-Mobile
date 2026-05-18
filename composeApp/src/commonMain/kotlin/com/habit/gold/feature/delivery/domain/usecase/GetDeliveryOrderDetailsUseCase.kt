package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto

class GetDeliveryOrderDetailsUseCase(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: String): Result<DeliveryOrderDto> {
        return repository.getDeliveryOrderDetails(id)
    }
}
