package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.ui.screen.ResultsScreen
import com.netah.hakkam.numyah.mind.ui.screen.SephiraDetailScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.ActiveAssessmentUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsOverviewUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsSephiraUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsUiState
import com.netah.hakkam.numyah.mind.viewmodel.SephiraDetailUiModel
import com.netah.hakkam.numyah.mind.viewmodel.SephiraDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ResultsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun resultsScreen_loadedState_opensSephiraDetail() {
        var openedSephiraId: SephiraId? = null
        var retookAssessment = false
        var confirmedReplaceAssessment = false

        composeTestRule.setContent {
            AppTheme {
                ResultsScreen(
                    paddingValues = PaddingValues(),
                    uiState = ResultsUiState.Loaded(resultsModel()),
                    onPrimaryAction = {},
                    onRetakeAssessment = { retookAssessment = true },
                    onConfirmReplaceActiveAssessment = { confirmedReplaceAssessment = true },
                    primaryActionLabel = "Back home",
                    onOpenSephiraDetail = { openedSephiraId = it.sephiraId }
                )
            }
        }

        composeTestRule.onNodeWithText("Yesod").assertIsDisplayed()
        composeTestRule.onNodeWithText("Open full interpretation").performClick()
        composeTestRule.onNodeWithText("Retake assessment").performClick()
        composeTestRule.onNodeWithText("Start fresh reflection").performClick()

        assertEquals(SephiraId.YESOD, openedSephiraId)
        assertEquals(true, retookAssessment)
        assertEquals(true, confirmedReplaceAssessment)
    }

    @Test
    fun sephiraDetailScreen_loadedState_rendersInterpretationSections() {
        composeTestRule.setContent {
            AppTheme {
                SephiraDetailScreen(
                    paddingValues = PaddingValues(),
                    uiState = SephiraDetailUiState.Loaded(detailModel())
                )
            }
        }

        composeTestRule.onNodeWithText("Healthy expression").assertIsDisplayed()
        composeTestRule.onNodeWithText("Healthy relational grounding").assertIsDisplayed()
        composeTestRule.onNodeWithText("Suggested practices").assertIsDisplayed()
        composeTestRule.onNodeWithText("1. Practice quiet connection").assertIsDisplayed()
    }

    private fun resultsModel() = ResultsOverviewUiModel(
        title = "Tree of Life reflection",
        isHistoricalSession = false,
        activeAssessment = ActiveAssessmentUiModel(
            sephiraName = "Yesod",
            completedSephirotCount = 1,
            totalSephirotCount = 10,
            currentQuestionNumber = 2,
            totalQuestions = 6,
            isAtSectionStart = false
        ),
        completedCount = 2,
        totalCount = 4,
        mostBalanced = null,
        needsAttention = null,
        sephirot = listOf(
            ResultsSephiraUiModel(
                sephiraId = SephiraId.YESOD,
                sephiraName = "Yesod",
                dominantPole = Pole.DEFICIENCY,
                confidence = ConfidenceLevel.MEDIUM,
                isLowConfidence = false,
                balanceScore = 0.31,
                deficiencyScore = 0.46,
                excessScore = 0.23,
                balancePercent = 31,
                deficiencyPercent = 46,
                excessPercent = 23,
                imbalancePercent = 69
            )
        )
    )

    private fun detailModel() = SephiraDetailUiModel(
        sephiraId = SephiraId.YESOD,
        sephiraName = "Yesod",
        shortMeaning = "Relational foundation",
        dominantPole = Pole.DEFICIENCY,
        confidence = ConfidenceLevel.MEDIUM,
        isLowConfidence = false,
        balancePercent = 31,
        deficiencyPercent = 46,
        excessPercent = 23,
        healthyExpression = "Healthy relational grounding",
        deficiencyPattern = "Withdrawn or underconfident",
        excessPattern = "Overdependent on attention",
        suggestedPractices = listOf("Practice quiet connection"),
        isHistoricalSession = false
    )
}
