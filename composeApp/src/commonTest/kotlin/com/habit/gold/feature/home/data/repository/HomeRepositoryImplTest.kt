package com.habit.gold.feature.home.data.repository

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.AuthTokenProvider
import com.habit.gold.core.network.SessionExpiryHandler
import com.habit.gold.core.network.UnsupportedTokenRefreshHandler
import com.habit.gold.core.network.applyHabitGoldHttpClientConfig
import com.habit.gold.feature.home.data.remote.HomeRemoteDataSource
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

class HomeRepositoryImplTest {

    @Test
    fun `maps dashboard recent transactions and force update from backend models`() = kotlinx.coroutines.test.runTest {
        val repository = createRepository { request ->
            when {
                request.url.encodedPath.endsWith("/portfolio") -> jsonResponse(
                    """
                    {
                      "totalGoldBalanceGrams":"1.2500",
                      "investedValue":"10000",
                      "rewardsApplied":"150",
                      "gstPaid":"27",
                      "totalCost":"10200",
                      "averageBuyPricePerGram":"8000",
                      "currentValue":"11250",
                      "liveBuyPricePerGram":"9000",
                      "liveSellPricePerGram":"8800",
                      "finalPayoutAmount":"11000",
                      "buySellPriceDifference":"200"
                    }
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/trade/transactions") -> jsonResponse(
                    """
                    {
                      "data":[
                        {
                          "id":"txn-1",
                          "type":"BUY",
                          "status":"COMPLETED",
                          "amount":"5000",
                          "gstAmount":"10",
                          "netAmount":"4990",
                          "goldQuantity":"0.5000",
                          "goldPrice":"10000",
                          "createdAt":"2026-05-15T08:30:00.000Z",
                          "rewards":{"used":false,"discountAmount":"0","extraGold":"0","cashback":"0"},
                          "isSip":false
                        }
                      ],
                      "meta":{"total":1,"page":1,"limit":5,"totalPages":1}
                    }
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/user/features") -> jsonResponse(
                    """
                    {
                      "forceUpdate":{
                        "isActive":true,
                        "title":"Update required",
                        "message":"Please update to continue",
                        "ctaText":"Update now",
                        "storeUrl":"https://apps.apple.com/example",
                        "minVersion":"1.0.17",
                        "latestVersion":"1.0.18",
                        "force":true
                      }
                    }
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/sip/mandates") -> jsonResponse(
                    """
                    [
                      {
                        "id":"sip-1",
                        "name":"Gold Savings SIP",
                        "amount":"500",
                        "frequency":"DAILY",
                        "startDate":"2026-05-01T00:00:00.000Z",
                        "status":"ACTIVE",
                        "promoCode":"SAVE5",
                        "nextExecutionDate":"2026-05-20T00:00:00.000Z",
                        "billingCurrentAmount":"500",
                        "billing":{"currentAmount":"500","needsAttention":false}
                      }
                    ]
                    """.trimIndent(),
                )
                request.url.encodedPath.endsWith("/gold/price/history") -> jsonResponse(
                    """
                    {
                      "days":365,
                      "points":2,
                      "data":[
                        {"date":"2026-05-14","price":"9010.25"},
                        {"date":"2026-05-15","price":"9050.75"}
                      ]
                    }
                    """.trimIndent(),
                )
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }

        val dashboard = assertIs<ApiResult.Success<*>>(repository.getPortfolioDashboard()).value
        val transactions = assertIs<ApiResult.Success<*>>(repository.getRecentTransactions(page = 1, limit = 5)).value
        val forceUpdate = assertIs<ApiResult.Success<*>>(repository.getForceUpdate()).value
        val sipMandates = assertIs<ApiResult.Success<*>>(repository.getSipMandates()).value
        val priceHistory = assertIs<ApiResult.Success<*>>(repository.getPriceHistory(days = 365)).value

        dashboard as com.habit.gold.feature.home.domain.model.HomeDashboardSummary
        transactions as List<*>
        forceUpdate as com.habit.gold.feature.home.domain.model.HomeForceUpdate?
        sipMandates as List<*>
        priceHistory as List<*>

        assertEquals(1.25, dashboard.totalGoldBalanceGrams)
        assertEquals(11250.0, dashboard.currentValue)
        assertEquals("txn-1", transactions.firstOrNull()?.let { (it as com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview).id })
        assertEquals("Update required", forceUpdate?.title)
        assertEquals(true, forceUpdate?.isForced)
        assertEquals("sip-1", sipMandates.firstOrNull()?.let { (it as com.habit.gold.feature.home.domain.model.HomeSipMandate).id })
        assertEquals(9050.75, priceHistory.lastOrNull()?.let { (it as com.habit.gold.feature.home.domain.model.HomeGoldPricePoint).price })
    }

    private fun createRepository(
        engineHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): HomeRepositoryImpl {
        val client = HttpClient(MockEngine { request -> engineHandler(request) }) {
            applyHabitGoldHttpClientConfig(
                appConfig = AppConfig(
                    appName = "HabitGold",
                    bundleId = "com.habit.gold",
                    appVersion = "1.0-debug",
                    appPlatform = "android",
                    environment = AppEnvironment.Staging,
                    baseUrl = "https://api.habitgold.com/v1/",
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
        return HomeRepositoryImpl(HomeRemoteDataSource(client))
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
