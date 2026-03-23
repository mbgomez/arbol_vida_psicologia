package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.HistoryScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.HistorySessionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryDeepTrendsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesChartUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesLineUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesPointUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendChartUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendDirection
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendScoreType
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendSephiraOptionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendMetricType
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendPointUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiState
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
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
                    onOpenAssessments = { startedAssessment = true },
                    onOpenTrends = {}
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
        var openedTrends = false

        composeTestRule.setContent {
            AppTheme {
                HistoryScreen(
                    paddingValues = PaddingValues(),
                    uiState = HistoryUiState.Loaded(
                        HistoryUiModel(
                            questionnaireTitle = "Tree of Life reflection",
                            totalSessions = 1,
                            trends = HistoryTrendsUiModel(
                                sessionCount = 1,
                                hasComparisonData = false,
                                charts = listOf(
                                    HistoryTrendChartUiModel(
                                        metric = HistoryTrendMetricType.HIGHEST_TENSION,
                                        latestValue = 92,
                                        latestSephiraName = "Hod",
                                        previousValue = null,
                                        direction = HistoryTrendDirection.INSUFFICIENT_DATA,
                                        points = listOf(
                                            HistoryTrendPointUiModel(
                                                sessionId = 7L,
                                                completedAt = 200L,
                                                value = 92,
                                                sephiraName = "Hod"
                                            )
                                        )
                                    ),
                                    HistoryTrendChartUiModel(
                                        metric = HistoryTrendMetricType.MOST_SETTLED,
                                        latestValue = 61,
                                        latestSephiraName = "Malkuth",
                                        previousValue = null,
                                        direction = HistoryTrendDirection.INSUFFICIENT_DATA,
                                        points = listOf(
                                            HistoryTrendPointUiModel(
                                                sessionId = 7L,
                                                completedAt = 200L,
                                                value = 61,
                                                sephiraName = "Malkuth"
                                            )
                                        )
                                    )
                                )
                            ),
                            deeperTrends = HistoryDeepTrendsUiModel(
                                sessionCount = 1,
                                hasComparisonData = false,
                                sephiraOptions = listOf(
                                    HistoryTrendSephiraOptionUiModel(
                                        sephiraId = SephiraId.MALKUTH,
                                        displayName = "Malkuth"
                                    )
                                ),
                                defaultSephiraId = SephiraId.MALKUTH,
                                defaultScoreType = HistoryTrendScoreType.BALANCE,
                                bySephiraCharts = mapOf(
                                    SephiraId.MALKUTH to HistoryTimeSeriesChartUiModel(
                                        sessionCount = 1,
                                        lines = HistoryTrendScoreType.values().map { scoreType ->
                                            HistoryTimeSeriesLineUiModel(
                                                id = scoreType.name,
                                                scoreType = scoreType,
                                                points = listOf(
                                                    HistoryTimeSeriesPointUiModel(
                                                        sessionId = 7L,
                                                        completedAt = 200L,
                                                        value = 61
                                                    )
                                                )
                                            )
                                        }
                                    )
                                ),
                                byScoreTypeCharts = HistoryTrendScoreType.values().associateWith { scoreType ->
                                    HistoryTimeSeriesChartUiModel(
                                        sessionCount = 1,
                                        lines = listOf(
                                            HistoryTimeSeriesLineUiModel(
                                                id = "MALKUTH_${scoreType.name}",
                                                sephiraId = SephiraId.MALKUTH,
                                                displayName = "Malkuth",
                                                points = listOf(
                                                    HistoryTimeSeriesPointUiModel(
                                                        sessionId = 7L,
                                                        completedAt = 200L,
                                                        value = 61
                                                    )
                                                )
                                            )
                                        )
                                    )
                                }
                            ),
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
                    onOpenAssessments = {},
                    onOpenTrends = { openedTrends = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("history_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trend_section").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_list_title)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_trends_title)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_needs_attention_summary, "Hod")).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_open_trends").performScrollTo().performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.history_open_action)).performClick()

        assertTrue(openedTrends)
        assertEquals(7L, openedSessionId)
    }
}
