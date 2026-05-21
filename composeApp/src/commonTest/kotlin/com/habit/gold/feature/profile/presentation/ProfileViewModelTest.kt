package com.habit.gold.feature.profile.presentation

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
import com.habit.gold.feature.profile.domain.ProfileRepository
import com.habit.gold.feature.profile.domain.model.ProfileNominee
import com.habit.gold.feature.profile.domain.model.ProfileSummary
import com.habit.gold.feature.profile.domain.model.ProfileUser
import com.habit.gold.feature.profile.domain.usecase.GetProfileSummaryUseCase
import com.habit.gold.feature.profile.domain.usecase.LogoutProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.RequestDeleteAccountUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

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
    fun `load keeps seeded summary visible while refresh is running`() = runTest(dispatcher) {
        val repository = FakeProfileRepository(
            summaryResult = ApiResult.Success(profileSummary(name = "Remote Name")),
            delayOnFetch = true,
        )
        val viewModel = createViewModel(
            repository = repository,
            initialSummary = profileSummary(name = "Seed Name"),
        )

        viewModel.onIntent(ProfileIntent.Load)
        runCurrent()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isRefreshing)
        assertEquals("Seed Name", viewModel.state.value.summary?.user?.name)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals("Remote Name", viewModel.state.value.summary?.user?.name)
    }

    @Test
    fun `delete account exposes repository error`() = runTest(dispatcher) {
        val repository = FakeProfileRepository(
            summaryResult = ApiResult.Success(profileSummary()),
            deleteResult = ApiResult.Failure(
                NetworkError(
                    kind = NetworkErrorKind.Server,
                    message = "Unable to request deletion",
                )
            ),
        )
        val viewModel = createViewModel(repository = repository, initialSummary = profileSummary())

        viewModel.onIntent(ProfileIntent.DeleteAccount)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isDeleteInFlight)
        assertEquals("Unable to request deletion", viewModel.state.value.errorMessage)
    }

    @Test
    fun `refresh failure keeps seeded summary visible and exposes error`() = runTest(dispatcher) {
        val repository = FakeProfileRepository(
            summaryResult = ApiResult.Failure(
                NetworkError(
                    kind = NetworkErrorKind.Server,
                    message = "Unable to refresh profile",
                )
            ),
        )
        val viewModel = createViewModel(
            repository = repository,
            initialSummary = profileSummary(name = "Seed Name"),
        )

        viewModel.onIntent(ProfileIntent.Refresh)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals("Seed Name", viewModel.state.value.summary?.user?.name)
        assertEquals("Unable to refresh profile", viewModel.state.value.errorMessage)
    }

    @Test
    fun `logout ignores duplicate taps while request is in flight`() = runTest(dispatcher) {
        val repository = FakeProfileRepository(
            summaryResult = ApiResult.Success(profileSummary()),
            delayOnLogout = true,
        )
        val viewModel = createViewModel(repository = repository, initialSummary = profileSummary())

        viewModel.onIntent(ProfileIntent.Logout)
        runCurrent()
        viewModel.onIntent(ProfileIntent.Logout)
        advanceUntilIdle()

        assertEquals(1, repository.logoutCalls)
        assertFalse(viewModel.state.value.isLogoutInFlight)
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    private fun createViewModel(
        repository: FakeProfileRepository,
        initialSummary: ProfileSummary?,
    ): ProfileViewModel {
        return ProfileViewModel(
            initialSummary = initialSummary,
            getProfileSummaryUseCase = GetProfileSummaryUseCase(repository),
            logoutProfileUseCase = LogoutProfileUseCase(repository),
            requestDeleteAccountUseCase = RequestDeleteAccountUseCase(repository),
        )
    }
}

private class FakeProfileRepository(
    private val summaryResult: ApiResult<ProfileSummary>,
    private val logoutResult: ApiResult<Unit> = ApiResult.Success(Unit),
    private val deleteResult: ApiResult<Unit> = ApiResult.Success(Unit),
    private val delayOnFetch: Boolean = false,
    private val delayOnLogout: Boolean = false,
) : ProfileRepository {
    var logoutCalls: Int = 0

    override suspend fun getProfileSummary(): ApiResult<ProfileSummary> {
        if (delayOnFetch) delay(1)
        return summaryResult
    }

    override suspend fun updateProfile(
        name: String,
        email: String,
        dateOfBirth: String?,
        gender: String?,
        nominee: ProfileNominee?,
    ): ApiResult<ProfileUser> = error("Not used in ProfileViewModelTest")

    override suspend fun verifyKyc(
        pan: String,
        name: String,
    ): ApiResult<Unit> = error("Not used in ProfileViewModelTest")

    override suspend fun logout(): ApiResult<Unit> {
        logoutCalls += 1
        if (delayOnLogout) delay(1)
        return logoutResult
    }

    override suspend fun requestDeleteAccount(): ApiResult<Unit> = deleteResult
}

private fun profileSummary(name: String = "Dushyant Mainwal"): ProfileSummary {
    return ProfileSummary(
        user = ProfileUser(
            id = "user-1",
            name = name,
            email = "dushyant@habitgold.com",
            mobileNumber = "9876543210",
            dateOfBirth = "",
            gender = "",
            pinCode = "110001",
            kycStatus = "VERIFIED",
            nominee = null,
            kyc = null,
            payoutVpa = "",
            payoutVpaVerified = false,
            vpas = emptyList(),
        ),
        totalGoldBalanceGrams = 0.5,
    )
}
