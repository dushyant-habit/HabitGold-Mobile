package com.habit.gold.feature.trade.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.habit.gold.ui.theme.HabitGoldTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WithdrawalModeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun proceed_usesCashByDefault() {
        var cashSelections = 0
        var coinSelections = 0

        composeRule.setContent {
            HabitGoldTheme {
                WithdrawalModeScreen(
                    onBackClick = {},
                    onNavigateToCoinMode = { coinSelections += 1 },
                    onNavigateToCashMode = { cashSelections += 1 },
                )
            }
        }

        composeRule.onNodeWithText("Proceed").performClick()

        assertEquals(1, cashSelections)
        assertEquals(0, coinSelections)
    }

    @Test
    fun selectingCoin_thenProceed_routesToCoinFlow() {
        var cashSelections = 0
        var coinSelections = 0

        composeRule.setContent {
            HabitGoldTheme {
                WithdrawalModeScreen(
                    onBackClick = {},
                    onNavigateToCoinMode = { coinSelections += 1 },
                    onNavigateToCashMode = { cashSelections += 1 },
                )
            }
        }

        composeRule.onNodeWithText("Get Coin").performClick()
        composeRule.onNodeWithText("Proceed").performClick()

        assertEquals(0, cashSelections)
        assertEquals(1, coinSelections)
    }
}
