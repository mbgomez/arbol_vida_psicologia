package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.BuildConfig
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.SettingsAboutScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class SettingsAboutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsAboutScreen_displaysCoreAboutContentAndVersion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                SettingsAboutScreen(paddingValues = PaddingValues())
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_about_screen_title)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_about_purpose_title)
        ).assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeUp() }

        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_about_version_body, BuildConfig.VERSION_NAME)
        ).assertIsDisplayed()
    }
}
