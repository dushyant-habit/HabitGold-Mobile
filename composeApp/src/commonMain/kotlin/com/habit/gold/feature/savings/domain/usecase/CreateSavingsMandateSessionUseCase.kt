package com.habit.gold.feature.savings.domain.usecase

import com.habit.gold.feature.savings.domain.SavingsRepository
import com.habit.gold.feature.savings.domain.model.SavingsCreateMandateRequest

class CreateSavingsMandateSessionUseCase(
    private val repository: SavingsRepository,
) {
    suspend operator fun invoke(request: SavingsCreateMandateRequest) =
        repository.createMandateSession(request)
}
