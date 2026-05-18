package com.habit.gold.feature.savings.presentation

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SavingsSetupDisplayTest {

    @Test
    fun `weekly next payment label uses monday first execution day`() {
        val label = nextPaymentLabel(
            frequency = SavingsFrequency.Weekly,
            executionDay = 1,
            today = LocalDate(2026, 5, 17),
        )

        assertEquals("On 18 May, 2026", label)
    }

    @Test
    fun `monthly next payment label rolls to next month when date passed`() {
        val label = nextPaymentLabel(
            frequency = SavingsFrequency.Monthly,
            executionDay = 10,
            today = LocalDate(2026, 5, 17),
        )

        assertEquals("On 10 Jun, 2026", label)
    }

    @Test
    fun `daily next payment label is available without execution day`() {
        val state = SavingsSetupUiState(
            frequency = SavingsFrequency.Daily,
            selectedExecutionDay = null,
        )

        assertEquals("Tomorrow, 18 May, 2026", state.nextDebitLabelOrNull(today = LocalDate(2026, 5, 17)))
    }

    @Test
    fun `compounding summary returns expected 10 year highlight`() {
        val summary = calculateCompoundingSummary(2500.0)

        assertNotNull(summary)
        assertEquals(10, summary.projectionYears)
        assertTrue(summary.highlightText.contains("in 10 years"))
        assertTrue(summary.totalValue > summary.totalInvested)
    }

    @Test
    fun `schedule suffix label renders weekly full weekday`() {
        val state = SavingsSetupUiState(
            frequency = SavingsFrequency.Weekly,
            selectedExecutionDay = 4,
        )

        assertEquals("Every Thursday", state.scheduleSuffixLabel())
    }
}
