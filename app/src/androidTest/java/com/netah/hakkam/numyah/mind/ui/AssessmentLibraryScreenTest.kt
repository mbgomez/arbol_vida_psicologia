package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.AssessmentLibraryScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.ActiveAssessmentUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryEntryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AssessmentLibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun assessmentLibrary_loadedState_showsResumeEntryAndAction() {
        var openedAssessment = false
        var startedFreshAssessment = false
        var confirmedStartFresh = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentLibraryScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentLibraryUiState.Loaded(
                        AssessmentLibraryUiModel(
                            entry = AssessmentLibraryEntryUiModel(
                                title = "Tree of Life reflection",
                                sephiraCount = 10,
                                activeAssessment = ActiveAssessmentUiModel(
                                    sephiraName = "Yesod",
                                    completedSephirotCount = 1,
                                    totalSephirotCount = 10,
                                    currentQuestionNumber = 2,
                                    totalQuestions = 6,
                                    isAtSectionStart = false
                                )
                            )
                        )
                    ),
                    onRetry = {},
                    onOpenAssessment = { openedAssessment = it },
                    onStartFreshAssessment = { startedFreshAssessment = true },
                    onConfirmStartFreshAssessment = { confirmedStartFresh = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Tree of Life reflection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resume reflection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resume reflection").performClick()
        composeTestRule.onNodeWithText("Start new reflection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start new reflection").performClick()
        composeTestRule.onNodeWithText("Start fresh reflection").performClick()

        assertTrue(openedAssessment)
        assertTrue(startedFreshAssessment)
        assertTrue(confirmedStartFresh)
    }

    @Test
    fun assessmentLibrary_loadedState_showsUserFacingFooterCopy() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                AssessmentLibraryScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentLibraryUiState.Loaded(
                        AssessmentLibraryUiModel(
                            entry = AssessmentLibraryEntryUiModel(
                                title = "Tree of Life reflection",
                                sephiraCount = 10,
                                activeAssessment = null
                            )
                        )
                    ),
                    onRetry = {},
                    onOpenAssessment = {},
                    onStartFreshAssessment = {},
                    onConfirmStartFreshAssessment = {}
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_library_footer)
        ).assertIsDisplayed()
    }

    @Test
    fun assessmentLibrary_errorState_showsRetryAndDirectOpenAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var retried = false
        var openedAssessment = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentLibraryScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentLibraryUiState.Error,
                    onRetry = { retried = true },
                    onOpenAssessment = { openedAssessment = true },
                    onStartFreshAssessment = {},
                    onConfirmStartFreshAssessment = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("assessment_library_error_card").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_library_retry_action))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_library_error_secondary_action))
            .performClick()

        assertTrue(retried)
        assertTrue(openedAssessment)
    }

    @Test
    fun assessmentLibrary_loadingState_showsProgressIndicator() {
        composeTestRule.setContent {
            AppTheme {
                AssessmentLibraryScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentLibraryUiState.Loading,
                    onRetry = {},
                    onOpenAssessment = {},
                    onStartFreshAssessment = {},
                    onConfirmStartFreshAssessment = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("assessment_library_loading_indicator").assertIsDisplayed()
    }
}
