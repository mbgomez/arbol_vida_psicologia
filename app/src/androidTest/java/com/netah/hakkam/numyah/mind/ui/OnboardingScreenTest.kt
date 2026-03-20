package com.netah.hakkam.numyah.mind.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.OnboardingScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.OnboardingUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingScreen_firstPage_displaysCoreContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                OnboardingScreen(
                    uiState = OnboardingUiState(),
                    onBack = {},
                    onContinue = {},
                    onSkip = {},
                    onPageChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_eyebrow)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_page_1_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_skip)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_continue)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.onboarding_primary_visual_title)
        ).assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_continueAndBack_updateVisiblePage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            var uiState by remember { mutableStateOf(OnboardingUiState()) }
            AppTheme {
                OnboardingScreen(
                    uiState = uiState,
                    onBack = {
                        uiState = uiState.copy(currentPage = (uiState.currentPage - 1).coerceAtLeast(0))
                    },
                    onContinue = {
                        uiState = uiState.copy(
                            currentPage = (uiState.currentPage + 1).coerceAtMost(uiState.pageCount - 1)
                        )
                    },
                    onSkip = {},
                    onPageChanged = { page -> uiState = uiState.copy(currentPage = page) }
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_continue))
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_page_2_title)).assertIsDisplayed()

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_back)).performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_page_1_title)).assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_skipInvokesCallback() {
        var skipped = false
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                OnboardingScreen(
                    uiState = OnboardingUiState(),
                    onBack = {},
                    onContinue = {},
                    onSkip = { skipped = true },
                    onPageChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_skip)).performClick()

        assertTrue(skipped)
    }
}
