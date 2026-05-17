package com.habit.gold.feature.savings.data.repository

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.savings.data.model.SavingsCreateMandateRequestDto
import com.habit.gold.feature.savings.data.model.SavingsCreateMandateResponseDto
import com.habit.gold.feature.savings.data.model.SavingsExecutionDto
import com.habit.gold.feature.savings.data.model.SavingsMandateBillingDto
import com.habit.gold.feature.savings.data.model.SavingsMandateDto
import com.habit.gold.feature.savings.data.remote.SavingsRemoteDataSource
import com.habit.gold.feature.savings.domain.SavingsRepository
import com.habit.gold.feature.savings.domain.model.SavingsCreateMandateRequest
import com.habit.gold.feature.savings.domain.model.SavingsExecution
import com.habit.gold.feature.savings.domain.model.SavingsMandate
import com.habit.gold.feature.savings.domain.model.SavingsMandateBilling
import com.habit.gold.feature.savings.domain.model.SavingsMandateSession

class SavingsRepositoryImpl(
    private val remoteDataSource: SavingsRemoteDataSource,
) : SavingsRepository {
    override suspend fun createMandateSession(request: SavingsCreateMandateRequest): ApiResult<SavingsMandateSession> {
        return when (
            val result = remoteDataSource.createMandateSession(request.toDto())
        ) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun updateMandateSession(
        mandateId: String,
        request: SavingsCreateMandateRequest,
    ): ApiResult<SavingsMandateSession> {
        return when (
            val result = remoteDataSource.updateMandateSession(mandateId = mandateId, body = request.toDto())
        ) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun getMandates(): ApiResult<List<SavingsMandate>> {
        return when (val result = remoteDataSource.getMandates()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.map(SavingsMandateDto::toDomain))
        }
    }

    override suspend fun getMandate(mandateId: String): ApiResult<SavingsMandate> {
        return when (val result = remoteDataSource.getMandate(mandateId)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun getExecutionHistory(mandateId: String): ApiResult<List<SavingsExecution>> {
        return when (val result = remoteDataSource.getExecutionHistory(mandateId)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.map(SavingsExecutionDto::toDomain))
        }
    }

    override suspend fun pauseMandate(mandateId: String): ApiResult<Unit> = remoteDataSource.pauseMandate(mandateId)

    override suspend fun resumeMandate(mandateId: String): ApiResult<Unit> = remoteDataSource.resumeMandate(mandateId)

    override suspend fun cancelMandate(mandateId: String): ApiResult<Unit> = remoteDataSource.cancelMandate(mandateId)
}

private fun SavingsCreateMandateRequest.toDto(): SavingsCreateMandateRequestDto {
    return SavingsCreateMandateRequestDto(
        amount = amount,
        frequency = frequency,
        name = name,
        goalType = goalType,
        executionDay = executionDay,
        promoCode = promoCode?.trim()?.takeIf { it.isNotEmpty() },
    )
}

private fun SavingsCreateMandateResponseDto.toDomain(): SavingsMandateSession {
    return SavingsMandateSession(
        mandateId = mandateId,
        sdkPayloadJson = sdk_payload?.toString(),
    )
}

private fun SavingsMandateBillingDto.toDomain(): SavingsMandateBilling {
    return SavingsMandateBilling(
        executionId = executionId,
        executionStatus = executionStatus,
        nextExecutionOrderId = nextExecutionOrderId,
        nextExecutionAmount = nextExecutionAmount,
        currentAmount = currentAmount,
        amountUpdatedAt = amountUpdatedAt,
        needsAttention = needsAttention,
    )
}

private fun SavingsMandateDto.toDomain(): SavingsMandate {
    return SavingsMandate(
        id = id,
        userId = userId,
        name = name,
        amount = amount,
        frequency = frequency,
        startDate = startDate,
        status = status,
        juspayMandateId = juspayMandateId,
        promoCode = promoCode,
        nextExecutionDate = nextExecutionDate,
        billingCurrentAmount = billingCurrentAmount,
        billingNextExecutionAmount = billingNextExecutionAmount,
        billingLastEventName = billingLastEventName,
        billingLastEventAt = billingLastEventAt,
        consecutiveFailures = consecutiveFailures,
        createdAt = createdAt,
        updatedAt = updatedAt,
        billing = billing?.toDomain(),
    )
}

private fun SavingsExecutionDto.toDomain(): SavingsExecution {
    return SavingsExecution(
        id = id,
        executionDate = executionDate,
        amount = amount,
        status = status,
    )
}
