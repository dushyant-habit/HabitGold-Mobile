package com.habit.gold.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.habit.gold.core.navigation.MainTab
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.auth_basic_details_description
import habitgoldmobile.composeapp.generated.resources.auth_basic_details_heading
import habitgoldmobile.composeapp.generated.resources.auth_completing_sign_in_message
import habitgoldmobile.composeapp.generated.resources.auth_completing_sign_in_title
import habitgoldmobile.composeapp.generated.resources.auth_confirm_and_proceed_cta
import habitgoldmobile.composeapp.generated.resources.auth_feature_insured
import habitgoldmobile.composeapp.generated.resources.auth_feature_physical
import habitgoldmobile.composeapp.generated.resources.auth_feature_zero_fees
import habitgoldmobile.composeapp.generated.resources.auth_formatted_phone_number
import habitgoldmobile.composeapp.generated.resources.auth_invalid_legal_name_error
import habitgoldmobile.composeapp.generated.resources.auth_invalid_otp_error
import habitgoldmobile.composeapp.generated.resources.auth_invalid_phone_error
import habitgoldmobile.composeapp.generated.resources.auth_invalid_pin_code_error
import habitgoldmobile.composeapp.generated.resources.auth_legal_name_label
import habitgoldmobile.composeapp.generated.resources.auth_legal_name_placeholder
import habitgoldmobile.composeapp.generated.resources.auth_legal_name_supporting_text
import habitgoldmobile.composeapp.generated.resources.auth_mobile_number_label
import habitgoldmobile.composeapp.generated.resources.auth_otp_heading
import habitgoldmobile.composeapp.generated.resources.auth_otp_reference_id
import habitgoldmobile.composeapp.generated.resources.auth_otp_sent_intro
import habitgoldmobile.composeapp.generated.resources.auth_otp_title
import habitgoldmobile.composeapp.generated.resources.auth_phone_number_placeholder
import habitgoldmobile.composeapp.generated.resources.auth_pin_code_label
import habitgoldmobile.composeapp.generated.resources.auth_pin_code_placeholder
import habitgoldmobile.composeapp.generated.resources.auth_privacy_policy_label
import habitgoldmobile.composeapp.generated.resources.auth_privacy_policy_url
import habitgoldmobile.composeapp.generated.resources.auth_profile_title
import habitgoldmobile.composeapp.generated.resources.auth_referral_applied_label
import habitgoldmobile.composeapp.generated.resources.auth_referral_heading
import habitgoldmobile.composeapp.generated.resources.auth_referral_placeholder
import habitgoldmobile.composeapp.generated.resources.auth_request_otp_cta
import habitgoldmobile.composeapp.generated.resources.auth_resend_countdown
import habitgoldmobile.composeapp.generated.resources.auth_resend_label
import habitgoldmobile.composeapp.generated.resources.auth_security_message
import habitgoldmobile.composeapp.generated.resources.auth_tagline
import habitgoldmobile.composeapp.generated.resources.auth_terms_footer_prefix
import habitgoldmobile.composeapp.generated.resources.auth_terms_label
import habitgoldmobile.composeapp.generated.resources.auth_terms_url
import habitgoldmobile.composeapp.generated.resources.auth_verify_and_proceed_cta
import habitgoldmobile.composeapp.generated.resources.main_tab_home
import habitgoldmobile.composeapp.generated.resources.main_tab_history
import habitgoldmobile.composeapp.generated.resources.main_tab_rewards
import habitgoldmobile.composeapp.generated.resources.shell_description
import habitgoldmobile.composeapp.generated.resources.shell_email_label
import habitgoldmobile.composeapp.generated.resources.shell_home_description
import habitgoldmobile.composeapp.generated.resources.shell_logout_cta
import habitgoldmobile.composeapp.generated.resources.shell_not_added_yet
import habitgoldmobile.composeapp.generated.resources.shell_phone_label
import habitgoldmobile.composeapp.generated.resources.shell_pin_code_label
import habitgoldmobile.composeapp.generated.resources.shell_profile_card_title
import habitgoldmobile.composeapp.generated.resources.shell_profile_complete
import habitgoldmobile.composeapp.generated.resources.shell_profile_pending
import habitgoldmobile.composeapp.generated.resources.shell_history_description
import habitgoldmobile.composeapp.generated.resources.shell_history_title
import habitgoldmobile.composeapp.generated.resources.shell_rewards_description
import habitgoldmobile.composeapp.generated.resources.shell_rewards_title
import habitgoldmobile.composeapp.generated.resources.shell_session_active
import habitgoldmobile.composeapp.generated.resources.shell_session_card_title
import habitgoldmobile.composeapp.generated.resources.shell_session_logged_out
import habitgoldmobile.composeapp.generated.resources.shell_title
import habitgoldmobile.composeapp.generated.resources.shell_welcome_back
import habitgoldmobile.composeapp.generated.resources.splash_loading_message
import habitgoldmobile.composeapp.generated.resources.splash_tagline
import org.jetbrains.compose.resources.stringResource

/**
 * Centralizes user-facing copy while the project migrates toward direct resource-backed localization.
 */
interface AppStrings {
    val authTagline: String
    val authMobileNumberLabel: String
    val authPhoneNumberPlaceholder: String
    val authRequestOtpCta: String
    val authOtpTitle: String
    val authOtpHeading: String
    val authOtpSentIntro: String
    val authVerifyAndProceedCta: String
    val authResendLabel: String
    val authProfileTitle: String
    val authBasicDetailsHeading: String
    val authBasicDetailsDescription: String
    val authLegalNameLabel: String
    val authLegalNamePlaceholder: String
    val authLegalNameSupportingText: String
    val authPinCodeLabel: String
    val authPinCodePlaceholder: String
    val authReferralHeading: String
    val authReferralPlaceholder: String
    val authReferralAppliedLabel: String
    val authSecurityMessage: String
    val authConfirmAndProceedCta: String
    val authFeaturePhysical: String
    val authFeatureInsured: String
    val authFeatureZeroFees: String
    val authTermsFooterPrefix: String
    val authCompletingSignInTitle: String
    val authCompletingSignInMessage: String
    val authInvalidPhoneError: String
    val authInvalidOtpError: String
    val authInvalidLegalNameError: String
    val authInvalidPinCodeError: String
    val authTermsLabel: String
    val authPrivacyPolicyLabel: String
    val authTermsUrl: String
    val authPrivacyPolicyUrl: String
    val splashTagline: String
    val splashLoadingMessage: String
    val shellTitle: String
    val shellDescription: String
    val shellWelcomeBack: String
    val shellHomeDescription: String
    val shellSessionCardTitle: String
    val shellProfileCardTitle: String
    val shellSessionActive: String
    val shellSessionLoggedOut: String
    val shellProfileComplete: String
    val shellProfilePending: String
    val shellRewardsTitle: String
    val shellRewardsDescription: String
    val shellHistoryTitle: String
    val shellHistoryDescription: String
    val shellPhoneLabel: String
    val shellEmailLabel: String
    val shellPinCodeLabel: String
    val shellNotAddedYet: String
    val shellLogoutCta: String

    fun authOtpReferenceId(refId: String): String
    fun authResendCountdown(secondsRemaining: Int): String
    fun authFormattedPhoneNumber(phoneNumber: String): String
    fun mainTabLabel(tab: MainTab): String
}

private data class ResourceAppStrings(
    override val authTagline: String,
    override val authMobileNumberLabel: String,
    override val authPhoneNumberPlaceholder: String,
    override val authRequestOtpCta: String,
    override val authOtpTitle: String,
    override val authOtpHeading: String,
    override val authOtpSentIntro: String,
    override val authVerifyAndProceedCta: String,
    override val authResendLabel: String,
    override val authProfileTitle: String,
    override val authBasicDetailsHeading: String,
    override val authBasicDetailsDescription: String,
    override val authLegalNameLabel: String,
    override val authLegalNamePlaceholder: String,
    override val authLegalNameSupportingText: String,
    override val authPinCodeLabel: String,
    override val authPinCodePlaceholder: String,
    override val authReferralHeading: String,
    override val authReferralPlaceholder: String,
    override val authReferralAppliedLabel: String,
    override val authSecurityMessage: String,
    override val authConfirmAndProceedCta: String,
    override val authFeaturePhysical: String,
    override val authFeatureInsured: String,
    override val authFeatureZeroFees: String,
    override val authTermsFooterPrefix: String,
    override val authCompletingSignInTitle: String,
    override val authCompletingSignInMessage: String,
    override val authInvalidPhoneError: String,
    override val authInvalidOtpError: String,
    override val authInvalidLegalNameError: String,
    override val authInvalidPinCodeError: String,
    override val authTermsLabel: String,
    override val authPrivacyPolicyLabel: String,
    override val authTermsUrl: String,
    override val authPrivacyPolicyUrl: String,
    override val splashTagline: String,
    override val splashLoadingMessage: String,
    override val shellTitle: String,
    override val shellDescription: String,
    override val shellWelcomeBack: String,
    override val shellHomeDescription: String,
    override val shellSessionCardTitle: String,
    override val shellProfileCardTitle: String,
    override val shellSessionActive: String,
    override val shellSessionLoggedOut: String,
    override val shellProfileComplete: String,
    override val shellProfilePending: String,
    override val shellRewardsTitle: String,
    override val shellRewardsDescription: String,
    override val shellHistoryTitle: String,
    override val shellHistoryDescription: String,
    override val shellPhoneLabel: String,
    override val shellEmailLabel: String,
    override val shellPinCodeLabel: String,
    override val shellNotAddedYet: String,
    override val shellLogoutCta: String,
    private val otpReferenceTemplate: String,
    private val resendCountdownTemplate: String,
    private val formattedPhoneNumberTemplate: String,
    private val mainTabHome: String,
    private val mainTabRewards: String,
    private val mainTabHistory: String,
) : AppStrings {
    override fun authOtpReferenceId(refId: String): String = otpReferenceTemplate
        .replace("%1\$s", refId)

    override fun authResendCountdown(secondsRemaining: Int): String {
        val minutes = (secondsRemaining / 60).toString().padStart(2, '0')
        val seconds = (secondsRemaining % 60).toString().padStart(2, '0')
        return resendCountdownTemplate
            .replace("%1\$02d", minutes)
            .replace("%2\$02d", seconds)
    }

    override fun authFormattedPhoneNumber(phoneNumber: String): String = formattedPhoneNumberTemplate
        .replace("%1\$s", phoneNumber)

    override fun mainTabLabel(tab: MainTab): String {
        return when (tab) {
            MainTab.Home -> mainTabHome
            MainTab.Rewards -> mainTabRewards
            MainTab.History -> mainTabHistory
        }
    }
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> {
    error("AppStrings not provided")
}

@Composable
fun rememberAppStrings(): AppStrings {
    val authTagline = stringResource(Res.string.auth_tagline)
    val authMobileNumberLabel = stringResource(Res.string.auth_mobile_number_label)
    val authPhoneNumberPlaceholder = stringResource(Res.string.auth_phone_number_placeholder)
    val authRequestOtpCta = stringResource(Res.string.auth_request_otp_cta)
    val authOtpTitle = stringResource(Res.string.auth_otp_title)
    val authOtpHeading = stringResource(Res.string.auth_otp_heading)
    val authOtpSentIntro = stringResource(Res.string.auth_otp_sent_intro)
    val authVerifyAndProceedCta = stringResource(Res.string.auth_verify_and_proceed_cta)
    val authResendLabel = stringResource(Res.string.auth_resend_label)
    val authProfileTitle = stringResource(Res.string.auth_profile_title)
    val authBasicDetailsHeading = stringResource(Res.string.auth_basic_details_heading)
    val authBasicDetailsDescription = stringResource(Res.string.auth_basic_details_description)
    val authLegalNameLabel = stringResource(Res.string.auth_legal_name_label)
    val authLegalNamePlaceholder = stringResource(Res.string.auth_legal_name_placeholder)
    val authLegalNameSupportingText = stringResource(Res.string.auth_legal_name_supporting_text)
    val authPinCodeLabel = stringResource(Res.string.auth_pin_code_label)
    val authPinCodePlaceholder = stringResource(Res.string.auth_pin_code_placeholder)
    val authReferralHeading = stringResource(Res.string.auth_referral_heading)
    val authReferralPlaceholder = stringResource(Res.string.auth_referral_placeholder)
    val authReferralAppliedLabel = stringResource(Res.string.auth_referral_applied_label)
    val authSecurityMessage = stringResource(Res.string.auth_security_message)
    val authConfirmAndProceedCta = stringResource(Res.string.auth_confirm_and_proceed_cta)
    val authFeaturePhysical = stringResource(Res.string.auth_feature_physical)
    val authFeatureInsured = stringResource(Res.string.auth_feature_insured)
    val authFeatureZeroFees = stringResource(Res.string.auth_feature_zero_fees)
    val authTermsFooterPrefix = stringResource(Res.string.auth_terms_footer_prefix)
    val authCompletingSignInTitle = stringResource(Res.string.auth_completing_sign_in_title)
    val authCompletingSignInMessage = stringResource(Res.string.auth_completing_sign_in_message)
    val authInvalidPhoneError = stringResource(Res.string.auth_invalid_phone_error)
    val authInvalidOtpError = stringResource(Res.string.auth_invalid_otp_error)
    val authInvalidLegalNameError = stringResource(Res.string.auth_invalid_legal_name_error)
    val authInvalidPinCodeError = stringResource(Res.string.auth_invalid_pin_code_error)
    val authTermsLabel = stringResource(Res.string.auth_terms_label)
    val authPrivacyPolicyLabel = stringResource(Res.string.auth_privacy_policy_label)
    val authTermsUrl = stringResource(Res.string.auth_terms_url)
    val authPrivacyPolicyUrl = stringResource(Res.string.auth_privacy_policy_url)
    val splashTagline = stringResource(Res.string.splash_tagline)
    val splashLoadingMessage = stringResource(Res.string.splash_loading_message)
    val shellTitle = stringResource(Res.string.shell_title)
    val shellDescription = stringResource(Res.string.shell_description)
    val shellWelcomeBack = stringResource(Res.string.shell_welcome_back)
    val shellHomeDescription = stringResource(Res.string.shell_home_description)
    val shellSessionCardTitle = stringResource(Res.string.shell_session_card_title)
    val shellProfileCardTitle = stringResource(Res.string.shell_profile_card_title)
    val shellSessionActive = stringResource(Res.string.shell_session_active)
    val shellSessionLoggedOut = stringResource(Res.string.shell_session_logged_out)
    val shellProfileComplete = stringResource(Res.string.shell_profile_complete)
    val shellProfilePending = stringResource(Res.string.shell_profile_pending)
    val shellRewardsTitle = stringResource(Res.string.shell_rewards_title)
    val shellRewardsDescription = stringResource(Res.string.shell_rewards_description)
    val shellHistoryTitle = stringResource(Res.string.shell_history_title)
    val shellHistoryDescription = stringResource(Res.string.shell_history_description)
    val shellPhoneLabel = stringResource(Res.string.shell_phone_label)
    val shellEmailLabel = stringResource(Res.string.shell_email_label)
    val shellPinCodeLabel = stringResource(Res.string.shell_pin_code_label)
    val shellNotAddedYet = stringResource(Res.string.shell_not_added_yet)
    val shellLogoutCta = stringResource(Res.string.shell_logout_cta)
    val mainTabHome = stringResource(Res.string.main_tab_home)
    val mainTabRewards = stringResource(Res.string.main_tab_rewards)
    val mainTabHistory = stringResource(Res.string.main_tab_history)

    return ResourceAppStrings(
        authTagline = authTagline,
        authMobileNumberLabel = authMobileNumberLabel,
        authPhoneNumberPlaceholder = authPhoneNumberPlaceholder,
        authRequestOtpCta = authRequestOtpCta,
        authOtpTitle = authOtpTitle,
        authOtpHeading = authOtpHeading,
        authOtpSentIntro = authOtpSentIntro,
        authVerifyAndProceedCta = authVerifyAndProceedCta,
        authResendLabel = authResendLabel,
        authProfileTitle = authProfileTitle,
        authBasicDetailsHeading = authBasicDetailsHeading,
        authBasicDetailsDescription = authBasicDetailsDescription,
        authLegalNameLabel = authLegalNameLabel,
        authLegalNamePlaceholder = authLegalNamePlaceholder,
        authLegalNameSupportingText = authLegalNameSupportingText,
        authPinCodeLabel = authPinCodeLabel,
        authPinCodePlaceholder = authPinCodePlaceholder,
        authReferralHeading = authReferralHeading,
        authReferralPlaceholder = authReferralPlaceholder,
        authReferralAppliedLabel = authReferralAppliedLabel,
        authSecurityMessage = authSecurityMessage,
        authConfirmAndProceedCta = authConfirmAndProceedCta,
        authFeaturePhysical = authFeaturePhysical,
        authFeatureInsured = authFeatureInsured,
        authFeatureZeroFees = authFeatureZeroFees,
        authTermsFooterPrefix = authTermsFooterPrefix,
        authCompletingSignInTitle = authCompletingSignInTitle,
        authCompletingSignInMessage = authCompletingSignInMessage,
        authInvalidPhoneError = authInvalidPhoneError,
        authInvalidOtpError = authInvalidOtpError,
        authInvalidLegalNameError = authInvalidLegalNameError,
        authInvalidPinCodeError = authInvalidPinCodeError,
        authTermsLabel = authTermsLabel,
        authPrivacyPolicyLabel = authPrivacyPolicyLabel,
        authTermsUrl = authTermsUrl,
        authPrivacyPolicyUrl = authPrivacyPolicyUrl,
        splashTagline = splashTagline,
        splashLoadingMessage = splashLoadingMessage,
        shellTitle = shellTitle,
        shellDescription = shellDescription,
        shellWelcomeBack = shellWelcomeBack,
        shellHomeDescription = shellHomeDescription,
        shellSessionCardTitle = shellSessionCardTitle,
        shellProfileCardTitle = shellProfileCardTitle,
        shellSessionActive = shellSessionActive,
        shellSessionLoggedOut = shellSessionLoggedOut,
        shellProfileComplete = shellProfileComplete,
        shellProfilePending = shellProfilePending,
        shellRewardsTitle = shellRewardsTitle,
        shellRewardsDescription = shellRewardsDescription,
        shellHistoryTitle = shellHistoryTitle,
        shellHistoryDescription = shellHistoryDescription,
        shellPhoneLabel = shellPhoneLabel,
        shellEmailLabel = shellEmailLabel,
        shellPinCodeLabel = shellPinCodeLabel,
        shellNotAddedYet = shellNotAddedYet,
        shellLogoutCta = shellLogoutCta,
        otpReferenceTemplate = stringResource(Res.string.auth_otp_reference_id),
        resendCountdownTemplate = stringResource(Res.string.auth_resend_countdown),
        formattedPhoneNumberTemplate = stringResource(Res.string.auth_formatted_phone_number),
        mainTabHome = mainTabHome,
        mainTabRewards = mainTabRewards,
        mainTabHistory = mainTabHistory,
    )
}

@Composable
fun ProvideAppStrings(
    appStrings: AppStrings,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppStrings provides appStrings, content = content)
}

val appStrings: AppStrings
    @Composable
    @ReadOnlyComposable
    get() = LocalAppStrings.current
