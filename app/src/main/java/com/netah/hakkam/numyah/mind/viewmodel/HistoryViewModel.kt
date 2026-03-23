package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveAssessmentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object Empty : HistoryUiState
    data class Loaded(val model: HistoryUiModel) : HistoryUiState
    data object Error : HistoryUiState
}

data class HistoryUiModel(
    val questionnaireTitle: String,
    val totalSessions: Int,
    val trends: HistoryTrendsUiModel,
    val deeperTrends: HistoryDeepTrendsUiModel,
    val sessions: List<HistorySessionUiModel>
)

data class HistoryTrendsUiModel(
    val sessionCount: Int,
    val hasComparisonData: Boolean,
    val charts: List<HistoryTrendChartUiModel>
)

data class HistoryDeepTrendsUiModel(
    val sessionCount: Int,
    val hasComparisonData: Boolean,
    val sephiraOptions: List<HistoryTrendSephiraOptionUiModel>,
    val defaultSephiraId: com.netah.hakkam.numyah.mind.domain.model.SephiraId?,
    val defaultScoreType: HistoryTrendScoreType,
    val bySephiraCharts: Map<com.netah.hakkam.numyah.mind.domain.model.SephiraId, HistoryTimeSeriesChartUiModel>,
    val byScoreTypeCharts: Map<HistoryTrendScoreType, HistoryTimeSeriesChartUiModel>
)

enum class HistoryTrendExploreMode {
    BY_SEPHIRA,
    BY_SCORE_TYPE
}

enum class HistoryTrendScoreType {
    BALANCE,
    DEFICIENCY,
    EXCESS
}

data class HistoryTrendSephiraOptionUiModel(
    val sephiraId: com.netah.hakkam.numyah.mind.domain.model.SephiraId,
    val displayName: String
)

data class HistoryTimeSeriesChartUiModel(
    val sessionCount: Int,
    val lines: List<HistoryTimeSeriesLineUiModel>
)

data class HistoryTimeSeriesLineUiModel(
    val id: String,
    val scoreType: HistoryTrendScoreType? = null,
    val sephiraId: com.netah.hakkam.numyah.mind.domain.model.SephiraId? = null,
    val displayName: String? = null,
    val points: List<HistoryTimeSeriesPointUiModel>
)

data class HistoryTimeSeriesPointUiModel(
    val sessionId: Long,
    val completedAt: Long,
    val value: Int?
)

enum class HistoryTrendMetricType {
    HIGHEST_TENSION,
    MOST_SETTLED
}

enum class HistoryTrendDirection {
    UP,
    DOWN,
    STEADY,
    INSUFFICIENT_DATA
}

data class HistoryTrendChartUiModel(
    val metric: HistoryTrendMetricType,
    val latestValue: Int,
    val latestSephiraName: String?,
    val previousValue: Int?,
    val direction: HistoryTrendDirection,
    val points: List<HistoryTrendPointUiModel>
)

data class HistoryTrendPointUiModel(
    val sessionId: Long,
    val completedAt: Long,
    val value: Int,
    val sephiraName: String?
)

data class HistorySessionUiModel(
    val sessionId: Long,
    val startedAt: Long,
    val completedAt: Long,
    val completedCount: Int,
    val totalCount: Int,
    val needsAttentionSephiraName: String?,
    val needsAttentionImbalancePercent: Int?,
    val mostBalancedSephiraName: String?,
    val mostBalancedBalancePercent: Int?
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val observeAssessmentHistoryUseCase: ObserveAssessmentHistoryUseCase,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                combine(
                    getCurrentQuestionnaireUseCase.run(currentLocaleProvider.current()),
                    observeAssessmentHistoryUseCase.run()
                ) { questionnaire, history ->
                    questionnaire to history
                }.collect { (questionnaire, history) ->
                    _uiState.value = if (history.isEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Loaded(buildModel(questionnaire, history))
                    }
                }
            } catch (_: Throwable) {
                _uiState.value = HistoryUiState.Error
            }
        }
    }

    private fun buildModel(
        questionnaire: QuestionnaireContent,
        history: List<AssessmentSessionSnapshot>
    ): HistoryUiModel {
        val sectionNames = questionnaire.sections.associate { it.sephiraId to it.displayName }
        val highlightsBySessionId = history.associate { snapshot ->
            snapshot.sessionId to snapshot.highlights(sectionNames)
        }
        val sessions = history.map { snapshot ->
            val highlights = highlightsBySessionId.getValue(snapshot.sessionId)

            HistorySessionUiModel(
                sessionId = snapshot.sessionId,
                startedAt = snapshot.startedAt,
                completedAt = snapshot.completedAt ?: snapshot.startedAt,
                completedCount = snapshot.scores.size,
                totalCount = questionnaire.sections.size,
                needsAttentionSephiraName = highlights.needsAttention?.sephiraName,
                needsAttentionImbalancePercent = highlights.needsAttention?.imbalancePercent,
                mostBalancedSephiraName = highlights.mostSettled?.sephiraName,
                mostBalancedBalancePercent = highlights.mostSettled?.balancePercent
            )
        }

        return HistoryUiModel(
            questionnaireTitle = questionnaire.title,
            totalSessions = sessions.size,
            trends = buildTrendModel(history, highlightsBySessionId),
            deeperTrends = buildDeepTrendModel(
                questionnaire = questionnaire,
                history = history
            ),
            sessions = sessions
        )
    }

    private fun buildTrendModel(
        history: List<AssessmentSessionSnapshot>,
        highlightsBySessionId: Map<Long, HistorySessionHighlights>
    ): HistoryTrendsUiModel {
        val chronologicalSessions = history.sortedBy { it.completedAt ?: it.startedAt }
        val tensionPoints = chronologicalSessions.map { snapshot ->
            val point = highlightsBySessionId[snapshot.sessionId]?.needsAttention
            HistoryTrendPointUiModel(
                sessionId = snapshot.sessionId,
                completedAt = snapshot.completedAt ?: snapshot.startedAt,
                value = point?.imbalancePercent ?: 0,
                sephiraName = point?.sephiraName
            )
        }
        val settledPoints = chronologicalSessions.map { snapshot ->
            val point = highlightsBySessionId[snapshot.sessionId]?.mostSettled
            HistoryTrendPointUiModel(
                sessionId = snapshot.sessionId,
                completedAt = snapshot.completedAt ?: snapshot.startedAt,
                value = point?.balancePercent ?: 0,
                sephiraName = point?.sephiraName
            )
        }

        return HistoryTrendsUiModel(
            sessionCount = chronologicalSessions.size,
            hasComparisonData = chronologicalSessions.size > 1,
            charts = listOf(
                buildTrendChart(
                    metric = HistoryTrendMetricType.HIGHEST_TENSION,
                    points = tensionPoints
                ),
                buildTrendChart(
                    metric = HistoryTrendMetricType.MOST_SETTLED,
                    points = settledPoints
                )
            )
        )
    }

    private fun buildTrendChart(
        metric: HistoryTrendMetricType,
        points: List<HistoryTrendPointUiModel>
    ): HistoryTrendChartUiModel {
        val latestPoint = points.last()
        val previousPoint = points.dropLast(1).lastOrNull()
        return HistoryTrendChartUiModel(
            metric = metric,
            latestValue = latestPoint.value,
            latestSephiraName = latestPoint.sephiraName,
            previousValue = previousPoint?.value,
            direction = trendDirection(
                latestValue = latestPoint.value,
                previousValue = previousPoint?.value
            ),
            points = points
        )
    }

    private fun trendDirection(
        latestValue: Int,
        previousValue: Int?
    ): HistoryTrendDirection {
        if (previousValue == null) return HistoryTrendDirection.INSUFFICIENT_DATA
        val delta = latestValue - previousValue
        return when {
            abs(delta) < TREND_STEADY_THRESHOLD -> HistoryTrendDirection.STEADY
            delta > 0 -> HistoryTrendDirection.UP
            else -> HistoryTrendDirection.DOWN
        }
    }

    private fun buildDeepTrendModel(
        questionnaire: QuestionnaireContent,
        history: List<AssessmentSessionSnapshot>
    ): HistoryDeepTrendsUiModel {
        val chronologicalSessions = history.sortedBy { it.completedAt ?: it.startedAt }
        val sephiraOptions = questionnaire.sections.map { section ->
            HistoryTrendSephiraOptionUiModel(
                sephiraId = section.sephiraId,
                displayName = section.displayName
            )
        }

        return HistoryDeepTrendsUiModel(
            sessionCount = chronologicalSessions.size,
            hasComparisonData = chronologicalSessions.size > 1,
            sephiraOptions = sephiraOptions,
            defaultSephiraId = sephiraOptions.firstOrNull()?.sephiraId,
            defaultScoreType = HistoryTrendScoreType.BALANCE,
            bySephiraCharts = sephiraOptions.associate { option ->
                option.sephiraId to buildBySephiraChart(
                    sessions = chronologicalSessions,
                    option = option
                )
            },
            byScoreTypeCharts = HistoryTrendScoreType.values().associateWith { scoreType ->
                buildByScoreTypeChart(
                    sessions = chronologicalSessions,
                    scoreType = scoreType,
                    sephiraOptions = sephiraOptions
                )
            }
        )
    }

    private fun buildBySephiraChart(
        sessions: List<AssessmentSessionSnapshot>,
        option: HistoryTrendSephiraOptionUiModel
    ): HistoryTimeSeriesChartUiModel {
        return HistoryTimeSeriesChartUiModel(
            sessionCount = sessions.size,
            lines = HistoryTrendScoreType.values().map { scoreType ->
                HistoryTimeSeriesLineUiModel(
                    id = "${option.sephiraId.name}_${scoreType.name}",
                    scoreType = scoreType,
                    points = sessions.map { session ->
                        val score = session.scoreFor(option.sephiraId)
                        HistoryTimeSeriesPointUiModel(
                            sessionId = session.sessionId,
                            completedAt = session.completedAt ?: session.startedAt,
                            value = score?.let { scorePercent(score.valueFor(scoreType)) }
                        )
                    }
                )
            }
        )
    }

    private fun buildByScoreTypeChart(
        sessions: List<AssessmentSessionSnapshot>,
        scoreType: HistoryTrendScoreType,
        sephiraOptions: List<HistoryTrendSephiraOptionUiModel>
    ): HistoryTimeSeriesChartUiModel {
        return HistoryTimeSeriesChartUiModel(
            sessionCount = sessions.size,
            lines = sephiraOptions.map { option ->
                HistoryTimeSeriesLineUiModel(
                    id = "${scoreType.name}_${option.sephiraId.name}",
                    sephiraId = option.sephiraId,
                    displayName = option.displayName,
                    points = sessions.map { session ->
                        val score = session.scoreFor(option.sephiraId)
                        HistoryTimeSeriesPointUiModel(
                            sessionId = session.sessionId,
                            completedAt = session.completedAt ?: session.startedAt,
                            value = score?.let { scorePercent(score.valueFor(scoreType)) }
                        )
                    }
                )
            }
        )
    }

    private fun scorePercent(value: Double): Int = (value * 100).roundToInt()

    companion object {
        private const val TREND_STEADY_THRESHOLD = 3
    }
}

private fun AssessmentSessionSnapshot.scoreFor(
    sephiraId: com.netah.hakkam.numyah.mind.domain.model.SephiraId
) = scores.firstOrNull { it.sephiraId == sephiraId }

private fun com.netah.hakkam.numyah.mind.domain.model.SephiraScore.valueFor(
    scoreType: HistoryTrendScoreType
): Double {
    return when (scoreType) {
        HistoryTrendScoreType.BALANCE -> balanceScore
        HistoryTrendScoreType.DEFICIENCY -> deficiencyScore
        HistoryTrendScoreType.EXCESS -> excessScore
    }
}

private fun AssessmentSessionSnapshot.highlights(
    sectionNames: Map<com.netah.hakkam.numyah.mind.domain.model.SephiraId, String>
): HistorySessionHighlights {
    val rankedScores = scores.map { score ->
        val balancePercent = scorePercent(score.balanceScore)
        val deficiencyPercent = scorePercent(score.deficiencyScore)
        val excessPercent = scorePercent(score.excessScore)
        HistoryScoreSummary(
            sephiraName = sectionNames[score.sephiraId]
                ?: score.sephiraId.name.lowercase(Locale.getDefault())
                    .replaceFirstChar { character -> character.titlecase(Locale.getDefault()) },
            balancePercent = balancePercent,
            imbalancePercent = deficiencyPercent + excessPercent
        )
    }

    return HistorySessionHighlights(
        needsAttention = rankedScores.maxWithOrNull(
            compareBy<HistoryScoreSummary> { it.imbalancePercent }
                .thenBy { it.balancePercent }
        ),
        mostSettled = rankedScores.minWithOrNull(
            compareBy<HistoryScoreSummary> { it.imbalancePercent }
                .thenByDescending { it.balancePercent }
        )
    )
}

private fun scorePercent(value: Double): Int = (value * 100).roundToInt()

private data class HistoryScoreSummary(
    val sephiraName: String,
    val balancePercent: Int,
    val imbalancePercent: Int
)

private data class HistorySessionHighlights(
    val needsAttention: HistoryScoreSummary?,
    val mostSettled: HistoryScoreSummary?
)
