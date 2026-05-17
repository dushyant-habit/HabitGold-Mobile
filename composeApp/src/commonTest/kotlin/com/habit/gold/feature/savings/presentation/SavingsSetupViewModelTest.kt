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
import com.habit.gold.feature.savings.domain.usecase.CreateSavingsMandateSessionUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.UpdateSavingsMandateSessionUseCase
import com.habit.gold.feature.trade.domain.model.TradePaymentContext
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SavingsSetupViewModelTest {

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
    fun `submit from new setup emits sip setup payment request`() = runTest(dispatcher) {
        val repository = FakeSavingsSetupRepository(
            createSessionResult = ApiResult.Success(
                SavingsMandateSession(
                    mandateId = "mandate-1",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
        )
        val viewModel = createViewModel(repository)
        val effect = async { viewModel.effects.firstEffect() }

        viewModel.onIntent(
            SavingsSetupIntent.Initialize(
                SavingsDestination.Setup(
                    frequency = "Weekly",
                    initialAmount = "500",
                    mandateId = null,
                    initialExecutionDay = 4,
                )
            )
        )
        advanceUntilIdle()
        viewModel.onIntent(SavingsSetupIntent.Submit)
        advanceUntilIdle()

        val launchEffect = assertIs<SavingsSetupEffect.LaunchPayment>(effect.await())
        assertEquals(TradePaymentContext.BuySipSetup, launchEffect.request.context)
        assertEquals("500", repository.lastCreateRequest?.amount?.toString())
        assertEquals("WEEKLY", repository.lastCreateRequest?.frequency)
        assertEquals(4, repository.lastCreateRequest?.executionDay)
    }

    @Test
    fun `initialize uses route data immediately without loading state`() = runTest(dispatcher) {
        val repository = FakeSavingsSetupRepository(
            createSessionResult = ApiResult.Success(
                SavingsMandateSession(
                    mandateId = "mandate-route",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            SavingsSetupIntent.Initialize(
                SavingsDestination.Setup(
                    frequency = "Monthly",
                    initialAmount = "2500",
                    initialExecutionDay = 12,
                )
            )
        )
        advanceUntilIdle()

        assertEquals(SavingsSetupPhase.Form, viewModel.state.value.phase)
        assertEquals("2500", viewModel.state.value.amountText)
        assertEquals(12, viewModel.state.value.selectedExecutionDay)
    }

    @Test
    fun `upgrade flow loads mandate and emits sip upgrade payment request`() = runTest(dispatcher) {
        val repository = FakeSavingsSetupRepository(
            createSessionResult = ApiResult.Success(
                SavingsMandateSession(
                    mandateId = "mandate-2",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
            updateSessionResult = ApiResult.Success(
                SavingsMandateSession(
                    mandateId = "mandate-2",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
            mandateResults = mutableListOf(
                ApiResult.Success(fakeSavingsMandate(id = "mandate-2", status = "ACTIVE", amount = "500")),
            ),
        )
        val viewModel = createViewModel(repository)
        val effect = async { viewModel.effects.firstEffect() }

        viewModel.onIntent(
            SavingsSetupIntent.Initialize(
                SavingsDestination.Setup(
                    frequency = "Weekly",
                    initialAmount = "500",
                    mandateId = "mandate-2",
                    initialExecutionDay = 2,
                )
            )
        )
        advanceUntilIdle()
        viewModel.onIntent(SavingsSetupIntent.Submit)
        advanceUntilIdle()

        val launchEffect = assertIs<SavingsSetupEffect.LaunchPayment>(effect.await())
        assertEquals(TradePaymentContext.BuySipUpgrade, launchEffect.request.context)
        assertEquals("mandate-2", repository.lastUpdatedMandateId)
        assertEquals(1000, repository.lastUpdateRequest?.amount)
    }

    @Test
    fun `paused mandate preserves current amount instead of forcing upgrade`() = runTest(dispatcher) {
        val repository = FakeSavingsSetupRepository(
            createSessionResult = ApiResult.Success(
                SavingsMandateSession(
                    mandateId = "mandate-paused",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            SavingsSetupIntent.Initialize(
                SavingsDestination.Setup(
                    frequency = "Weekly",
                    initialAmount = "500",
                    mandateId = "mandate-paused",
                    initialExecutionDay = 2,
                    initialStatus = "PAUSED",
                )
            )
        )
        advanceUntilIdle()

        assertEquals("500", viewModel.state.value.amountText)
        assertTrue(viewModel.state.value.isPausedMandate)
    }

    @Test
    fun `payment success polls mandate until active and reaches success`() = runTest(dispatcher) {
        val repository = FakeSavingsSetupRepository(
            createSessionResult = ApiResult.Success(
                SavingsMandateSession(
                    mandateId = "mandate-3",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
            mandateResults = mutableListOf(
                ApiResult.Success(fakeSavingsMandate(id = "mandate-3", status = "PENDING_REGISTRATION")),
                ApiResult.Success(fakeSavingsMandate(id = "mandate-3", status = "ACTIVE")),
            ),
        )
        val viewModel = createViewModel(repository)
        val effect = async { viewModel.effects.firstEffect() }

        viewModel.onIntent(
            SavingsSetupIntent.Initialize(
                SavingsDestination.Setup(
                    frequency = "Daily",
                    initialAmount = "50",
                )
            )
        )
        advanceUntilIdle()
        viewModel.onIntent(SavingsSetupIntent.Submit)
        advanceUntilIdle()
        effect.await()

        viewModel.onIntent(SavingsSetupIntent.HandlePaymentResult(TradePaymentLaunchResult.Success(status = "charged")))
        advanceTimeBy(5_000)
        advanceUntilIdle()

        assertIs<SavingsSetupPhase.Success>(viewModel.state.value.phase)
    }

    @Test
    fun `payment polling falls through to processing after max attempts`() = runTest(dispatcher) {
        val repository = FakeSavingsSetupRepository(
            createSessionResult = ApiResult.Success(
                SavingsMandateSession(
                    mandateId = "mandate-4",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
            mandateResults = MutableList(6) {
                ApiResult.Success(fakeSavingsMandate(id = "mandate-4", status = "PENDING_REGISTRATION"))
            },
        )
        val viewModel = createViewModel(repository)
        val effect = async { viewModel.effects.firstEffect() }

        viewModel.onIntent(
            SavingsSetupIntent.Initialize(
                SavingsDestination.Setup(
                    frequency = "Monthly",
                    initialAmount = "2500",
                    initialExecutionDay = 10,
                )
            )
        )
        advanceUntilIdle()
        viewModel.onIntent(SavingsSetupIntent.Submit)
        advanceUntilIdle()
        effect.await()

        viewModel.onIntent(
            SavingsSetupIntent.HandlePaymentResult(
                TradePaymentLaunchResult.Failure(
                    status = "network_error",
                    message = "SDK network issue",
                    shouldPollOrderStatus = true,
                )
            )
        )
        advanceTimeBy(30_000)
        advanceUntilIdle()

        assertIs<SavingsSetupPhase.Processing>(viewModel.state.value.phase)
    }

    private fun createViewModel(repository: FakeSavingsSetupRepository): SavingsSetupViewModel {
        return SavingsSetupViewModel(
            createSavingsMandateSessionUseCase = CreateSavingsMandateSessionUseCase(repository),
            updateSavingsMandateSessionUseCase = UpdateSavingsMandateSessionUseCase(repository),
            getSavingsMandateUseCase = GetSavingsMandateUseCase(repository),
        )
    }
}

private suspend fun SharedFlow<SavingsSetupEffect>.firstEffect(): SavingsSetupEffect {
    return first()
}

private class FakeSavingsSetupRepository(
    private val createSessionResult: ApiResult<SavingsMandateSession>,
    private val updateSessionResult: ApiResult<SavingsMandateSession> = createSessionResult,
    val mandateResults: MutableList<ApiResult<SavingsMandate>> = mutableListOf(),
) : SavingsRepository {
    var lastCreateRequest: SavingsCreateMandateRequest? = null
    var lastUpdateRequest: SavingsCreateMandateRequest? = null
    var lastUpdatedMandateId: String? = null

    override suspend fun createMandateSession(request: SavingsCreateMandateRequest): ApiResult<SavingsMandateSession> {
        lastCreateRequest = request
        return createSessionResult
    }

    override suspend fun updateMandateSession(
        mandateId: String,
        request: SavingsCreateMandateRequest,
    ): ApiResult<SavingsMandateSession> {
        lastUpdatedMandateId = mandateId
        lastUpdateRequest = request
        return updateSessionResult
    }

    override suspend fun getMandates(): ApiResult<List<SavingsMandate>> {
        return ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Unknown,
                message = "Not used in this test",
            )
        )
    }

    override suspend fun getMandate(mandateId: String): ApiResult<SavingsMandate> {
        return mandateResults.removeFirstOrNull()
            ?: ApiResult.Failure(
                NetworkError(
                    kind = NetworkErrorKind.Unknown,
                    message = "No fake mandate configured for $mandateId",
                )
            )
    }

    override suspend fun getExecutionHistory(mandateId: String): ApiResult<List<SavingsExecution>> {
        error("Not used in this test")
    }

    override suspend fun pauseMandate(mandateId: String): ApiResult<Unit> {
        error("Not used in this test")
    }

    override suspend fun resumeMandate(mandateId: String): ApiResult<Unit> {
        error("Not used in this test")
    }

    override suspend fun cancelMandate(mandateId: String): ApiResult<Unit> {
        error("Not used in this test")
    }
}

private fun fakeSavingsMandate(
    id: String,
    status: String,
    amount: String = "500",
): SavingsMandate {
    return SavingsMandate(
        id = id,
        userId = "user-1",
        name = "Weekly Gold Savings",
        amount = amount,
        frequency = "WEEKLY",
        startDate = "2026-05-01T10:30:00.000Z",
        status = status,
        juspayMandateId = "juspay-$id",
        promoCode = null,
        nextExecutionDate = "2026-05-20T00:00:00.000Z",
        billingCurrentAmount = amount,
        billingNextExecutionAmount = amount,
        billingLastEventName = null,
        billingLastEventAt = null,
        consecutiveFailures = 0,
        createdAt = "2026-05-01T10:30:00.000Z",
        updatedAt = "2026-05-05T10:30:00.000Z",
        billing = SavingsMandateBilling(
            executionId = null,
            executionStatus = null,
            nextExecutionOrderId = null,
            nextExecutionAmount = amount,
            currentAmount = amount,
            amountUpdatedAt = null,
            needsAttention = false,
        ),
    )
}
