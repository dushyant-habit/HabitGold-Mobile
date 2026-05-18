package com.habit.gold.feature.alerts.presentation

import com.habit.gold.feature.alerts.domain.model.AlertItem
import com.habit.gold.feature.alerts.domain.repository.AlertsRepository
import com.habit.gold.feature.alerts.domain.usecase.GetAlertsUseCase
import com.habit.gold.feature.alerts.domain.usecase.MarkAllAlertsReadUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AlertsViewModelTest {
    @Test
    fun `load maps alerts and marks them read`() = runTest(StandardTestDispatcher()) {
        val repository = FakeAlertsRepository(
            alerts = listOf(
                AlertItem(
                    id = "1",
                    title = "Gold purchased",
                    description = "Your order is complete.",
                    createdAt = "2026-05-18T10:15:30Z",
                    isRead = false,
                ),
            ),
        )
        val viewModel = AlertsViewModel(
            getAlertsUseCase = GetAlertsUseCase(repository),
            markAllAlertsReadUseCase = MarkAllAlertsReadUseCase(repository),
        )

        viewModel.onIntent(AlertsIntent.Load)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(1, state.alerts.size)
        assertEquals("2026-05-18T10:15:30Z", state.alerts.first().createdAt)
        assertTrue(repository.markAllReadCalled)
    }
}

private class FakeAlertsRepository(
    private val alerts: List<AlertItem>,
) : AlertsRepository {
    var markAllReadCalled = false

    override suspend fun getAlerts(): List<AlertItem> = alerts

    override suspend fun markAllAlertsRead() {
        markAllReadCalled = true
    }
}
