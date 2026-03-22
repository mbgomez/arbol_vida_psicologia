package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.ui.screen.HomeScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.HomeFocusUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HomeSummaryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HomeUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysPrimaryContentAndActions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    paddingValues = PaddingValues(),
                    uiState = HomeUiState.Empty,
                    onStartAssessment = {},
                    onOpenResults = {},
                    onOpenHistory = {},
                    onOpenLearn = {},
                    onOpenSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.home_primary_cta))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule.onNodeWithText(context.getString(R.string.home_secondary_cta))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule.onNodeWithText(context.getString(R.string.home_summary_empty_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.home_card_history_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.home_card_learn_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.home_card_settings_title)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_primaryAndSecondaryActions_invokeCallbacks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var startedAssessment = false
        var openedResults = false

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    paddingValues = PaddingValues(),
                    uiState = HomeUiState.Empty,
                    onStartAssessment = { startedAssessment = true },
                    onOpenResults = { openedResults = true },
                    onOpenHistory = {},
                    onOpenLearn = {},
                    onOpenSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_primary_cta)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.home_secondary_cta)).performClick()

        assertTrue(startedAssessment)
        assertTrue(openedResults)
    }

    @Test
    fun homeScreen_loadedState_showsLatestReflectionSummary() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    paddingValues = PaddingValues(),
                    uiState = HomeUiState.Loaded(
                        HomeSummaryUiModel(
                            lastAssessmentDate = "Mar 22, 2026",
                            daysSinceLastAssessment = 2,
                            needsAttentionSephiraName = "Yesod",
                            mostBalancedSephiraName = "Malkuth",
                            currentFocus = HomeFocusUiModel(
                                sephiraName = "Yesod",
                                dominantPole = Pole.DEFICIENCY
                            )
                        )
                    ),
                    onStartAssessment = {},
                    onOpenResults = {},
                    onOpenHistory = {},
                    onOpenLearn = {},
                    onOpenSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_summary_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.home_summary_tension_title, "Yesod")
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.home_summary_body, "Mar 22, 2026", "2 days ago")
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.home_focus_deficiency, "Yesod")
        ).assertIsDisplayed()
    }
}
