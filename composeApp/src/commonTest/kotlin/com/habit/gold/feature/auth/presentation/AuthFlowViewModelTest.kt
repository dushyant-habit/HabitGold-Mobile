package com.habit.gold.feature.auth.presentation

import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.AppEnvironment
import com.habit.gold.core.localization.AppStrings
import com.habit.gold.core.navigation.MainTab
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
import com.habit.gold.feature.auth.domain.usecase.RequestOtpUseCase
import com.habit.gold.feature.auth.domain.usecase.SubmitBasicDetailsUseCase
import com.habit.gold.feature.auth.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
                        requiresBasicDetails = false,
                        isPinCodeRequired = true,
                    ),
                ),
            ),
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
    fun verifyOtp_routes_onboarding_profiles_to_basic_details() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            repository = FakeAuthRepository(
                verifyOtpResult = ApiResult.Success(
                    VerifyOtpResult(
                        user = incompleteUser(),
                        requiresBasicDetails = true,
                        isPinCodeRequired = false,
                    ),
                ),
            ),
        )
        advanceUntilIdle()

        viewModel.onIntent(AuthIntent.UpdatePhoneNumber("9876543210"))
        viewModel.onIntent(AuthIntent.UpdateOtp("123456"))
        viewModel.onIntent(AuthIntent.VerifyOtp)
        advanceUntilIdle()

        assertEquals(AuthStep.BasicDetails, viewModel.uiState.value.screen)
        assertEquals(false, viewModel.uiState.value.isPinCodeRequired)
        assertEquals(incompleteUser().phoneNumber, viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun restored_incomplete_profile_routes_to_basic_details() = runTest(testDispatcher) {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = incompleteUser(),
            isProfileComplete = false,
            isPinCodeRequired = false,
        )

        val viewModel = createViewModel(
            repository = FakeAuthRepository(),
            sessionStore = sessionStore,
        )
        advanceUntilIdle()

        assertEquals(AuthStep.BasicDetails, viewModel.uiState.value.screen)
        assertEquals(incompleteUser().phoneNumber, viewModel.uiState.value.phoneNumber)
        assertEquals(false, viewModel.uiState.value.isPinCodeRequired)
    }

    @Test
    fun requestOtp_starts_android_resend_interval() = runTest(testDispatcher) {
        val repository = FakeAuthRepository(
            requestOtpResponses = mutableListOf(
                ApiResult.Success(OtpRequestResult(refId = "ref-1", message = "sent")),
            ),
        )
        val viewModel = createViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.onIntent(AuthIntent.UpdatePhoneNumber("9876543210"))
        viewModel.onIntent(AuthIntent.RequestOtp)
        runCurrent()

        assertEquals(AuthStep.Otp, viewModel.uiState.value.screen)
        assertEquals(30, viewModel.uiState.value.resendSecondsRemaining)
        assertEquals(0, viewModel.uiState.value.resendAttempt)
    }

    @Test
    fun submitBasicDetails_uses_new_contract_and_routes_to_handoff() = runTest(testDispatcher) {
        val sessionStore = createSessionStore()
        sessionStore.saveAuthenticatedUser(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            user = incompleteUser(),
            isProfileComplete = false,
            isPinCodeRequired = true,
        )
        val repository = FakeAuthRepository(
            submitBasicDetailsResult = ApiResult.Success(completeUser()),
        )
        val viewModel = createViewModel(
            repository = repository,
            sessionStore = sessionStore,
        )
        advanceUntilIdle()

        viewModel.onIntent(AuthIntent.UpdateLegalName("Dushyant Mainwal"))
        viewModel.onIntent(AuthIntent.UpdatePinCode("110001"))
        viewModel.onIntent(AuthIntent.UpdateReferralCode("friend1"))
        viewModel.onIntent(AuthIntent.SubmitBasicDetails)
        advanceUntilIdle()

        assertEquals(AuthStep.Handoff, viewModel.uiState.value.screen)
        assertEquals(completeUser(), viewModel.uiState.value.user)
        assertEquals(
            SubmitBasicDetailsCall(
                name = "Dushyant Mainwal",
                pinCode = "110001",
                referralCode = "FRIEND1",
            ),
            repository.lastSubmitBasicDetailsCall,
        )
    }

    private fun createViewModel(
        repository: AuthRepository,
        sessionStore: SessionStore = createSessionStore(),
    ): AuthFlowViewModel {
        return AuthFlowViewModel(
            appConfig = AppConfig(
                appName = "HabitGold",
                bundleId = "com.habit.gold",
                appVersion = "1.0-debug",
                appPlatform = "android",
                environment = AppEnvironment.Staging,
                baseUrl = "https://staging.habitgold.com/v1/",
                enableNetworkLogs = false,
            ),
            platformInfo = PlatformInfo(
                name = "Test",
                version = "1.0",
                isDebugBinary = true,
            ),
            appStrings = TestAppStrings,
            requestOtpUseCase = RequestOtpUseCase(repository),
            verifyOtpUseCase = VerifyOtpUseCase(repository),
            submitBasicDetailsUseCase = SubmitBasicDetailsUseCase(repository),
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

private object TestAppStrings : AppStrings {
    override val authTagline = "Invest in a Habit. Invest in Gold."
    override val authMobileNumberLabel = "MOBILE NUMBER"
    override val authPhoneNumberPlaceholder = "98765 43210"
    override val authRequestOtpCta = "Get OTP"
    override val authOtpTitle = "Enter OTP"
    override val authOtpHeading = "Enter your OTP"
    override val authOtpSentIntro = "We have sent a 6-digit code to"
    override val authVerifyAndProceedCta = "Verify & Proceed"
    override val authResendLabel = "Resend OTP"
    override val authProfileTitle = "Profile"
    override val authBasicDetailsHeading = "Tell us about yourself"
    override val authBasicDetailsDescription = "We need these details to set up your secure Gold account."
    override val authLegalNameLabel = "Enter Your Full Name"
    override val authLegalNamePlaceholder = "e.g. Johnathan Doe"
    override val authLegalNameSupportingText = "Must match your bank account records exactly."
    override val authPinCodeLabel = "Location Pincode"
    override val authPinCodePlaceholder = "Enter 6-digit location pincode"
    override val authReferralHeading = "Have a referral code?"
    override val authReferralPlaceholder = "Code"
    override val authReferralAppliedLabel = "Referral code applied"
    override val authSecurityMessage = "YOUR DATA IS ENCRYPTED & SECURE"
    override val authConfirmAndProceedCta = "Confirm & Proceed"
    override val authFeaturePhysical = "100% Physical"
    override val authFeatureInsured = "100% Insured"
    override val authFeatureZeroFees = "Zero hidden fees"
    override val authTermsFooterPrefix = "By continuing, you agree to our"
    override val authCompletingSignInTitle = "Finalizing your account"
    override val authCompletingSignInMessage = "We're syncing your HabitGold session before handing you off to the main app."
    override val authInvalidPhoneError = "Enter a valid 10-digit mobile number"
    override val authInvalidOtpError = "Enter the 6-digit OTP"
    override val authInvalidLegalNameError = "Enter your full name"
    override val authInvalidPinCodeError = "Enter a valid 6-digit pincode to continue."
    override val authTermsLabel = "Terms"
    override val authPrivacyPolicyLabel = "Privacy Policy"
    override val authTermsUrl = "https://habitgold.com/terms"
    override val authPrivacyPolicyUrl = "https://habitgold.com/privacy_policy"
    override val splashTagline = "Invest in a Habit\nInvest in Gold"
    override val splashLoadingMessage = "Opening your gold locker"
    override val shellTitle = "HabitGold"
    override val shellDescription = "Phase 4 app shell is live. This shared container will host the real Home, Transactions, and Profile features as we migrate them."
    override val shellWelcomeBack = "Welcome back"
    override val shellHomeDescription = "Your shared post-login shell is ready. Home widgets and portfolio cards will plug into this screen during the next feature migration."
    override val shellSessionCardTitle = "Session"
    override val shellProfileCardTitle = "Profile"
    override val shellSessionActive = "Active"
    override val shellSessionLoggedOut = "Logged out"
    override val shellProfileComplete = "Complete"
    override val shellProfilePending = "Pending"
    override val shellRewardsTitle = "Rewards migration in progress"
    override val shellRewardsDescription = "Rewards stays on the bottom bar in Android. We are keeping that product flow intact while the rewards and referral screens are rebuilt."
    override val shellHistoryTitle = "History migration in progress"
    override val shellHistoryDescription = "History stays on the bottom bar in Android. Transactions, status drilldowns, and invoices will be restored here next."
    override val shellPhoneLabel = "Phone"
    override val shellEmailLabel = "Email"
    override val shellPinCodeLabel = "Pincode"
    override val shellNotAddedYet = "Not added yet"
    override val shellLogoutCta = "Log out"

    override fun authOtpReferenceId(refId: String): String = "Reference ID: $refId"

    override fun authResendCountdown(secondsRemaining: Int): String {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        return "Resend OTP in ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    override fun authFormattedPhoneNumber(phoneNumber: String): String = "+91 $phoneNumber"

    override fun mainTabLabel(tab: MainTab): String {
        return when (tab) {
            MainTab.Home -> "Home"
            MainTab.Rewards -> "Rewards"
            MainTab.History -> "History"
        }
    }
}

private data class SubmitBasicDetailsCall(
    val name: String,
    val pinCode: String?,
    val referralCode: String?,
)

private class FakeAuthRepository(
    requestOtpResponses: MutableList<ApiResult<OtpRequestResult>> = mutableListOf(
        ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Server,
                message = "requestOtp not configured for this test",
            ),
        ),
    ),
    private val verifyOtpResult: ApiResult<VerifyOtpResult> = ApiResult.Failure(
        NetworkError(
            kind = NetworkErrorKind.Server,
            message = "verifyOtp not configured for this test",
        ),
    ),
    private val submitBasicDetailsResult: ApiResult<AuthenticatedUser> = ApiResult.Failure(
        NetworkError(
            kind = NetworkErrorKind.Server,
            message = "submitBasicDetails not configured for this test",
        ),
    ),
) : AuthRepository {
    private val requestOtpQueue = requestOtpResponses

    var lastSubmitBasicDetailsCall: SubmitBasicDetailsCall? = null
        private set

    override suspend fun requestOtp(phoneNumber: String): ApiResult<OtpRequestResult> {
        return requestOtpQueue.removeFirstOrNull()
            ?: ApiResult.Failure(
                NetworkError(
                    kind = NetworkErrorKind.Server,
                    message = "requestOtp queue exhausted",
                ),
            )
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): ApiResult<VerifyOtpResult> = verifyOtpResult

    override suspend fun submitBasicDetails(
        name: String,
        pinCode: String?,
        referralCode: String?,
    ): ApiResult<AuthenticatedUser> {
        lastSubmitBasicDetailsCall = SubmitBasicDetailsCall(
            name = name,
            pinCode = pinCode,
            referralCode = referralCode,
        )
        return submitBasicDetailsResult
    }
}
