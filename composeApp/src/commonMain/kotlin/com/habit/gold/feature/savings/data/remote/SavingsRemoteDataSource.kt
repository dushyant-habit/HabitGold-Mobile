package com.habit.gold.feature.savings.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.savings.data.model.SavingsCreateMandateRequestDto
import com.habit.gold.feature.savings.data.model.SavingsCreateMandateResponseDto
import com.habit.gold.feature.savings.data.model.SavingsExecutionDto
import com.habit.gold.feature.savings.data.model.SavingsMandateDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SavingsRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun createMandateSession(
        body: SavingsCreateMandateRequestDto,
    ): ApiResult<SavingsCreateMandateResponseDto> = safeApiCall {
        httpClient.post("sip/mandate/session") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun updateMandateSession(
        mandateId: String,
        body: SavingsCreateMandateRequestDto,
    ): ApiResult<SavingsCreateMandateResponseDto> = safeApiCall {
        httpClient.post("sip/mandates/$mandateId/update-session") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun getMandates(): ApiResult<List<SavingsMandateDto>> = safeApiCall {
        httpClient.get("sip/mandates").body()
    }

    suspend fun getMandate(mandateId: String): ApiResult<SavingsMandateDto> = safeApiCall {
        httpClient.get("sip/mandates/$mandateId").body()
    }

    suspend fun getExecutionHistory(mandateId: String): ApiResult<List<SavingsExecutionDto>> = safeApiCall {
        httpClient.get("sip/mandates/$mandateId/executions").body()
    }

    suspend fun pauseMandate(mandateId: String): ApiResult<Unit> = safeApiCall {
        httpClient.post("sip/mandate/$mandateId/pause").body<Unit>()
    }

    suspend fun resumeMandate(mandateId: String): ApiResult<Unit> = safeApiCall {
        httpClient.post("sip/mandate/$mandateId/resume").body<Unit>()
    }

    suspend fun cancelMandate(mandateId: String): ApiResult<Unit> = safeApiCall {
        httpClient.post("sip/mandate/$mandateId/cancel").body<Unit>()
    }
}
