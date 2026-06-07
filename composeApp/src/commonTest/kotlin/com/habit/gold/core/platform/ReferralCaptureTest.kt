package com.habit.gold.core.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReferralCaptureTest {

    @Test
    fun `extracts referral code from query parameters`() {
        assertEquals(
            "SAVEGOLD20",
            extractReferralCodeFromUrl("https://habitgold.com/?referralCode=savegold20"),
        )
        assertEquals(
            "FRIEND123",
            extractReferralCodeFromUrl("https://habitgold.com/?code=friend123"),
        )
    }

    @Test
    fun `extracts referral code from referral path routes only`() {
        assertEquals(
            "SAVEGOLD20",
            extractReferralCodeFromUrl("habitgold-staging://refer/savegold20"),
        )
        assertEquals(
            "SAVEGOLD20",
            extractReferralCodeFromUrl("https://habitgold.com/refer/savegold20"),
        )
        assertEquals(
            "FRIEND123",
            extractReferralCodeFromUrl("https://habitgold.com/invite/Friend123"),
        )
    }

    @Test
    fun `ignores unrelated routes and invalid codes`() {
        assertNull(extractReferralCodeFromUrl("https://habitgold.com/privacy"))
        assertNull(extractReferralCodeFromUrl("https://habitgold.com/refer/ab"))
        assertNull(extractReferralCodeFromUrl("https://habitgold.com/?code=not-valid!"))
    }
}
