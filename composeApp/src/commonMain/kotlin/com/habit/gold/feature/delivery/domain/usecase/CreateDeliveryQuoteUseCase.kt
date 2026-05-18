package com.habit.gold.feature.delivery.domain.usecase

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto

class CreateDeliveryQuoteUseCase(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(body: CreateDeliveryQuoteDto): Result<DeliveryQuoteResponseDto> {
        return repository.createDeliveryQuote(body)
    }
}
