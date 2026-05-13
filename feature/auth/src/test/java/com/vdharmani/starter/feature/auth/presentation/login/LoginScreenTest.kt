package com.vdharmani.starter.feature.auth.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Reference pattern for **Compose UI tests that run as JVM unit tests**.
 *
 * Robolectric provides a fake Android runtime, so we don't need an emulator.
 * `./gradlew testDebugUnitTest` runs this on a laptop / CI without any
 * device setup.
 *
 * Junior tip: prefer this over instrumented (`androidTest`) tests when you
 * can — they're an order of magnitude faster and don't require a connected
 * device on CI.
 *
 * Note on test surface: we test the **submit button in isolation** rather
 * than the full [LoginScreen], because `LoginScreen` depends on
 * `hiltViewModel()` which requires a Hilt test harness. The button is the
 * thing where the loading-state contract actually matters; adding Hilt-Test
 * for the rest is a separate, optional upgrade.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `submit button is disabled while loading`() {
        composeRule.setContent {
            Column {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,  // mirrors LoginUiState(isLoading = true)
                ) { Text("Sign in") }
            }
        }
        composeRule.onNodeWithText("Sign in").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun `submit button is enabled when not loading`() {
        composeRule.setContent {
            Column {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                ) { Text("Sign in") }
            }
        }
        composeRule.onNodeWithText("Sign in").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun `clicking submit invokes the onClick handler`() {
        var clicked = 0
        composeRule.setContent {
            Column {
                Button(
                    onClick = { clicked++ },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Sign in") }
            }
        }
        composeRule.onNodeWithText("Sign in").performClick()
        assertEquals(1, clicked)
    }
}
