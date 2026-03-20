package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed interface ResultsUiState {
    data object Loading : ResultsUiState
    data object Empty : ResultsUiState
    data class Loaded(val model: ResultsOverviewUiModel) : ResultsUiState
    data object Error : ResultsUiState
}

data class ResultsOverviewUiModel(
    val title: String,
    val subtitle: String,
    val completedCount: Int,
    val totalCount: Int,
    val sephirot: List<ResultsSephiraUiModel>
)

data class ResultsSephiraUiModel(
    val sephiraName: String,
    val dominantPole: Pole,
    val confidence: ConfidenceLevel,
    val isLowConfidence: Boolean,
    val balanceScore: Double,
    val deficiencyScore: Double,
    val excessScore: Double
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase,
    private val locale: Locale
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                combine(
                    getCurrentQuestionnaireUseCase.run(locale),
                    observeLatestCompletedAssessmentUseCase.run()
                ) { questionnaire, snapshot ->
                    questionnaire to snapshot
                }.collect { (questionnaire, snapshot) ->
                    _uiState.value = snapshot?.let {
                        ResultsUiState.Loaded(buildModel(questionnaire, it))
                    } ?: ResultsUiState.Empty
                }
            } catch (_: Throwable) {
                _uiState.value = ResultsUiState.Error
            }
        }
    }

    private fun buildModel(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): ResultsOverviewUiModel {
        val orderedScores = questionnaire.sections.mapNotNull { section ->
            snapshot.scores.firstOrNull { it.sephiraId == section.sephiraId }?.let { score ->
                ResultsSephiraUiModel(
                    sephiraName = section.displayName,
                    dominantPole = score.dominantPole,
                    confidence = score.confidence,
                    isLowConfidence = score.isLowConfidence,
                    balanceScore = score.balanceScore,
                    deficiencyScore = score.deficiencyScore,
                    excessScore = score.excessScore
                )
            }
        }

        return ResultsOverviewUiModel(
            title = questionnaire.title,
            subtitle = snapshot.completedAt?.let { "Most recent completed reflection" } ?: "Current completed reflection",
            completedCount = orderedScores.size,
            totalCount = questionnaire.sections.size,
            sephirot = orderedScores
        )
    }
}
