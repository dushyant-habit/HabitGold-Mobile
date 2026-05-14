package com.habit.gold.feature.auth.presentation

import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.localization.EnglishAppStrings
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import com.habit.gold.feature.auth.domain.OtpRequestResult
import com.habit.gold.feature.auth.domain.VerifyOtpResult
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AuthFlowViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun verifyOtp_routes_complete_profiles_to_handoff() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            repository = FakeAuthRepository(
                verifyOtpResult = ApiResult.Success(
                    VerifyOtpResult(
                        user = completeUser(),
                        requiresBasicInfo = false,
                    )
                )
            )
        )
        advanceUntilIdle()

        viewModel.onIntent(AuthIntent.UpdatePhoneNumber("9876543210"))
        viewModel.onIntent(AuthIntent.UpdateOtp("123456"))
        viewModel.onIntent(AuthIntent.VerifyOtp)
        advanceUntilIdle()

        assertEquals(AuthStep.Handoff, viewModel.uiState.value.screen)
        assertEquals(completeUser(), viewModel.uiState.value.user)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun restored_incomplete_profile_routes_to_basic_info() = runTest(testDispatcher) {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = incompleteUser(),
            isProfileComplete = false,
        )

        val viewModel = createViewModel(
            repository = FakeAuthRepository(),
            sessionStore = sessionStore,
        )
        advanceUntilIdle()

        assertEquals(AuthStep.BasicInfo, viewModel.uiState.value.screen)
        assertEquals(incompleteUser().phoneNumber, viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun submitBasicInfo_routes_to_handoff_after_success() = runTest(testDispatcher) {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = incompleteUser(),
            isProfileComplete = false,
        )
        val updatedUser = completeUser()
        val viewModel = createViewModel(
            repository = FakeAuthRepository(
                submitBasicInfoResult = ApiResult.Success(updatedUser)
            ),
            sessionStore = sessionStore,
        )
        advanceUntilIdle()

        viewModel.onIntent(AuthIntent.UpdateName(updatedUser.name))
        viewModel.onIntent(AuthIntent.UpdateEmail(updatedUser.email))
        viewModel.onIntent(AuthIntent.UpdatePinCode(updatedUser.pinCode))
        viewModel.onIntent(AuthIntent.SubmitBasicInfo)
        advanceUntilIdle()

        assertEquals(AuthStep.Handoff, viewModel.uiState.value.screen)
        assertEquals(updatedUser, viewModel.uiState.value.user)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    private fun createViewModel(
        repository: AuthRepository,
        sessionStore: SessionStore = createSessionStore(),
    ): AuthFlowViewModel {
        return AuthFlowViewModel(
            appConfig = AppConfig(
                appName = "HabitGold",
                bundleId = "com.habit.gold",
                environment = AppEnvironment.Staging,
                baseUrl = "https://staging.habitgold.com/v1/",
                enableNetworkLogs = false,
            ),
            platformInfo = PlatformInfo(
                name = "Test",
                version = "1.0",
                isDebugBinary = true,
            ),
            appStrings = EnglishAppStrings,
            authRepository = repository,
            sessionStore = sessionStore,
        )
    }

    private fun createSessionStore(): SessionStore {
        return SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        )
    }

    private fun completeUser(): AuthenticatedUser {
        return AuthenticatedUser(
            id = "user-1",
            phoneNumber = "9876543210",
            name = "Dushyant Mainwal",
            email = "dushyant@example.com",
            pinCode = "110001",
        )
    }

    private fun incompleteUser(): AuthenticatedUser {
        return AuthenticatedUser(
            id = "user-2",
            phoneNumber = "9876543210",
        )
    }
}

private class FakeAuthRepository(
    private val requestOtpResult: ApiResult<OtpRequestResult> = ApiResult.Failure(
        NetworkError(
            kind = NetworkErrorKind.Server,
            message = "requestOtp not configured for this test",
        )
    ),
    private val verifyOtpResult: ApiResult<VerifyOtpResult> = ApiResult.Failure(
        NetworkError(
            kind = NetworkErrorKind.Server,
            message = "verifyOtp not configured for this test",
        )
    ),
    private val submitBasicInfoResult: ApiResult<AuthenticatedUser> = ApiResult.Failure(
        NetworkError(
            kind = NetworkErrorKind.Server,
            message = "submitBasicInfo not configured for this test",
        )
    ),
) : AuthRepository {
    override suspend fun requestOtp(phoneNumber: String): ApiResult<OtpRequestResult> = requestOtpResult

    override suspend fun verifyOtp(phoneNumber: String, otp: String): ApiResult<VerifyOtpResult> = verifyOtpResult

    override suspend fun submitBasicInfo(
        name: String,
        email: String,
        pinCode: String,
    ): ApiResult<AuthenticatedUser> = submitBasicInfoResult
}
