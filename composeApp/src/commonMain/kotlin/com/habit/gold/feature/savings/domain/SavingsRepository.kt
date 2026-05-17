package com.habit.gold.feature.savings.domain

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.savings.domain.model.SavingsCreateMandateRequest
import com.habit.gold.feature.savings.domain.model.SavingsExecution
import com.habit.gold.feature.savings.domain.model.SavingsMandate
import com.habit.gold.feature.savings.domain.model.SavingsMandateSession

interface SavingsRepository {
    suspend fun createMandateSession(request: SavingsCreateMandateRequest): ApiResult<SavingsMandateSession>

    suspend fun updateMandateSession(
        mandateId: String,
        request: SavingsCreateMandateRequest,
    ): ApiResult<SavingsMandateSession>

    suspend fun getMandates(): ApiResult<List<SavingsMandate>>

    suspend fun getMandate(mandateId: String): ApiResult<SavingsMandate>

    suspend fun getExecutionHistory(mandateId: String): ApiResult<List<SavingsExecution>>

    suspend fun pauseMandate(mandateId: String): ApiResult<Unit>

    suspend fun resumeMandate(mandateId: String): ApiResult<Unit>

    suspend fun cancelMandate(mandateId: String): ApiResult<Unit>
}
