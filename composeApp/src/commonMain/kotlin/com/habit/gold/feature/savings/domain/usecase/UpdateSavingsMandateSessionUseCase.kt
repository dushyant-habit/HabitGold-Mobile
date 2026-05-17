package com.habit.gold.feature.savings.domain.usecase

import com.habit.gold.feature.savings.domain.SavingsRepository
import com.habit.gold.feature.savings.domain.model.SavingsCreateMandateRequest

class UpdateSavingsMandateSessionUseCase(
    private val repository: SavingsRepository,
) {
    suspend operator fun invoke(
        mandateId: String,
        request: SavingsCreateMandateRequest,
    ) = repository.updateMandateSession(mandateId, request)
}
