package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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
                    onOpenAssessment = { openedAssessment = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Tree of Life reflection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resume reflection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resume reflection").performClick()

        assertTrue(openedAssessment)
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
                    onOpenAssessment = {}
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_library_footer)
        ).assertIsDisplayed()
    }
}
