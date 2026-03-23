package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.observability.NonFatalIssueKey
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveCompletedAssessmentByIdUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

sealed interface ResultsUiState {
    data object Loading : ResultsUiState
    data object Empty : ResultsUiState
    data class Loaded(val model: ResultsOverviewUiModel) : ResultsUiState
    data object Error : ResultsUiState
}

data class ResultsOverviewUiModel(
    val title: String,
    val isHistoricalSession: Boolean,
    val activeAssessment: ActiveAssessmentUiModel?,
    val completedCount: Int,
    val totalCount: Int,
    val mostBalanced: ResultsSephiraUiModel?,
    val needsAttention: ResultsSephiraUiModel?,
    val sephirot: List<ResultsSephiraUiModel>
)

data class ResultsSephiraUiModel(
    val sephiraId: SephiraId,
    val sephiraName: String,
    val dominantPole: Pole,
    val confidence: ConfidenceLevel,
    val isLowConfidence: Boolean,
    val balanceScore: Double,
    val deficiencyScore: Double,
    val excessScore: Double,
    val balancePercent: Int,
    val deficiencyPercent: Int,
    val excessPercent: Int,
    val imbalancePercent: Int
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase,
    private val observeCompletedAssessmentByIdUseCase: ObserveCompletedAssessmentByIdUseCase,
    private val observeActiveAssessmentUseCase: ObserveActiveAssessmentUseCase,
    savedStateHandle: SavedStateHandle,
    private val currentLocaleProvider: CurrentLocaleProvider,
    private val appTelemetry: AppTelemetry
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()
    private val selectedSessionId = savedStateHandle.get<Long>(AppDestination.Results.sessionIdArg)
        ?.takeIf { it > 0L }

    init {
        viewModelScope.launch {
            try {
                combine(
                    getCurrentQuestionnaireUseCase.run(currentLocaleProvider.current()),
                    selectedAssessmentFlow(),
                    observeActiveAssessmentUseCase.run()
                ) { questionnaire, snapshot, activeAssessment ->
                    Triple(questionnaire, snapshot, activeAssessment)
                }.collect { (questionnaire, snapshot, activeAssessment) ->
                    _uiState.value = snapshot?.let {
                        ResultsUiState.Loaded(
                            buildModel(
                                questionnaire = questionnaire,
                                snapshot = it,
                                activeAssessment = activeAssessment
                            )
                        )
                    } ?: ResultsUiState.Empty
                }
            } catch (throwable: Throwable) {
                appTelemetry.recordNonFatal(
                    key = NonFatalIssueKey.RESULTS_LOAD_FAILED,
                    throwable = throwable,
                    attributes = mapOf(
                        "session_scope" to if (selectedSessionId != null) "saved" else "latest"
                    )
                )
                _uiState.value = ResultsUiState.Error
            }
        }
    }

    private fun selectedAssessmentFlow() = selectedSessionId?.let { sessionId ->
        observeCompletedAssessmentByIdUseCase.run(sessionId)
    } ?: observeLatestCompletedAssessmentUseCase.run()

    private fun buildModel(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot,
        activeAssessment: AssessmentSessionSnapshot?
    ): ResultsOverviewUiModel {
        val rankedScores = questionnaire.sections.mapNotNull { section ->
            snapshot.scores.firstOrNull { it.sephiraId == section.sephiraId }?.let { score ->
                val balancePercent = scorePercent(score.balanceScore)
                val deficiencyPercent = scorePercent(score.deficiencyScore)
                val excessPercent = scorePercent(score.excessScore)
                ResultsSephiraUiModel(
                    sephiraId = section.sephiraId,
                    sephiraName = section.displayName,
                    dominantPole = score.dominantPole,
                    confidence = score.confidence,
                    isLowConfidence = score.isLowConfidence,
                    balanceScore = score.balanceScore,
                    deficiencyScore = score.deficiencyScore,
                    excessScore = score.excessScore,
                    balancePercent = balancePercent,
                    deficiencyPercent = deficiencyPercent,
                    excessPercent = excessPercent,
                    imbalancePercent = deficiencyPercent + excessPercent
                )
            }
        }.sortedWith(
            compareByDescending<ResultsSephiraUiModel> { it.imbalancePercent }
                .thenBy { it.balancePercent }
        )

        val mostBalanced = rankedScores.minWithOrNull(
            compareBy<ResultsSephiraUiModel> { it.imbalancePercent }
                .thenByDescending { it.balancePercent }
        )
        val needsAttention = rankedScores.maxWithOrNull(
            compareBy<ResultsSephiraUiModel> { it.imbalancePercent }
                .thenBy { it.balancePercent }
        )

        return ResultsOverviewUiModel(
            title = questionnaire.title,
            isHistoricalSession = selectedSessionId != null,
            activeAssessment = activeAssessment?.let {
                buildActiveAssessmentUiModel(questionnaire, it)
            },
            completedCount = rankedScores.size,
            totalCount = questionnaire.sections.size,
            mostBalanced = mostBalanced,
            needsAttention = needsAttention,
            sephirot = rankedScores
        )
    }

    private fun scorePercent(value: Double): Int = (value * 100).roundToInt()
}
