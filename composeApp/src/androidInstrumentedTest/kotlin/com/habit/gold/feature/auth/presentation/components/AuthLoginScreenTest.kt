package com.habit.gold.feature.auth.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.habit.gold.core.localization.ProvideAppStrings
import com.habit.gold.core.localization.rememberAppStrings
import com.habit.gold.feature.auth.presentation.AuthFlowUiState
import com.habit.gold.ui.theme.HabitGoldTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AuthLoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun requestOtpButton_enablesOnlyForValidPhone_andInvokesCallback() {
        var requestOtpClicks = 0

        composeRule.setContent {
            HabitGoldTheme {
                ProvideAppStrings(rememberAppStrings()) {
                    var state by remember { mutableStateOf(AuthFlowUiState()) }
                    AuthLoginScreen(
                        uiState = state,
                        onPhoneChanged = { state = state.copy(phoneNumber = it) },
                        onRequestOtp = { requestOtpClicks += 1 },
                    )
                }
            }
        }

        composeRule.onNodeWithText("Get OTP").assertIsNotEnabled()

        composeRule.onNode(hasSetTextAction())
            .performTextInput("9876543210")

        composeRule.onNodeWithText("Get OTP").assertIsEnabled().performClick()

        assertEquals(1, requestOtpClicks)
    }
}
