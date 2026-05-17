package com.habit.gold.feature.trade.presentation.sell

import kotlin.test.Test
import kotlin.test.assertEquals

class SellTradeMathTest {

    @Test
    fun `sell under minimum rupee value is rejected`() {
        val result = computeSellTrade(
            entryMode = SellTradeEntryMode.Rupees,
            rawInput = "4",
            sellPrice = 20_000.0,
            sellableBalance = 1.0,
        )

        assertEquals(0.0, result.tradableGrams)
        assertEquals(0.0, result.payoutAmount)
        assertEquals("Amount must be at least ₹10.", result.message)
    }

    @Test
    fun `sell rounds grams to exact 0_0001 units and payout stays aligned`() {
        val result = computeSellTrade(
            entryMode = SellTradeEntryMode.Grams,
            rawInput = "0.123456",
            sellPrice = 10_000.0,
            sellableBalance = 1.0,
        )

        assertEquals(0.1235, result.tradableGrams)
        assertEquals(1235.0, result.payoutAmount)
        assertEquals(null, result.message)
    }

    @Test
    fun `sell rupees mode converts to grams without scientific notation`() {
        val result = computeSellTrade(
            entryMode = SellTradeEntryMode.Rupees,
            rawInput = "10",
            sellPrice = 50_000.0,
            sellableBalance = 1.0,
        )

        assertEquals(0.0002, result.tradableGrams)
        assertEquals("0.0002", formatGold(result.tradableGrams))
    }

    @Test
    fun `sell exceeding balance is rejected instead of capped silently`() {
        val result = computeSellTrade(
            entryMode = SellTradeEntryMode.Rupees,
            rawInput = "1000",
            sellPrice = 1_000.0,
            sellableBalance = 0.5,
        )

        assertEquals(0.0, result.tradableGrams)
        assertEquals("Amount exceeds your sellable gold balance.", result.message)
    }
}
