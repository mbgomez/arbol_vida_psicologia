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
import kotlin.math.roundToInt
import java.util.Locale

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object Empty : HistoryUiState
    data class Loaded(val model: HistoryUiModel) : HistoryUiState
    data object Error : HistoryUiState
}

data class HistoryUiModel(
    val questionnaireTitle: String,
    val totalSessions: Int,
    val sessions: List<HistorySessionUiModel>
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
        val sessions = history.map { snapshot ->
            val rankedScores = snapshot.scores.map { score ->
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

            val needsAttention = rankedScores.maxWithOrNull(
                compareBy<HistoryScoreSummary> { it.imbalancePercent }
                    .thenBy { it.balancePercent }
            )
            val mostBalanced = rankedScores.minWithOrNull(
                compareBy<HistoryScoreSummary> { it.imbalancePercent }
                    .thenByDescending { it.balancePercent }
            )

            HistorySessionUiModel(
                sessionId = snapshot.sessionId,
                startedAt = snapshot.startedAt,
                completedAt = snapshot.completedAt ?: snapshot.startedAt,
                completedCount = snapshot.scores.size,
                totalCount = questionnaire.sections.size,
                needsAttentionSephiraName = needsAttention?.sephiraName,
                needsAttentionImbalancePercent = needsAttention?.imbalancePercent,
                mostBalancedSephiraName = mostBalanced?.sephiraName,
                mostBalancedBalancePercent = mostBalanced?.balancePercent
            )
        }

        return HistoryUiModel(
            questionnaireTitle = questionnaire.title,
            totalSessions = sessions.size,
            sessions = sessions
        )
    }

    private fun scorePercent(value: Double): Int = (value * 100).roundToInt()
}

private data class HistoryScoreSummary(
    val sephiraName: String,
    val balancePercent: Int,
    val imbalancePercent: Int
)
