package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.ui.screen.AssessmentScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentAnswerOptionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentCompletedUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentErrorType
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentIntroUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentNavigationUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentProgressUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentQuestionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AssessmentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun assessmentScreen_introState_displaysContentAndStarts() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var started = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Intro(
                        AssessmentIntroUiModel(
                            questionnaireTitle = "Malkuth reflection",
                            sephiraName = "Malkuth",
                            shortMeaning = "Meaning",
                            introText = "Intro body",
                            isResumeSession = false,
                            progress = AssessmentProgressUiModel(
                                currentPageIndex = 0,
                                totalPages = 2,
                                currentQuestionNumber = 0,
                                totalQuestions = 6,
                                overallProgress = 0f
                            )
                        )
                    ),
                    onStart = { started = true },
                    onSelectAnswer = {},
                    onContinue = {},
                    onBack = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.assessment_intro_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Intro body").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_start))
            .assertHasClickAction()
            .performClick()

        assertTrue(started)
    }

    @Test
    fun assessmentScreen_questionState_selectsAndContinues() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var selectedAnswer: String? = null
        var continued = false
        var wentBack = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Question(
                        AssessmentQuestionUiModel(
                            questionnaireTitle = "Malkuth reflection",
                            sephiraName = "Malkuth",
                            currentPageTitle = "Money",
                            currentPageDescription = "Resources",
                            currentQuestionPrompt = "I appreciate what I have.",
                            answerOptions = listOf(
                                AssessmentAnswerOptionUiModel("disagree", "Disagree", 1, false),
                                AssessmentAnswerOptionUiModel("agree", "Agree", 3, true)
                            ),
                            selectedOptionId = "agree",
                            progress = AssessmentProgressUiModel(
                                currentPageIndex = 0,
                                totalPages = 2,
                                currentQuestionNumber = 1,
                                totalQuestions = 6,
                                overallProgress = 1f / 6f
                            ),
                            navigation = AssessmentNavigationUiModel(
                                canGoBack = true,
                                canContinue = true,
                                isFirstQuestion = true,
                                isLastQuestion = false
                            )
                        )
                    ),
                    onStart = {},
                    onSelectAnswer = { selectedAnswer = it },
                    onContinue = { continued = true },
                    onBack = { wentBack = true },
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("I appreciate what I have.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Disagree").performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_continue)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_back)).performClick()

        assertEquals("disagree", selectedAnswer)
        assertTrue(continued)
        assertTrue(wentBack)
    }

    @Test
    fun assessmentScreen_completedState_showsSoftenedResult() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Completed(
                        AssessmentCompletedUiModel(
                            sephiraName = "Malkuth",
                            dominantPole = Pole.DEFICIENCY,
                            confidence = ConfidenceLevel.LOW,
                            balanceScore = 0.22,
                            deficiencyScore = 0.51,
                            excessScore = 0.27,
                            isLowConfidence = true
                        )
                    ),
                    onStart = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onBack = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.assessment_result_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_result_leans_deficiency)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_confidence_low)).assertIsDisplayed()
    }

    @Test
    fun assessmentScreen_errorState_retries() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var retried = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Error(AssessmentErrorType.LOAD),
                    onStart = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onBack = {},
                    onRetry = { retried = true }
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.assessment_error_load)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_retry)).performClick()

        assertTrue(retried)
    }
}
