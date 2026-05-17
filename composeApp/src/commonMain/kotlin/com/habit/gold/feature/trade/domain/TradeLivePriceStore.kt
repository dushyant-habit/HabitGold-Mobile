package com.habit.gold.feature.trade.domain

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.domain.model.TradeLivePrice
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Instant

data class TradeLivePriceState(
    val price: TradeLivePrice? = null,
    val buyRemainingSeconds: Int = 0,
    val sellRemainingSeconds: Int = 0,
    val buyRefreshWindowSeconds: Int = 1,
    val sellRefreshWindowSeconds: Int = 1,
    val isFetching: Boolean = true,
    val errorMessage: String? = null,
)

class TradeLivePriceStore(
    private val tradeRepository: TradeRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(TradeLivePriceState())
    val state: StateFlow<TradeLivePriceState> = _state.asStateFlow()

    private var isLoggedIn = false
    private var isFetching = false
    private var fetchJob: Job? = null
    private var timerJob: Job? = null
    private var lastResolvedAuthState: Boolean? = null

    fun setLoggedIn(loggedIn: Boolean) {
        if (lastResolvedAuthState == loggedIn) return
        isLoggedIn = loggedIn
        lastResolvedAuthState = loggedIn
        fetchJob?.cancel()
        timerJob?.cancel()
        isFetching = false

        if (!loggedIn) {
            _state.value = TradeLivePriceState(isFetching = false)
            return
        }

        refreshPrices(force = true)
    }

    fun refreshPricesAfterRateExpired() {
        fetchJob?.cancel()
        timerJob?.cancel()
        isFetching = false
        refreshPrices(force = true)
    }

    fun refreshPrices(force: Boolean = false) {
        if (!isLoggedIn) {
            _state.value = TradeLivePriceState(isFetching = false)
            isFetching = false
            return
        }

        val cachedPrice = _state.value.price
        val cachedIsStillValid = cachedPrice != null &&
            lastResolvedAuthState == isLoggedIn &&
            !hasExpired(cachedPrice.buyValidUntil) &&
            !hasExpired(cachedPrice.sellValidUntil)
        if (!force && cachedIsStillValid) return
        if (isFetching) return

        isFetching = true
        timerJob?.cancel()
        _state.value = _state.value.copy(
            isFetching = true,
            errorMessage = null,
            buyRemainingSeconds = 0,
            sellRemainingSeconds = 0,
        )

        fetchJob?.cancel()
        fetchJob = scope.launch {
            try {
                when (val result = tradeRepository.getLivePrice()) {
                    is ApiResult.Success -> {
                        _state.value = _state.value.copy(
                            price = result.value,
                            isFetching = false,
                            errorMessage = null,
                        )
                        startRefreshTimer(
                            buyValidUntil = result.value.buyValidUntil,
                            sellValidUntil = result.value.sellValidUntil,
                        )
                    }
                    is ApiResult.Failure -> {
                        _state.value = _state.value.copy(
                            price = null,
                            isFetching = false,
                            errorMessage = result.error.message,
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                coroutineContext.ensureActive()
                _state.value = _state.value.copy(
                    price = null,
                    isFetching = false,
                    errorMessage = error.message,
                )
            } finally {
                isFetching = false
            }
        }
    }

    private fun startRefreshTimer(
        buyValidUntil: String,
        sellValidUntil: String,
    ) {
        timerJob?.cancel()
        timerJob = scope.launch {
            try {
                val buyExpiry = Instant.parse(buyValidUntil)
                val sellExpiry = Instant.parse(sellValidUntil)
                val buyInitialRemaining = max((buyExpiry - Clock.System.now()).inWholeSeconds.toInt(), 0)
                val sellInitialRemaining = max((sellExpiry - Clock.System.now()).inWholeSeconds.toInt(), 0)

                if (minOf(buyInitialRemaining, sellInitialRemaining) <= 0) {
                    _state.value = _state.value.copy(
                        buyRefreshWindowSeconds = STALE_PRICE_RETRY_SECONDS.toInt(),
                        sellRefreshWindowSeconds = STALE_PRICE_RETRY_SECONDS.toInt(),
                        buyRemainingSeconds = 0,
                        sellRemainingSeconds = 0,
                    )
                    delay(STALE_PRICE_RETRY_SECONDS * 1000)
                    refreshPrices(force = true)
                    return@launch
                }

                _state.value = _state.value.copy(
                    buyRefreshWindowSeconds = buyInitialRemaining,
                    sellRefreshWindowSeconds = sellInitialRemaining,
                    buyRemainingSeconds = buyInitialRemaining,
                    sellRemainingSeconds = sellInitialRemaining,
                )

                while (true) {
                    val now = Clock.System.now()
                    val buyRemaining = max((buyExpiry - now).inWholeSeconds.toInt(), 0)
                    val sellRemaining = max((sellExpiry - now).inWholeSeconds.toInt(), 0)
                    if (minOf(buyRemaining, sellRemaining) <= 0) {
                        _state.value = _state.value.copy(
                            buyRemainingSeconds = 0,
                            sellRemainingSeconds = 0,
                        )
                        refreshPrices(force = true)
                        break
                    }

                    _state.value = _state.value.copy(
                        buyRemainingSeconds = buyRemaining,
                        sellRemainingSeconds = sellRemaining,
                    )
                    delay(1000)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.value = _state.value.copy(
                    buyRefreshWindowSeconds = 30,
                    sellRefreshWindowSeconds = 30,
                    buyRemainingSeconds = 0,
                    sellRemainingSeconds = 0,
                )
                delay(30000)
                refreshPrices(force = true)
            }
        }
    }

    private fun hasExpired(validUntil: String): Boolean {
        return runCatching {
            Instant.parse(validUntil) <= Clock.System.now()
        }.getOrElse { true }
    }

    private companion object {
        const val STALE_PRICE_RETRY_SECONDS = 5L
    }
}
