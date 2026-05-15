package com.habit.gold.feature.auth.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthValidatorsTest {

    @Test
    fun `normalizes phone to 10 digits`() {
        assertEquals("9876543210", AuthValidators.normalizePhone("+91 98765-43210"))
        assertEquals("9876543210", AuthValidators.normalizePhone("9876543210123"))
    }

    @Test
    fun `normalizes legal name using android onboarding rules`() {
        assertEquals("Johnathan 123", AuthValidators.normalizeLegalName("Johnathan@ 123!"))
    }

    @Test
    fun `validates otp and pin code lengths`() {
        assertTrue(AuthValidators.isOtpValid("123456"))
        assertFalse(AuthValidators.isOtpValid("12345"))
        assertTrue(AuthValidators.isPinCodeValid("560001"))
        assertFalse(AuthValidators.isPinCodeValid("5600"))
    }

    @Test
    fun `normalizes referral code to uppercase`() {
        assertEquals("FRIEND1", AuthValidators.normalizeReferralCode(" friend1 "))
    }

    @Test
    fun `requires legal name and optional pincode based on backend rule`() {
        assertTrue(
            AuthValidators.isBasicDetailsComplete(
                user = AuthenticatedUser(
                    phoneNumber = "9876543210",
                    name = "Habit Gold",
                    pinCode = "560001",
                ),
                isPinCodeRequired = true,
            ),
        )
        assertTrue(
            AuthValidators.isBasicDetailsComplete(
                user = AuthenticatedUser(
                    phoneNumber = "9876543210",
                    name = "Habit Gold",
                ),
                isPinCodeRequired = false,
            ),
        )
        assertFalse(
            AuthValidators.isBasicDetailsComplete(
                user = AuthenticatedUser(
                    phoneNumber = "9876543210",
                    name = "",
                    pinCode = "560001",
                ),
                isPinCodeRequired = true,
            ),
        )
    }
}
