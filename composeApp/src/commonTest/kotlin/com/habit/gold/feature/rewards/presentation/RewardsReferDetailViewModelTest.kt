package com.habit.gold.feature.rewards.presentation

import com.habit.gold.feature.rewards.domain.model.ReferDetails
import com.habit.gold.feature.rewards.domain.model.ReferDetailsReferralCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RewardsReferDetailViewModelTest {

    @Test
    fun `map refer detail uses default referral code and backend extension values`() {
        val ui = mapReferDetailUi(
            ReferDetails(
                lifetimeEarnings = "450",
                activeReferrals = 7,
                boosterIsActive = true,
                currentPercentage = "0.75",
                daysLeft = 11,
                totalDaysCap = 30,
                referralFirstBuyDays = 14,
                buyThresholdInr = "10000",
                buyBonusDays = 7,
                sipMinAmountInr = "2500",
                sipBonusDays = 5,
                referralCode = null,
                referralList = listOf(
                    ReferDetailsReferralCode(code = "TEST42", isDefault = true, isActive = true),
                ),
            ),
        )

        assertEquals("₹450", ui.lifetimeEarningsDisplay)
        assertEquals(7, ui.activeFriendsCount)
        assertEquals("0.75%", ui.cashbackPercentLabel)
        assertEquals("TEST42", ui.referralCode)
        assertEquals("Extend for +7 days", ui.buyExtensionTitle)
        assertEquals("With every ₹10,000 purchase", ui.buyExtensionSubtitle)
        assertEquals("Weekly ₹2,500 SIP = +5 days", ui.sipExtensionSubtitle)
        assertTrue(ui.estimateCashbackFraction > 0.0074f)
    }
}
