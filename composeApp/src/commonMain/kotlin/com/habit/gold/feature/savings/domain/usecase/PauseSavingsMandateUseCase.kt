package com.habit.gold.feature.savings.domain.usecase

import com.habit.gold.feature.savings.domain.SavingsRepository

class PauseSavingsMandateUseCase(
    private val repository: SavingsRepository,
) {
    suspend operator fun invoke(mandateId: String) = repository.pauseMandate(mandateId)
}
