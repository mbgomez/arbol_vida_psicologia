package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.SettingsPrivacyScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class SettingsPrivacyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsPrivacyScreen_displaysCorePrivacyContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                SettingsPrivacyScreen(paddingValues = PaddingValues())
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_privacy_screen_title)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_privacy_local_title)
        ).assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeUp() }

        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_privacy_entry_note_title)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_privacy_boundary_title)
        ).assertIsDisplayed()
    }
}
