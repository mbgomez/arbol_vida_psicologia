package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.ui.screen.AssessmentCompletedState
import com.netah.hakkam.numyah.mind.ui.screen.AssessmentErrorState
import com.netah.hakkam.numyah.mind.ui.screen.AssessmentLoadingState
import com.netah.hakkam.numyah.mind.ui.screen.AssessmentScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentAnswerOptionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentCompletedUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentErrorType
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentHonestyNoticeUiModel
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
    fun assessmentScreen_loadingState_displaysProgressIndicator() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Loading,
                    onStart = {},
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = {},
                    onBack = {},
                    onRetry = {},
                    onBackHome = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.progress_indicator_desccription)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_loading_title)
        ).assertIsDisplayed()
    }

    @Test
    fun assessmentLoadingState_directComposable_displaysSupportCopy() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                AssessmentLoadingState()
            }
        }

        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.progress_indicator_desccription)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_loading_support)
        ).assertIsDisplayed()
    }

    @Test
    fun assessmentScreen_honestyNoticeState_updatesPreferenceAndContinues() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var doNotShowAgain = false
        var continued = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.HonestyNotice(
                        AssessmentHonestyNoticeUiModel(isDoNotShowAgainChecked = false)
                    ),
                    onStart = {},
                    onContinueFromHonestyNotice = { continued = true },
                    onHonestyPreferenceChanged = { doNotShowAgain = it },
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = {},
                    onBack = {},
                    onRetry = {},
                    onBackHome = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.assessment_honesty_title)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("assessment_honesty_checkbox").performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_honesty_continue)).performClick()

        assertTrue(doNotShowAgain)
        assertTrue(continued)
    }

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
                            sephiraId = SephiraId.MALKUTH,
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
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = {},
                    onBack = {},
                    onRetry = {},
                    onBackHome = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Malkuth", useUnmergedTree = true)[0].assertIsDisplayed()
        composeTestRule.onNodeWithText("Intro body").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_start))
            .assertHasClickAction()
            .performClick()

        assertTrue(started)
    }

    @Test
    fun assessmentScreen_introState_resumeMode_displaysResumeAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Intro(
                        AssessmentIntroUiModel(
                            questionnaireTitle = "Malkuth reflection",
                            sephiraId = SephiraId.MALKUTH,
                            sephiraName = "Malkuth",
                            shortMeaning = "Meaning",
                            introText = "Intro body",
                            isResumeSession = true,
                            progress = AssessmentProgressUiModel(
                                currentPageIndex = 1,
                                totalPages = 2,
                                currentQuestionNumber = 0,
                                totalQuestions = 6,
                                overallProgress = 0f
                            )
                        )
                    ),
                    onStart = {},
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = {},
                    onBack = {},
                    onRetry = {},
                    onBackHome = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText(
            context.getString(R.string.assessment_resume),
            useUnmergedTree = true
        )[0].assertIsDisplayed()
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
                            sephiraId = SephiraId.MALKUTH,
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
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = { selectedAnswer = it },
                    onContinue = { continued = true },
                    onContinueFromCompleted = {},
                    onBack = { wentBack = true },
                    onRetry = {},
                    onBackHome = {}
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
    fun assessmentScreen_questionState_withoutSelection_disablesContinue() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Question(
                        AssessmentQuestionUiModel(
                            questionnaireTitle = "Malkuth reflection",
                            sephiraId = SephiraId.MALKUTH,
                            sephiraName = "Malkuth",
                            currentPageTitle = "Money",
                            currentPageDescription = "Resources",
                            currentQuestionPrompt = "I appreciate what I have.",
                            answerOptions = listOf(
                                AssessmentAnswerOptionUiModel("disagree", "Disagree", 1, false),
                                AssessmentAnswerOptionUiModel("agree", "Agree", 3, false)
                            ),
                            selectedOptionId = null,
                            progress = AssessmentProgressUiModel(
                                currentPageIndex = 0,
                                totalPages = 2,
                                currentQuestionNumber = 1,
                                totalQuestions = 6,
                                overallProgress = 1f / 6f
                            ),
                            navigation = AssessmentNavigationUiModel(
                                canGoBack = false,
                                canContinue = false,
                                isFirstQuestion = true,
                                isLastQuestion = false
                            )
                        )
                    ),
                    onStart = {},
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = {},
                    onBack = {},
                    onRetry = {},
                    onBackHome = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.assessment_continue)).assertIsNotEnabled()
    }

    @Test
    fun assessmentScreen_completedState_showsSoftenedResult() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var returnedHome = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Completed(
                        AssessmentCompletedUiModel(
                            sephiraId = SephiraId.MALKUTH,
                            sephiraName = "Malkuth",
                            sectionSummary = "Grounded contact with practical life.",
                            completionReflection = "Material life may be asking for more support.",
                            practiceSuggestion = "Choose one steady act of care this week.",
                            dominantPole = Pole.DEFICIENCY,
                            confidence = ConfidenceLevel.LOW,
                            balanceScore = 0.22,
                            deficiencyScore = 0.51,
                            excessScore = 0.27,
                            isLowConfidence = true,
                            hasNextSephira = false,
                            nextSephiraName = null
                        )
                    ),
                    onStart = {},
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = {},
                    onBack = {},
                    onRetry = {},
                    onBackHome = { returnedHome = true }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_result_title, "Malkuth")).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_result_leans_deficiency)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_confidence_low)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_result_what_it_means_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Grounded contact with practical life.").performScrollTo().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Material life may be asking for more support.")
            .assertCountEquals(1)
        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_confidence_note_low)
        ).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose one steady act of care this week.").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("22%", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("51%", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("27%", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_result_next_step_title)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_result_home_action)).performScrollTo().performClick()

        assertTrue(returnedHome)
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
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = {},
                    onBack = {},
                    onRetry = { retried = true },
                    onBackHome = {}
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_error_load_title)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_error_load)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.assessment_retry)).performClick()

        assertTrue(retried)
    }

    @Test
    fun assessmentScreen_completedState_withNextSephira_usesContinueAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var continued = false
        var returnedHome = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentScreen(
                    paddingValues = PaddingValues(),
                    uiState = AssessmentUiState.Completed(
                        AssessmentCompletedUiModel(
                            sephiraId = SephiraId.MALKUTH,
                            sephiraName = "Malkuth",
                            sectionSummary = "Grounded contact with practical life.",
                            completionReflection = "Malkuth feels steady and supported right now.",
                            practiceSuggestion = "Choose one steady act of care this week.",
                            dominantPole = Pole.BALANCE,
                            confidence = ConfidenceLevel.MEDIUM,
                            balanceScore = 0.55,
                            deficiencyScore = 0.20,
                            excessScore = 0.25,
                            isLowConfidence = false,
                            hasNextSephira = true,
                            nextSephiraName = "Yesod"
                        )
                    ),
                    onStart = {},
                    onContinueFromHonestyNotice = {},
                    onHonestyPreferenceChanged = {},
                    onSelectAnswer = {},
                    onContinue = {},
                    onContinueFromCompleted = { continued = true },
                    onBack = {},
                    onRetry = {},
                    onBackHome = { returnedHome = true }
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_result_continue_action, "Yesod")
        ).performScrollTo().performClick()

        assertTrue(continued)
        assertEquals(false, returnedHome)
    }

    @Test
    fun assessmentCompletedState_withoutPracticeSuggestion_showsFallbackText() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                AssessmentCompletedState(
                    model = AssessmentCompletedUiModel(
                        sephiraId = SephiraId.MALKUTH,
                        sephiraName = "Malkuth",
                        sectionSummary = "Grounded contact with practical life.",
                        completionReflection = "Malkuth feels steady and supported right now.",
                        practiceSuggestion = null,
                        dominantPole = Pole.BALANCE,
                        confidence = ConfidenceLevel.MEDIUM,
                        balanceScore = 0.55,
                        deficiencyScore = 0.20,
                        excessScore = 0.25,
                        isLowConfidence = false,
                        hasNextSephira = false,
                        nextSephiraName = null
                    ),
                    onContinue = {},
                    onBackHome = {}
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_result_next_step_fallback)
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun assessmentErrorState_saveAnswer_displaysMappedCopyAndRetries() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var retried = false

        composeTestRule.setContent {
            AppTheme {
                AssessmentErrorState(
                    errorType = AssessmentErrorType.SAVE_ANSWER,
                    onRetry = { retried = true }
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_error_save_title)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_error_save)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_error_save_support)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.assessment_retry)
        ).performClick()

        assertTrue(retried)
    }
}
