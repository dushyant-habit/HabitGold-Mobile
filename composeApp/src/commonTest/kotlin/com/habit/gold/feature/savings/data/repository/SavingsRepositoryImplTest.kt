package com.habit.gold.feature.savings.data.repository

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.SessionExpiryHandler
import com.habit.gold.core.network.UnsupportedTokenRefreshHandler
import com.habit.gold.core.network.applyHabitGoldHttpClientConfig
import com.habit.gold.feature.savings.data.remote.SavingsRemoteDataSource
import com.habit.gold.feature.savings.domain.model.SavingsCreateMandateRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class SavingsRepositoryImplTest {

    @Test
    fun `create mandate session maps backend payload`() = runTest {
        val repository = createRepository { request ->
            when {
                request.url.encodedPath.endsWith("/sip/mandate/session") -> jsonResponse(
                    """
                    {
                      "mandateId":"sip-1",
                      "sdk_payload":{"order_id":"order-1"}
                    }
                    """.trimIndent()
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.createMandateSession(
            SavingsCreateMandateRequest(
                amount = 2500,
                frequency = "WEEKLY",
                name = "Weekly Gold",
                executionDay = 2,
                promoCode = " SAVE7 ",
            )
        )

        val session = assertIs<ApiResult.Success<*>>(result).value as com.habit.gold.feature.savings.domain.model.SavingsMandateSession
        assertEquals("sip-1", session.mandateId)
        assertEquals("{\"order_id\":\"order-1\"}", session.sdkPayloadJson)
    }

    @Test
    fun `get mandates maps billing state and status fields`() = runTest {
        val repository = createRepository { request ->
            when {
                request.url.encodedPath.endsWith("/sip/mandates") -> jsonResponse(
                    """
                    [
                      {
                        "id":"sip-1",
                        "name":"Weekly Savings",
                        "amount":"2500",
                        "frequency":"WEEKLY",
                        "startDate":"2026-05-01T10:30:00.000Z",
                        "status":"PAUSED",
                        "juspayMandateId":"mandate-1",
                        "promoCode":"SAVE7",
                        "nextExecutionDate":"2026-05-20T00:00:00.000Z",
                        "billingCurrentAmount":"2500",
                        "billingNextExecutionAmount":"2500",
                        "billingLastEventName":"MANDATE_PAUSED",
                        "billingLastEventAt":"2026-05-15T10:00:00.000Z",
                        "consecutiveFailures":1,
                        "billing":{
                          "currentAmount":"2500",
                          "nextExecutionAmount":"2500",
                          "needsAttention":true
                        }
                      }
                    ]
                    """.trimIndent()
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.getMandates()

        val mandates = assertIs<ApiResult.Success<*>>(result).value as List<*>
        val first = mandates.first() as com.habit.gold.feature.savings.domain.model.SavingsMandate
        assertEquals("sip-1", first.id)
        assertEquals("PAUSED", first.status)
        assertEquals("2500", first.billing?.currentAmount)
        assertEquals(true, first.billing?.needsAttention)
    }

    private fun createRepository(
        engineHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): SavingsRepositoryImpl {
        val client = HttpClient(MockEngine { request -> engineHandler(request) }) {
            applyHabitGoldHttpClientConfig(
                appConfig = AppConfig(
                    appName = "HabitGold",
                    bundleId = "com.habit.gold",
                    appVersion = "1.0.18",
                    appPlatform = "android",
                    environment = AppEnvironment.Staging,
                    baseUrl = "https://staging.habitgold.com/v1/",
                    enableNetworkLogs = false,
                ),
                authTokenProvider = object : AuthTokenProvider {
                    override fun getAccessToken(): String? = "access-token"
                    override fun getRefreshToken(): String? = "refresh-token"
                },
                sessionExpiryHandler = object : SessionExpiryHandler {
                    override suspend fun onSessionExpired() = Unit
                },
                tokenRefreshHandler = UnsupportedTokenRefreshHandler(),
            )
        }
        return SavingsRepositoryImpl(SavingsRemoteDataSource(client))
    }

    private fun MockRequestHandleScope.jsonResponse(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) = respond(
        content = ByteReadChannel(body),
        status = status,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )
}
