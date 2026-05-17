package com.habit.gold.feature.savings.presentation

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
import com.habit.gold.feature.savings.domain.SavingsRepository
import com.habit.gold.feature.savings.domain.model.SavingsCreateMandateRequest
import com.habit.gold.feature.savings.domain.model.SavingsExecution
import com.habit.gold.feature.savings.domain.model.SavingsMandate
import com.habit.gold.feature.savings.domain.model.SavingsMandateBilling
import com.habit.gold.feature.savings.domain.model.SavingsMandateSession
import com.habit.gold.feature.savings.domain.usecase.CancelSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandatesUseCase
import com.habit.gold.feature.savings.domain.usecase.PauseSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.ResumeSavingsMandateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SavingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load keeps active and paused mandates visible by default`() = runTest(dispatcher) {
        val repository = FakeSavingsRepository(
            mandatesResult = ApiResult.Success(
                listOf(
                    fakeMandate(id = "active-1", status = "ACTIVE"),
                    fakeMandate(id = "paused-1", status = "PAUSED"),
                    fakeMandate(id = "cancelled-1", status = "CANCELLED"),
                )
            )
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(listOf("active-1", "paused-1"), viewModel.state.value.visibleMandates.map { it.id })
    }

    @Test
    fun `select filter shows only matching mandates`() = runTest(dispatcher) {
        val repository = FakeSavingsRepository(
            mandatesResult = ApiResult.Success(
                listOf(
                    fakeMandate(id = "active-1", status = "ACTIVE"),
                    fakeMandate(id = "failed-1", status = "FAILED_REGISTRATION"),
                )
            )
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(SavingsIntent.SelectFilter(SavingsStatusFilter.Failed))

        assertEquals(listOf("failed-1"), viewModel.state.value.visibleMandates.map { it.id })
    }

    @Test
    fun `pause success refreshes mandates and exposes action message`() = runTest(dispatcher) {
        val repository = FakeSavingsRepository(
            mandatesResult = ApiResult.Success(listOf(fakeMandate(id = "active-1", status = "ACTIVE"))),
            pauseResult = ApiResult.Success(Unit),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(SavingsIntent.PauseMandate("active-1"))
        advanceUntilIdle()

        assertEquals(SavingsActionMessage.Paused, viewModel.state.value.actionMessage)
        assertEquals(2, repository.fetchCount)
        assertEquals(null, viewModel.state.value.actionInFlightMandateId)
    }

    @Test
    fun `action failure keeps message and clears loading mandate`() = runTest(dispatcher) {
        val repository = FakeSavingsRepository(
            mandatesResult = ApiResult.Success(listOf(fakeMandate(id = "paused-1", status = "PAUSED"))),
            resumeResult = ApiResult.Failure(
                NetworkError(
                    kind = NetworkErrorKind.Server,
                    message = "Unable to resume mandate",
                )
            ),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(SavingsIntent.ResumeMandate("paused-1"))
        advanceUntilIdle()

        assertEquals(null, viewModel.state.value.actionInFlightMandateId)
        assertEquals(null, viewModel.state.value.selectedFilter)
        assertEquals(null, viewModel.state.value.actionMessage)
        assertEquals("Unable to resume mandate", viewModel.state.value.actionErrorMessage)
    }

    private fun createViewModel(repository: FakeSavingsRepository): SavingsViewModel {
        return SavingsViewModel(
            getSavingsMandatesUseCase = GetSavingsMandatesUseCase(repository),
            pauseSavingsMandateUseCase = PauseSavingsMandateUseCase(repository),
            resumeSavingsMandateUseCase = ResumeSavingsMandateUseCase(repository),
            cancelSavingsMandateUseCase = CancelSavingsMandateUseCase(repository),
        )
    }
}

private class FakeSavingsRepository(
    private val mandatesResult: ApiResult<List<SavingsMandate>>,
    private val pauseResult: ApiResult<Unit> = ApiResult.Success(Unit),
    private val resumeResult: ApiResult<Unit> = ApiResult.Success(Unit),
    private val cancelResult: ApiResult<Unit> = ApiResult.Success(Unit),
) : SavingsRepository {
    var fetchCount: Int = 0

    override suspend fun createMandateSession(request: SavingsCreateMandateRequest): ApiResult<SavingsMandateSession> {
        error("Not used in this test")
    }

    override suspend fun updateMandateSession(
        mandateId: String,
        request: SavingsCreateMandateRequest,
    ): ApiResult<SavingsMandateSession> {
        error("Not used in this test")
    }

    override suspend fun getMandates(): ApiResult<List<SavingsMandate>> {
        fetchCount += 1
        return mandatesResult
    }

    override suspend fun getMandate(mandateId: String): ApiResult<SavingsMandate> {
        error("Not used in this test")
    }

    override suspend fun getExecutionHistory(mandateId: String): ApiResult<List<SavingsExecution>> {
        error("Not used in this test")
    }

    override suspend fun pauseMandate(mandateId: String): ApiResult<Unit> = pauseResult

    override suspend fun resumeMandate(mandateId: String): ApiResult<Unit> = resumeResult

    override suspend fun cancelMandate(mandateId: String): ApiResult<Unit> = cancelResult
}

private fun fakeMandate(
    id: String,
    status: String,
): SavingsMandate {
    return SavingsMandate(
        id = id,
        userId = "user-1",
        name = "Weekly Gold",
        amount = "2500",
        frequency = "WEEKLY",
        startDate = "2026-05-01T10:30:00.000Z",
        status = status,
        juspayMandateId = "mandate-1",
        promoCode = null,
        nextExecutionDate = "2026-05-20T00:00:00.000Z",
        billingCurrentAmount = "2500",
        billingNextExecutionAmount = "2500",
        billingLastEventName = null,
        billingLastEventAt = null,
        consecutiveFailures = 0,
        createdAt = "2026-05-01T10:30:00.000Z",
        updatedAt = "2026-05-05T10:30:00.000Z",
        billing = SavingsMandateBilling(
            executionId = null,
            executionStatus = null,
            nextExecutionOrderId = null,
            nextExecutionAmount = "2500",
            currentAmount = "2500",
            amountUpdatedAt = null,
            needsAttention = false,
        ),
    )
}
