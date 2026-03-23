package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.ui.screen.HistoryTrendsScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.HistoryDeepTrendsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistorySessionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesChartUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesLineUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesPointUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendChartUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendDirection
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendMetricType
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendPointUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendScoreType
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendSephiraOptionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiState
import org.junit.Rule
import org.junit.Test

class HistoryTrendsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun historyTrendsScreen_supportsSephiraAndScoreTypeModes() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                HistoryTrendsScreen(
                    paddingValues = PaddingValues(),
                    uiState = HistoryUiState.Loaded(historyUiModel()),
                    onOpenAssessments = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("history_trends_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_trends_sessions_badge_label)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trends_mode_by_sephira").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trends_sephira_MALKUTH").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.history_trends_chart_by_sephira_title, "Malkuth")
        ).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.history_trends_legend_value, 64))
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("history_trends_mode_by_score_type").performClick()
        composeTestRule.onNodeWithTag("history_trends_score_type_EXCESS").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trends_score_type_EXCESS").performClick()
        composeTestRule.onNodeWithTag("history_trends_visibility_MALKUTH").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trends_visibility_MALKUTH").performClick()

        composeTestRule.onNodeWithText(
            context.getString(
                R.string.history_trends_chart_by_score_type_title,
                context.getString(R.string.assessment_score_excess)
            )
        ).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trends_session_details").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trends_value_2_EXCESS_YESOD").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_trends_value_2_EXCESS_MALKUTH").assertDoesNotExist()
    }

    private fun historyUiModel(): HistoryUiModel {
        return HistoryUiModel(
            questionnaireTitle = "Tree of Life reflection",
            totalSessions = 2,
            trends = HistoryTrendsUiModel(
                sessionCount = 2,
                hasComparisonData = true,
                charts = listOf(
                    HistoryTrendChartUiModel(
                        metric = HistoryTrendMetricType.HIGHEST_TENSION,
                        latestValue = 78,
                        latestSephiraName = "Yesod",
                        previousValue = 70,
                        direction = HistoryTrendDirection.UP,
                        points = listOf(
                            HistoryTrendPointUiModel(1L, 100L, 70, "Malkuth"),
                            HistoryTrendPointUiModel(2L, 200L, 78, "Yesod")
                        )
                    ),
                    HistoryTrendChartUiModel(
                        metric = HistoryTrendMetricType.MOST_SETTLED,
                        latestValue = 64,
                        latestSephiraName = "Malkuth",
                        previousValue = 60,
                        direction = HistoryTrendDirection.UP,
                        points = listOf(
                            HistoryTrendPointUiModel(1L, 100L, 60, "Malkuth"),
                            HistoryTrendPointUiModel(2L, 200L, 64, "Malkuth")
                        )
                    )
                )
            ),
            deeperTrends = HistoryDeepTrendsUiModel(
                sessionCount = 2,
                hasComparisonData = true,
                sephiraOptions = listOf(
                    HistoryTrendSephiraOptionUiModel(SephiraId.MALKUTH, "Malkuth"),
                    HistoryTrendSephiraOptionUiModel(SephiraId.YESOD, "Yesod")
                ),
                defaultSephiraId = SephiraId.MALKUTH,
                defaultScoreType = HistoryTrendScoreType.BALANCE,
                bySephiraCharts = mapOf(
                    SephiraId.MALKUTH to HistoryTimeSeriesChartUiModel(
                        sessionCount = 2,
                        lines = listOf(
                            HistoryTimeSeriesLineUiModel(
                                id = "BALANCE",
                                scoreType = HistoryTrendScoreType.BALANCE,
                                points = listOf(
                                    HistoryTimeSeriesPointUiModel(1L, 100L, 60),
                                    HistoryTimeSeriesPointUiModel(2L, 200L, 64)
                                )
                            ),
                            HistoryTimeSeriesLineUiModel(
                                id = "DEFICIENCY",
                                scoreType = HistoryTrendScoreType.DEFICIENCY,
                                points = listOf(
                                    HistoryTimeSeriesPointUiModel(1L, 100L, 25),
                                    HistoryTimeSeriesPointUiModel(2L, 200L, 20)
                                )
                            ),
                            HistoryTimeSeriesLineUiModel(
                                id = "EXCESS",
                                scoreType = HistoryTrendScoreType.EXCESS,
                                points = listOf(
                                    HistoryTimeSeriesPointUiModel(1L, 100L, 15),
                                    HistoryTimeSeriesPointUiModel(2L, 200L, 16)
                                )
                            )
                        )
                    ),
                    SephiraId.YESOD to HistoryTimeSeriesChartUiModel(
                        sessionCount = 2,
                        lines = listOf(
                            HistoryTimeSeriesLineUiModel(
                                id = "BALANCE_Y",
                                scoreType = HistoryTrendScoreType.BALANCE,
                                points = listOf(
                                    HistoryTimeSeriesPointUiModel(1L, 100L, 40),
                                    HistoryTimeSeriesPointUiModel(2L, 200L, 35)
                                )
                            ),
                            HistoryTimeSeriesLineUiModel(
                                id = "DEFICIENCY_Y",
                                scoreType = HistoryTrendScoreType.DEFICIENCY,
                                points = listOf(
                                    HistoryTimeSeriesPointUiModel(1L, 100L, 30),
                                    HistoryTimeSeriesPointUiModel(2L, 200L, 42)
                                )
                            ),
                            HistoryTimeSeriesLineUiModel(
                                id = "EXCESS_Y",
                                scoreType = HistoryTrendScoreType.EXCESS,
                                points = listOf(
                                    HistoryTimeSeriesPointUiModel(1L, 100L, 30),
                                    HistoryTimeSeriesPointUiModel(2L, 200L, 23)
                                )
                            )
                        )
                    )
                ),
                byScoreTypeCharts = mapOf(
                    HistoryTrendScoreType.BALANCE to scoreTypeChart(
                        HistoryTrendScoreType.BALANCE,
                        60,
                        64,
                        40,
                        35
                    ),
                    HistoryTrendScoreType.DEFICIENCY to scoreTypeChart(
                        HistoryTrendScoreType.DEFICIENCY,
                        25,
                        20,
                        30,
                        42
                    ),
                    HistoryTrendScoreType.EXCESS to scoreTypeChart(
                        HistoryTrendScoreType.EXCESS,
                        15,
                        16,
                        30,
                        23
                    )
                )
            ),
            sessions = listOf(
                HistorySessionUiModel(
                    sessionId = 2L,
                    startedAt = 150L,
                    completedAt = 200L,
                    completedCount = 2,
                    totalCount = 10,
                    needsAttentionSephiraName = "Yesod",
                    needsAttentionImbalancePercent = 65,
                    mostBalancedSephiraName = "Malkuth",
                    mostBalancedBalancePercent = 64
                )
            )
        )
    }

    private fun scoreTypeChart(
        scoreType: HistoryTrendScoreType,
        firstMalkuth: Int,
        secondMalkuth: Int,
        firstYesod: Int,
        secondYesod: Int
    ): HistoryTimeSeriesChartUiModel {
        return HistoryTimeSeriesChartUiModel(
            sessionCount = 2,
            lines = listOf(
                HistoryTimeSeriesLineUiModel(
                    id = "${scoreType.name}_MALKUTH",
                    sephiraId = SephiraId.MALKUTH,
                    displayName = "Malkuth",
                    points = listOf(
                        HistoryTimeSeriesPointUiModel(1L, 100L, firstMalkuth),
                        HistoryTimeSeriesPointUiModel(2L, 200L, secondMalkuth)
                    )
                ),
                HistoryTimeSeriesLineUiModel(
                    id = "${scoreType.name}_YESOD",
                    sephiraId = SephiraId.YESOD,
                    displayName = "Yesod",
                    points = listOf(
                        HistoryTimeSeriesPointUiModel(1L, 100L, firstYesod),
                        HistoryTimeSeriesPointUiModel(2L, 200L, secondYesod)
                    )
                )
            )
        )
    }
}
