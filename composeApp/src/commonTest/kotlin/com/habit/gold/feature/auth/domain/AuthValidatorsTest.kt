package com.habit.gold.feature.auth.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthValidatorsTest {

    @Test
    fun `normalizes phone to 10 digits`() {
        assertEquals("9876543210", AuthValidators.normalizePhone("+91 98765-43210"))
    }

    @Test
    fun `validates otp and pin code lengths`() {
        assertTrue(AuthValidators.isOtpValid("123456"))
        assertFalse(AuthValidators.isOtpValid("12345"))
        assertTrue(AuthValidators.isPinCodeValid("560001"))
        assertFalse(AuthValidators.isPinCodeValid("5600"))
    }

    @Test
    fun `requires all basic info fields`() {
        assertTrue(
            AuthValidators.isBasicInfoComplete(
                AuthenticatedUser(
                    phoneNumber = "9876543210",
                    name = "Habit Gold",
                    email = "team@habitgold.com",
                    pinCode = "560001",
                )
            )
        )
        assertFalse(
            AuthValidators.isBasicInfoComplete(
                AuthenticatedUser(
                    phoneNumber = "9876543210",
                    name = "Habit Gold",
                    email = "",
                    pinCode = "560001",
                )
            )
        )
    }
}
