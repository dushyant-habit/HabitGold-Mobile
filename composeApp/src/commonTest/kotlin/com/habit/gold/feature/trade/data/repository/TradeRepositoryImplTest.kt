package com.habit.gold.feature.trade.data.repository

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.SessionExpiryHandler
import com.habit.gold.core.network.UnsupportedTokenRefreshHandler
import com.habit.gold.core.network.applyHabitGoldHttpClientConfig
import com.habit.gold.feature.trade.data.remote.TradeRemoteDataSource
import com.habit.gold.feature.trade.domain.model.TradeBuyOrderRequest
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class TradeRepositoryImplTest {

    @Test
    fun `create buy order sends idempotency header and maps backend response`() = runTest {
        var capturedIdempotencyKey: String? = null
        val repository = createRepository { request ->
            if (request.url.encodedPath.endsWith("/trade/buy")) {
                capturedIdempotencyKey = request.headers["Idempotency-Key"]
                jsonResponse(
                    """
                    {
                      "orderId":"buy-1",
                      "status":"PENDING",
                      "paymentProvider":"JUSPAY",
                      "paymentProviderOrderId":"provider-1",
                      "priceLockId":"lock-1",
                      "priceLockExpiresAt":"2026-05-16T10:00:00.000Z",
                      "goldQuantity":"0.4321",
                      "goldPrice":"9150.50",
                      "gst":{"grossAmount":"2000.00","netAmount":"1964.29","gstAmount":"35.71"},
                      "sdkPayload":{"orderId":"buy-1"}
                    }
                    """.trimIndent()
                )
            } else {
                error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.createBuyOrder(
            TradeBuyOrderRequest(
                amount = 1999.999,
                grams = 0.43219,
                buyRateId = "rate-1",
                couponCode = " SAVE50 ",
                useRewardsInr = 25.127,
            )
        )

        if (result is ApiResult.Failure) {
            error("Trade buy failed in test: ${result.error.message}")
        }
        val order = assertIs<ApiResult.Success<*>>(result).value as com.habit.gold.feature.trade.domain.model.TradeBuyOrder
        assertEquals("buy-1", order.orderId)
        assertEquals(0.4321, order.goldQuantityGrams)
        assertEquals(9150.50, order.goldPricePerGram)
        assertEquals(35.71, order.gstAmount)
        assertNotNull(capturedIdempotencyKey)
        assertTrue(capturedIdempotencyKey!!.startsWith("buy-"))
    }

    @Test
    fun `get trade status falls back to legacy endpoint when order endpoint returns 404`() = runTest {
        val repository = createRepository { request ->
            when {
                request.url.encodedPath.endsWith("/trade/orders/order-1") -> jsonResponse(
                    """
                    {"message":"Not found","error":"Not Found","statusCode":404}
                    """.trimIndent(),
                    status = HttpStatusCode.NotFound,
                )
                request.url.encodedPath.endsWith("/trade/status/order-1") -> jsonResponse(
                    """
                    {"orderId":"order-1","status":"SUCCESS","message":"Completed"}
                    """.trimIndent()
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val result = repository.getTradeStatus("order-1")
        val status = assertIs<ApiResult.Success<*>>(result).value as com.habit.gold.feature.trade.domain.model.TradeStatus

        assertEquals("order-1", status.orderId)
        assertEquals("SUCCESS", status.status)
        assertEquals("Completed", status.message)
    }

    private fun createRepository(
        engineHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): TradeRepositoryImpl {
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
        return TradeRepositoryImpl(TradeRemoteDataSource(client))
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
