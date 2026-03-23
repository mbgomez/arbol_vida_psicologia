package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.HistoryScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.HistorySessionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun historyScreen_emptyState_showsStartAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var startedAssessment = false

        composeTestRule.setContent {
            AppTheme {
                HistoryScreen(
                    paddingValues = PaddingValues(),
                    uiState = HistoryUiState.Empty,
                    onOpenAssessment = {},
                    onOpenAssessments = { startedAssessment = true }
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.history_empty_title)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_primary_action").performClick()

        assertTrue(startedAssessment)
    }

    @Test
    fun historyScreen_loadedState_showsSavedSessionAndOpensIt() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var openedSessionId: Long? = null

        composeTestRule.setContent {
            AppTheme {
                HistoryScreen(
                    paddingValues = PaddingValues(),
                    uiState = HistoryUiState.Loaded(
                        HistoryUiModel(
                            questionnaireTitle = "Tree of Life reflection",
                            totalSessions = 1,
                            sessions = listOf(
                                HistorySessionUiModel(
                                    sessionId = 7L,
                                    startedAt = 100L,
                                    completedAt = 200L,
                                    completedCount = 3,
                                    totalCount = 10,
                                    needsAttentionSephiraName = "Hod",
                                    needsAttentionImbalancePercent = 92,
                                    mostBalancedSephiraName = "Malkuth",
                                    mostBalancedBalancePercent = 61
                                )
                            )
                        )
                    ),
                    onOpenAssessment = { openedSessionId = it },
                    onOpenAssessments = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("history_list").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_list_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_needs_attention_summary, "Hod")).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_open_action)).performClick()

        assertEquals(7L, openedSessionId)
    }
}
