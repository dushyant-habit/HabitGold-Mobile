package com.habit.gold.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.habit.gold.core.navigation.MainTab

/**
 * Centralizes user-facing copy so migrated shared features do not scatter hardcoded strings.
 */
interface AppStrings {
    val authTagline: String
    val authMobileNumberLabel: String
    val authPhoneNumberPlaceholder: String
    val authRequestOtpCta: String
    val authTermsFooter: String
    val authOtpTitle: String
    val authOtpHeading: String
    val authVerifyOtpCta: String
    val authResendLabel: String
    val authDidNotReceiveOtp: String
    val authProfileTitle: String
    val authBasicInfoHeading: String
    val authBasicInfoDescription: String
    val authVerifiedMobileLabel: String
    val authLegalNameLabel: String
    val authLegalNamePlaceholder: String
    val authLegalNameSupportingText: String
    val authEmailLabel: String
    val authEmailPlaceholder: String
    val authPinCodeLabel: String
    val authPinCodePlaceholder: String
    val authContinueCta: String
    val authBenefit24kGold: String
    val authBenefitSecure: String
    val authBenefitInstant: String
    val authCompletingSignInTitle: String
    val authCompletingSignInMessage: String
    val authInvalidPhoneError: String
    val authInvalidOtpError: String
    val authBlankNameError: String
    val authInvalidEmailError: String
    val authInvalidPinCodeError: String
    val splashPreparingMessage: String
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
    val shellTransactionsTitle: String
    val shellTransactionsDescription: String
    val shellProfileTitle: String
    val shellPhoneLabel: String
    val shellEmailLabel: String
    val shellPinCodeLabel: String
    val shellNotAddedYet: String
    val shellLogoutCta: String

    fun authOtpSentMessage(phoneNumber: String): String
    fun authOtpReferenceId(refId: String): String
    fun authResendCountdown(secondsRemaining: Int): String
    fun formatPhoneNumber(phoneNumber: String): String
    fun mainTabLabel(tab: MainTab): String
}

object EnglishAppStrings : AppStrings {
    override val authTagline = "Invest in a habit. Invest in gold."
    override val authMobileNumberLabel = "MOBILE NUMBER"
    override val authPhoneNumberPlaceholder = "98765 43210"
    override val authRequestOtpCta = "Get OTP"
    override val authTermsFooter = "By continuing, you agree to our Terms & Conditions and Privacy Policy."
    override val authOtpTitle = "Enter OTP"
    override val authOtpHeading = "Enter your OTP"
    override val authVerifyOtpCta = "Verify OTP"
    override val authResendLabel = "Resend"
    override val authDidNotReceiveOtp = "Didn't receive the OTP?"
    override val authProfileTitle = "Profile"
    override val authBasicInfoHeading = "Tell us about yourself"
    override val authBasicInfoDescription = "We need these details to set up your HabitGold profile across Android and iOS."
    override val authVerifiedMobileLabel = "Verified mobile number"
    override val authLegalNameLabel = "LEGAL NAME"
    override val authLegalNamePlaceholder = "e.g. Dushyant Mainwal"
    override val authLegalNameSupportingText = "Must match your bank account records exactly."
    override val authEmailLabel = "EMAIL ADDRESS"
    override val authEmailPlaceholder = "you@example.com"
    override val authPinCodeLabel = "PINCODE"
    override val authPinCodePlaceholder = "Enter 6-digit pincode"
    override val authContinueCta = "Continue"
    override val authBenefit24kGold = "24K Gold"
    override val authBenefitSecure = "Secure"
    override val authBenefitInstant = "Instant"
    override val authCompletingSignInTitle = "Finalizing your account"
    override val authCompletingSignInMessage = "We're syncing your HabitGold session before handing you off to the main app."
    override val authInvalidPhoneError = "Enter a valid 10-digit mobile number."
    override val authInvalidOtpError = "Enter the 6-digit OTP."
    override val authBlankNameError = "Enter your full name."
    override val authInvalidEmailError = "Enter a valid email address."
    override val authInvalidPinCodeError = "Enter a valid 6-digit pin code."
    override val splashPreparingMessage = "Preparing your shared HabitGold experience..."
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
    override val shellTransactionsTitle = "Transactions shell"
    override val shellTransactionsDescription = "This placeholder keeps the bottom-navigation contract real while we migrate transaction history, status, invoices, and related drilldowns."
    override val shellProfileTitle = "Profile shell"
    override val shellPhoneLabel = "Phone"
    override val shellEmailLabel = "Email"
    override val shellPinCodeLabel = "Pincode"
    override val shellNotAddedYet = "Not added yet"
    override val shellLogoutCta = "Log out"

    override fun authOtpSentMessage(phoneNumber: String): String {
        return "We've sent a 6-digit code to ${formatPhoneNumber(phoneNumber)}"
    }

    override fun authOtpReferenceId(refId: String): String = "Reference ID: $refId"

    override fun authResendCountdown(secondsRemaining: Int): String {
        return "Resend available in ${secondsRemaining}s"
    }

    override fun formatPhoneNumber(phoneNumber: String): String = "+91 $phoneNumber"

    override fun mainTabLabel(tab: MainTab): String {
        return when (tab) {
            MainTab.Home -> "Home"
            MainTab.Transactions -> "Transactions"
            MainTab.Profile -> "Profile"
        }
    }
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnglishAppStrings }

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
