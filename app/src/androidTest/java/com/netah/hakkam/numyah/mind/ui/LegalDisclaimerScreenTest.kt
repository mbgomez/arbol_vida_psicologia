package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.LegalDisclaimerScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LegalDisclaimerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun legalDisclaimerScreen_displaysCoreBoundaryContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                LegalDisclaimerScreen(
                    paddingValues = PaddingValues(),
                    onContinue = {}
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.legal_disclaimer_title)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.legal_disclaimer_boundary_title)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.legal_disclaimer_privacy_title)
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun legalDisclaimerScreen_continueReturnsCheckboxPreference() {
        var skipFuture = false

        composeTestRule.setContent {
            AppTheme {
                LegalDisclaimerScreen(
                    paddingValues = PaddingValues(),
                    onContinue = { skipFuture = it }
                )
            }
        }

        composeTestRule.onNodeWithTag("legal_disclaimer_scroll").performTouchInput { swipeUp() }
        composeTestRule.onNodeWithTag("legal_disclaimer_skip_checkbox").performClick()
        composeTestRule.onNodeWithTag("legal_disclaimer_continue_button").performClick()

        assertEquals(true, skipFuture)
    }
}
