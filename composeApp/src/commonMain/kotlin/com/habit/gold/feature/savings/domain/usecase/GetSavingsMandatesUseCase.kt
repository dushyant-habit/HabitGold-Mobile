package com.habit.gold.feature.savings.domain.usecase

import com.habit.gold.feature.savings.domain.SavingsRepository

class GetSavingsMandatesUseCase(
    private val repository: SavingsRepository,
) {
    suspend operator fun invoke() = repository.getMandates()
}
