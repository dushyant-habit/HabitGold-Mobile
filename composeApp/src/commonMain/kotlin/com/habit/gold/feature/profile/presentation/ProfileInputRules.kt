package com.habit.gold.feature.profile.presentation

internal object ProfileInputRules {
    private val legalNameRegex = Regex("[A-Za-z ]")
    private val nomineeNameRegex = Regex("[A-Za-z ]")
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun normalizeLegalName(rawValue: String): String {
        return rawValue.filter { legalNameRegex.matches(it.toString()) }
            .replace(Regex("\\s+"), " ")
            .trimStart()
            .take(60)
    }

    fun normalizeEmail(rawValue: String): String = rawValue.trim().take(80)

    fun normalizePan(rawValue: String): String {
        return rawValue.uppercase()
            .filter { it.isLetterOrDigit() }
            .take(10)
    }

    fun normalizeNomineeName(rawValue: String): String {
        return rawValue.filter { nomineeNameRegex.matches(it.toString()) }
            .replace(Regex("\\s+"), " ")
            .trimStart()
            .take(60)
    }

    fun normalizePhone(rawValue: String): String = rawValue.filter(Char::isDigit).take(10)

    fun normalizePin(rawValue: String): String = rawValue.filter(Char::isDigit).take(4)

    fun isLegalNameValid(value: String): Boolean = value.trim().length >= 2

    fun isEmailValid(value: String): Boolean = value.isBlank() || emailRegex.matches(value.trim())

    fun isPanValid(value: String): Boolean {
        val normalized = value.trim().uppercase()
        return normalized.length == 10 &&
            normalized.take(5).all(Char::isLetter) &&
            normalized.drop(5).take(4).all(Char::isDigit) &&
            normalized.last().isLetter()
    }

    fun isNomineeNameValid(value: String): Boolean = value.trim().length >= 2

    fun isNomineePhoneValid(value: String): Boolean = value.length == 10
}
