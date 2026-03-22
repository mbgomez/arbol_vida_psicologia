package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveCompletedAssessmentByIdUseCase
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

sealed interface SephiraDetailUiState {
    data object Loading : SephiraDetailUiState
    data class Loaded(val model: SephiraDetailUiModel) : SephiraDetailUiState
    data object NotFound : SephiraDetailUiState
    data object Error : SephiraDetailUiState
}

data class SephiraDetailUiModel(
    val sephiraId: SephiraId,
    val sephiraName: String,
    val shortMeaning: String,
    val dominantPole: Pole,
    val confidence: ConfidenceLevel,
    val isLowConfidence: Boolean,
    val balancePercent: Int,
    val deficiencyPercent: Int,
    val excessPercent: Int,
    val healthyExpression: String,
    val deficiencyPattern: String,
    val excessPattern: String,
    val suggestedPractices: List<String>,
    val isHistoricalSession: Boolean
)

@HiltViewModel
class SephiraDetailViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase,
    private val observeCompletedAssessmentByIdUseCase: ObserveCompletedAssessmentByIdUseCase,
    savedStateHandle: SavedStateHandle,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<SephiraDetailUiState>(SephiraDetailUiState.Loading)
    val uiState: StateFlow<SephiraDetailUiState> = _uiState.asStateFlow()

    private val selectedSessionId = savedStateHandle.get<Long>(AppDestination.ResultsDetail.sessionIdArg)
        ?.takeIf { it > 0L }
    private val selectedSephiraId = savedStateHandle.get<String>(AppDestination.ResultsDetail.sephiraIdArg)
        ?.let { rawValue -> runCatching { SephiraId.valueOf(rawValue) }.getOrNull() }

    init {
        observeDetail()
    }

    private fun observeDetail() {
        if (selectedSephiraId == null) {
            _uiState.value = SephiraDetailUiState.NotFound
            return
        }

        viewModelScope.launch {
            try {
                combine(
                    getCurrentQuestionnaireUseCase.run(currentLocaleProvider.current()),
                    selectedAssessmentFlow()
                ) { questionnaire, snapshot ->
                    questionnaire to snapshot
                }.collect { (questionnaire, snapshot) ->
                    _uiState.value = if (snapshot == null) {
                        SephiraDetailUiState.NotFound
                    } else {
                        buildModel(
                            questionnaire = questionnaire,
                            snapshot = snapshot,
                            sephiraId = selectedSephiraId
                        )?.let { model ->
                            SephiraDetailUiState.Loaded(model)
                        } ?: SephiraDetailUiState.NotFound
                    }
                }
            } catch (_: Throwable) {
                _uiState.value = SephiraDetailUiState.Error
            }
        }
    }

    private fun selectedAssessmentFlow() = selectedSessionId?.let { sessionId ->
        observeCompletedAssessmentByIdUseCase.run(sessionId)
    } ?: observeLatestCompletedAssessmentUseCase.run()

    private fun buildModel(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot,
        sephiraId: SephiraId
    ): SephiraDetailUiModel? {
        val section = questionnaire.sections.firstOrNull { it.sephiraId == sephiraId } ?: return null
        val score = snapshot.scores.firstOrNull { it.sephiraId == sephiraId } ?: return null

        return SephiraDetailUiModel(
            sephiraId = section.sephiraId,
            sephiraName = section.displayName,
            shortMeaning = section.shortMeaning,
            dominantPole = score.dominantPole,
            confidence = score.confidence,
            isLowConfidence = score.isLowConfidence,
            balancePercent = scorePercent(score.balanceScore),
            deficiencyPercent = scorePercent(score.deficiencyScore),
            excessPercent = scorePercent(score.excessScore),
            healthyExpression = section.detailContent.healthyExpression,
            deficiencyPattern = section.detailContent.deficiencyPattern,
            excessPattern = section.detailContent.excessPattern,
            suggestedPractices = section.detailContent.suggestedPractices,
            isHistoricalSession = selectedSessionId != null
        )
    }

    private fun scorePercent(value: Double): Int = (value * 100).roundToInt()
}
