package com.habit.gold.feature.savings.domain.usecase

import com.habit.gold.feature.savings.domain.SavingsRepository

class GetSavingsMandateUseCase(
    private val repository: SavingsRepository,
) {
    suspend operator fun invoke(mandateId: String) = repository.getMandate(mandateId)
}
