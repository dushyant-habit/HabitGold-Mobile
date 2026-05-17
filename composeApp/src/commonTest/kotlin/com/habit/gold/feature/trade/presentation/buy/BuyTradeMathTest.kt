package com.habit.gold.feature.trade.presentation.buy

import kotlin.test.Test
import kotlin.test.assertEquals

class BuyTradeMathTest {

    @Test
    fun `rupees mode uses inclusive gst and rounds gold quantity to 4 decimals`() {
        val calculation = calculateBuyTrade(
            entryMode = BuyTradeEntryMode.Rupees,
            numericRupees = 100.0,
            numericGrams = 0.0,
            goldPrice = 10_000.0,
            gstRate = 0.03,
        )

        assertEquals(100.0, calculation.totalPayable)
        assertEquals(97.09, calculation.goldValue)
        assertEquals(2.91, calculation.gstAmount)
        assertEquals(0.0097, calculation.goldQuantity)
    }

    @Test
    fun `grams mode rounds quantity and computes payable correctly`() {
        val calculation = calculateBuyTrade(
            entryMode = BuyTradeEntryMode.Grams,
            numericRupees = 0.0,
            numericGrams = 0.12345,
            goldPrice = 10_000.0,
            gstRate = 0.03,
        )

        assertEquals(0.1235, calculation.goldQuantity)
        assertEquals(1235.0, calculation.goldValue)
        assertEquals(37.05, calculation.gstAmount)
        assertEquals(1272.05, calculation.totalPayable)
    }

    @Test
    fun `format conversion grams avoids scientific notation`() {
        assertEquals("0.0002", formatConversionGrams(0.0002))
        assertEquals("1.2345", formatConversionGrams(1.2345))
    }
}
