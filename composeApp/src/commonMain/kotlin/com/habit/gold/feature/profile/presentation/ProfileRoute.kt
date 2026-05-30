package com.habit.gold.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.session.AuthSession
import com.habit.gold.core.storage.SecureStorage
import com.habit.gold.feature.profile.domain.model.ProfileSummary
import com.habit.gold.feature.profile.domain.model.ProfileUser
import com.habit.gold.feature.profile.domain.usecase.GetProfileSummaryUseCase
import com.habit.gold.feature.profile.domain.usecase.LogoutProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.RequestDeleteAccountUseCase
import com.habit.gold.feature.profile.domain.usecase.UpdateProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.VerifyProfileKycUseCase
import com.habit.gold.feature.delivery.presentation.DeliveryDestination
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.SetDefaultTradeVpaUseCase
import com.habit.gold.feature.trade.domain.usecase.VerifyTradeVpaUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.profile_biometric_disabled
import habitgoldmobile.composeapp.generated.resources.profile_biometric_enabled
import habitgoldmobile.composeapp.generated.resources.profile_biometric_prompt_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_biometric
import habitgoldmobile.composeapp.generated.resources.profile_hub_biometric_subtitle_format
import io.ktor.client.HttpClient
import org.jetbrains.compose.resources.stringResource

data class ProfileRouteDependencies(
    val getProfileSummaryUseCase: GetProfileSummaryUseCase,
    val updateProfileUseCase: UpdateProfileUseCase,
    val verifyProfileKycUseCase: VerifyProfileKycUseCase,
    val logoutProfileUseCase: LogoutProfileUseCase,
    val requestDeleteAccountUseCase: RequestDeleteAccountUseCase,
    val getTradeUserVpasUseCase: GetTradeUserVpasUseCase,
    val setDefaultTradeVpaUseCase: SetDefaultTradeVpaUseCase,
    val verifyTradeVpaUseCase: VerifyTradeVpaUseCase,
    val httpClient: HttpClient,
    val secureStorage: SecureStorage,
    val appVersion: String,
)

@Composable
fun ProfileRoute(
    dependencies: ProfileRouteDependencies,
    destination: ProfileDestination,
    session: AuthSession,
    sessionResetKey: String,
    onBackToHome: () -> Unit,
    onNavigate: (ProfileDestination) -> Unit,
    onOpenAutopay: () -> Unit,
    onOpenReferEarn: () -> Unit,
    onBiometricStateChanged: (Boolean) -> Unit = {},
    onOpenDelivery: (DeliveryDestination) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when (destination) {
        ProfileDestination.Hub -> {
            val viewModel = viewModel(key = "profile:$sessionResetKey") {
                ProfileViewModel(
                    initialSummary = session.toProfileSeedSummary(),
                    getProfileSummaryUseCase = dependencies.getProfileSummaryUseCase,
                    logoutProfileUseCase = dependencies.logoutProfileUseCase,
                    requestDeleteAccountUseCase = dependencies.requestDeleteAccountUseCase,
                )
            }
            val state = viewModel.state.collectAsStateWithLifecycle()
            val biometricAuthenticator = rememberProfileBiometricAuthenticator()
            val securityStore = remember(dependencies.secureStorage) { ProfileSecurityStore(dependencies.secureStorage) }
            var biometricEnabled by rememberSaveable { mutableStateOf(false) }
            val biometricTitle = stringResource(Res.string.profile_hub_biometric)
            val biometricSubtitle = stringResource(
                Res.string.profile_hub_biometric_subtitle_format,
                biometricAuthenticator.label,
            )
            val biometricPromptSubtitle = stringResource(
                Res.string.profile_biometric_prompt_subtitle,
                biometricAuthenticator.label,
            )
            val biometricEnabledMessage = stringResource(
                Res.string.profile_biometric_enabled,
                biometricAuthenticator.label,
            )
            val biometricDisabledMessage = stringResource(
                Res.string.profile_biometric_disabled,
                biometricAuthenticator.label,
            )
            val cancelLabel = stringResource(Res.string.common_cancel)

            LaunchedEffect(securityStore) {
                biometricEnabled = securityStore.read().biometricEnabled
            }

            LaunchedEffect(viewModel) {
                viewModel.onIntent(ProfileIntent.Load)
            }

            PlatformBackHandler(
                enabled = true,
                onBack = onBackToHome,
            )

            ProfileScreen(
                state = state.value,
                appVersion = dependencies.appVersion,
                onBackClick = onBackToHome,
                onRefresh = { viewModel.onIntent(ProfileIntent.Refresh) },
                onDismissError = { viewModel.onIntent(ProfileIntent.ClearError) },
                onOpenPersonalInfo = { onNavigate(ProfileDestination.PersonalInfo(state.value.summary)) },
                onOpenNominee = { onNavigate(ProfileDestination.Nominee(state.value.summary)) },
                onOpenKyc = { onNavigate(ProfileDestination.Kyc(state.value.summary)) },
                biometricEnabled = biometricEnabled,
                biometricSubtitle = biometricSubtitle,
                onToggleBiometric = { enabled ->
                    if (!enabled) {
                        securityStore.setBiometricEnabled(false)
                        biometricEnabled = false
                        onBiometricStateChanged(false)
                        return@ProfileScreen ProfileBiometricToggleResult(
                            enabled = false,
                            message = biometricDisabledMessage,
                        )
                    }

                    when (
                        val result = biometricAuthenticator.authenticate(
                            promptTitle = biometricTitle,
                            promptSubtitle = biometricPromptSubtitle,
                            cancelLabel = cancelLabel,
                        )
                    ) {
                        ProfileBiometricAuthResult.Success -> {
                            securityStore.setBiometricEnabled(true)
                            biometricEnabled = true
                            onBiometricStateChanged(true)
                            ProfileBiometricToggleResult(
                                enabled = true,
                                message = biometricEnabledMessage,
                            )
                        }

                        is ProfileBiometricAuthResult.Unavailable -> ProfileBiometricToggleResult(
                            enabled = false,
                            message = result.message,
                        )

                        is ProfileBiometricAuthResult.Error -> ProfileBiometricToggleResult(
                            enabled = false,
                            message = result.message,
                        )
                    }
                },
                onOpenAutopay = onOpenAutopay,
                onOpenVpaList = { onNavigate(ProfileDestination.VpaList) },
                onOpenTrackOrder = { onOpenDelivery(DeliveryDestination.Tracking) },
                onOpenSavedAddresses = { onOpenDelivery(DeliveryDestination.AddressList) },
                onOpenReferEarn = onOpenReferEarn,
                onOpenHelpCenter = {
                    onNavigate(ProfileDestination.HelpCenter(returnDestination = ProfileDestination.Hub))
                },
                onOpenContactUs = {
                    onNavigate(
                        ProfileDestination.ContactUs(
                            seedSummary = state.value.summary,
                            returnDestination = ProfileDestination.Hub,
                        ),
                    )
                },
                onConfirmLogout = { viewModel.onIntent(ProfileIntent.Logout) },
                onConfirmDeleteAccount = { viewModel.onIntent(ProfileIntent.DeleteAccount) },
                modifier = modifier,
            )
        }

        is ProfileDestination.PersonalInfo -> {
            PlatformBackHandler(
                enabled = true,
                onBack = { onNavigate(ProfileDestination.Hub) },
            )
            ProfilePersonalInfoRoute(
                seedSummary = destination.seedSummary,
                updateProfileUseCase = dependencies.updateProfileUseCase,
                onBackClick = { onNavigate(ProfileDestination.Hub) },
                modifier = modifier,
            )
        }

        is ProfileDestination.Kyc -> {
            PlatformBackHandler(
                enabled = true,
                onBack = { onNavigate(ProfileDestination.Hub) },
            )
            ProfileKycRoute(
                seedSummary = destination.seedSummary,
                verifyProfileKycUseCase = dependencies.verifyProfileKycUseCase,
                onBackClick = { onNavigate(ProfileDestination.Hub) },
                modifier = modifier,
            )
        }

        is ProfileDestination.Nominee -> {
            PlatformBackHandler(
                enabled = true,
                onBack = { onNavigate(ProfileDestination.Hub) },
            )
            ProfileNomineeRoute(
                seedSummary = destination.seedSummary,
                updateProfileUseCase = dependencies.updateProfileUseCase,
                onBackClick = { onNavigate(ProfileDestination.Hub) },
                modifier = modifier,
            )
        }

        is ProfileDestination.HelpCenter -> ProfileHelpCenterScreen(
            onBackClick = {
                destination.returnDestination?.let(onNavigate) ?: onBackToHome()
            },
            onOpenContactUs = {
                onNavigate(ProfileDestination.ContactUs(returnDestination = destination))
            },
            modifier = modifier,
        )

        ProfileDestination.VpaList -> {
            PlatformBackHandler(
                enabled = true,
                onBack = { onNavigate(ProfileDestination.Hub) },
            )
            ProfileVpaListRoute(
                getTradeUserVpasUseCase = dependencies.getTradeUserVpasUseCase,
                setDefaultTradeVpaUseCase = dependencies.setDefaultTradeVpaUseCase,
                verifyTradeVpaUseCase = dependencies.verifyTradeVpaUseCase,
                onBackClick = { onNavigate(ProfileDestination.Hub) },
                modifier = modifier,
            )
        }

        is ProfileDestination.ContactUs -> ProfileContactUsScreen(
            httpClient = dependencies.httpClient,
            userName = destination.seedSummary?.user?.name.orEmpty().ifBlank { session.user?.name.orEmpty() },
            userPhone = destination.seedSummary?.user?.mobileNumber.orEmpty().ifBlank { session.user?.phoneNumber.orEmpty() },
            userEmail = destination.seedSummary?.user?.email.orEmpty().ifBlank { session.user?.email.orEmpty() },
            userGender = destination.seedSummary?.user?.gender.orEmpty(),
            userDob = formatDateOfBirthForDisplay(destination.seedSummary?.user?.dateOfBirth),
            onBackClick = {
                destination.returnDestination?.let(onNavigate) ?: onBackToHome()
            },
            modifier = modifier,
        )

        ProfileDestination.TrackOrder -> {
            LaunchedEffect(Unit) {
                onOpenDelivery(DeliveryDestination.Tracking)
            }
        }

        ProfileDestination.SavedAddresses -> {
            LaunchedEffect(Unit) {
                onOpenDelivery(DeliveryDestination.AddressList)
            }
        }
    }
}

private fun AuthSession.toProfileSeedSummary(): ProfileSummary? {
    val user = user ?: return null
    return ProfileSummary(
        user = ProfileUser(
            id = user.id.orEmpty(),
            name = user.name,
            email = user.email,
            mobileNumber = user.phoneNumber,
            dateOfBirth = "",
            gender = "",
            pinCode = user.pinCode,
            kycStatus = "",
            nominee = null,
            kyc = null,
            payoutVpa = "",
            payoutVpaVerified = false,
            vpas = emptyList(),
        ),
        totalGoldBalanceGrams = 0.0,
    )
}
