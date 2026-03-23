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
import com.netah.hakkam.numyah.mind.viewmodel.HomeActiveAssessmentUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HomeUiModel
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
                    onStartFreshAssessment = {},
                    onResumeAssessment = {},
                    onOpenLatestResults = {},
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
                    uiState = HomeUiState.Loaded(
                        HomeUiModel(
                            activeAssessment = null,
                            latestReflection = HomeSummaryUiModel(
                                lastAssessmentDate = "Mar 22, 2026",
                                daysSinceLastAssessment = 2,
                                needsAttentionSephiraName = "Yesod",
                                mostBalancedSephiraName = "Malkuth",
                                currentFocus = HomeFocusUiModel(
                                    sephiraName = "Yesod",
                                    dominantPole = Pole.DEFICIENCY
                                )
                            )
                        )
                    ),
                    onStartAssessment = { startedAssessment = true },
                    onStartFreshAssessment = {},
                    onResumeAssessment = {},
                    onOpenLatestResults = { openedResults = true },
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
                        HomeUiModel(
                            activeAssessment = null,
                            latestReflection = HomeSummaryUiModel(
                                lastAssessmentDate = "Mar 22, 2026",
                                daysSinceLastAssessment = 2,
                                needsAttentionSephiraName = "Yesod",
                                mostBalancedSephiraName = "Malkuth",
                                currentFocus = HomeFocusUiModel(
                                    sephiraName = "Yesod",
                                    dominantPole = Pole.DEFICIENCY
                                )
                            )
                        )
                    ),
                    onStartAssessment = {},
                    onStartFreshAssessment = {},
                    onResumeAssessment = {},
                    onOpenLatestResults = {},
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

    @Test
    fun homeScreen_activeAssessment_showsResumeContentAndAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var resumedAssessment = false
        var startedFresh = false

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    paddingValues = PaddingValues(),
                    uiState = HomeUiState.Loaded(
                        HomeUiModel(
                            activeAssessment = HomeActiveAssessmentUiModel(
                                sephiraName = "Yesod",
                                completedSephirotCount = 1,
                                totalSephirotCount = 10,
                                currentQuestionNumber = 2,
                                totalQuestions = 6,
                                isAtSectionStart = false
                            ),
                            latestReflection = null
                        )
                    ),
                    onStartAssessment = {},
                    onStartFreshAssessment = { startedFresh = true },
                    onResumeAssessment = { resumedAssessment = true },
                    onOpenLatestResults = {},
                    onOpenHistory = {},
                    onOpenLearn = {},
                    onOpenSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_active_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.home_active_question_body, "Yesod", 2, 6, 1, 10)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.home_resume_cta)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.home_start_fresh_cta)).performClick()
        composeTestRule.onNodeWithText(
            context.getString(R.string.replace_assessment_dialog_confirm)
        ).performClick()

        assertTrue(resumedAssessment)
        assertTrue(startedFresh)
    }
}
