package com.habit.gold.feature.auth.domain

object AuthValidators {
    fun normalizePhone(rawValue: String): String {
        val digitsOnly = rawValue.filter(Char::isDigit)
        return if (digitsOnly.length > 10 && digitsOnly.startsWith("91")) {
            digitsOnly.takeLast(10)
        } else {
            digitsOnly.take(10)
        }
    }

    fun normalizeOtp(rawValue: String): String = rawValue.filter(Char::isDigit).take(6)

    fun normalizePinCode(rawValue: String): String = rawValue.filter(Char::isDigit).take(6)

    fun normalizeLegalName(rawValue: String): String = rawValue.filter { character ->
        character.isLetterOrDigit() || character.isWhitespace()
    }

    fun normalizeReferralCode(rawValue: String): String = rawValue.trim().uppercase()

    fun isPhoneValid(phoneNumber: String): Boolean = phoneNumber.length == 10

    fun isOtpValid(otp: String): Boolean = otp.length == 6

    fun isLegalNameValid(name: String): Boolean = name.trim().length >= 2

    fun isPinCodeValid(pinCode: String): Boolean = pinCode.length == 6

    fun isBasicDetailsComplete(
        user: AuthenticatedUser,
        isPinCodeRequired: Boolean,
    ): Boolean {
        val hasName = user.name.trim().isNotBlank()
        val hasRequiredPinCode = !isPinCodeRequired || user.pinCode.isNotBlank()
        return hasName && hasRequiredPinCode
    }
}
