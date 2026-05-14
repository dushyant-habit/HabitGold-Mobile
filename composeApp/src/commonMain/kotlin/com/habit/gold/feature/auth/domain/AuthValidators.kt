package com.habit.gold.feature.auth.domain

object AuthValidators {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun normalizePhone(rawValue: String): String = rawValue.filter(Char::isDigit).takeLast(10)

    fun normalizeOtp(rawValue: String): String = rawValue.filter(Char::isDigit).take(6)

    fun normalizePinCode(rawValue: String): String = rawValue.filter(Char::isDigit).take(6)

    fun isPhoneValid(phoneNumber: String): Boolean = phoneNumber.length == 10

    fun isOtpValid(otp: String): Boolean = otp.length == 6

    fun isEmailValid(email: String): Boolean = emailRegex.matches(email.trim())

    fun isPinCodeValid(pinCode: String): Boolean = pinCode.length == 6

    fun isBasicInfoComplete(user: AuthenticatedUser): Boolean {
        return user.name.isNotBlank() && user.email.isNotBlank() && user.pinCode.isNotBlank()
    }
}
