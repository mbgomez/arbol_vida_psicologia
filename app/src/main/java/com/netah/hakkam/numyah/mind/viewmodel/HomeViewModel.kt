package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Loaded(val model: HomeUiModel) : HomeUiState
    data object Error : HomeUiState
}

data class HomeUiModel(
    val activeAssessment: HomeActiveAssessmentUiModel?,
    val latestReflection: HomeSummaryUiModel?
)

data class HomeActiveAssessmentUiModel(
    val sephiraName: String,
    val completedSephirotCount: Int,
    val totalSephirotCount: Int,
    val currentQuestionNumber: Int,
    val totalQuestions: Int,
    val isAtSectionStart: Boolean
)

data class HomeSummaryUiModel(
    val lastAssessmentDate: String,
    val daysSinceLastAssessment: Int,
    val needsAttentionSephiraName: String,
    val mostBalancedSephiraName: String,
    val currentFocus: HomeFocusUiModel
)

data class HomeFocusUiModel(
    val sephiraName: String,
    val dominantPole: Pole
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val observeActiveAssessmentUseCase: ObserveActiveAssessmentUseCase,
    private val observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var observeHomeSummaryJob: Job? = null

    init {
        observeHomeSummary()
    }

    fun retry() {
        _uiState.value = HomeUiState.Loading
        observeHomeSummary()
    }

    private fun observeHomeSummary() {
        observeHomeSummaryJob?.cancel()
        observeHomeSummaryJob = viewModelScope.launch {
            try {
                combine(
                    getCurrentQuestionnaireUseCase.run(currentLocaleProvider.current()),
                    observeActiveAssessmentUseCase.run(),
                    observeLatestCompletedAssessmentUseCase.run()
                ) { questionnaire, activeAssessment, latestAssessment ->
                    Triple(questionnaire, activeAssessment, latestAssessment)
                }.collect { (questionnaire, activeAssessment, latestAssessment) ->
                    val activeModel = activeAssessment?.let {
                        buildActiveModel(questionnaire, it)
                    }
                    val latestModel = latestAssessment?.let {
                        buildLatestReflectionModel(questionnaire, it)
                    }

                    _uiState.value = if (activeModel == null && latestModel == null) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Loaded(
                            HomeUiModel(
                                activeAssessment = activeModel,
                                latestReflection = latestModel
                            )
                        )
                    }
                }
            } catch (_: Throwable) {
                _uiState.value = HomeUiState.Error
            }
        }
    }

    private fun buildActiveModel(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): HomeActiveAssessmentUiModel? {
        return buildActiveAssessmentUiModel(questionnaire, snapshot)?.let { model ->
            HomeActiveAssessmentUiModel(
                sephiraName = model.sephiraName,
                completedSephirotCount = model.completedSephirotCount,
                totalSephirotCount = model.totalSephirotCount,
                currentQuestionNumber = model.currentQuestionNumber,
                totalQuestions = model.totalQuestions,
                isAtSectionStart = model.isAtSectionStart
            )
        }
    }

    private fun buildLatestReflectionModel(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): HomeSummaryUiModel? {
        val sectionNames = questionnaire.sections.associate { it.sephiraId to it.displayName }
        val rankedScores = snapshot.scores.map { score ->
            val sephiraName = sectionNames[score.sephiraId] ?: return@map null
            HomeScoreSummary(
                sephiraName = sephiraName,
                dominantPole = score.dominantPole,
                balanceScore = score.balanceScore,
                imbalanceScore = score.deficiencyScore + score.excessScore
            )
        }.filterNotNull()

        val needsAttention = rankedScores.maxWithOrNull(
            compareBy<HomeScoreSummary> { it.imbalanceScore }
                .thenBy { it.balanceScore }
        ) ?: return null
        val mostBalanced = rankedScores.minWithOrNull(
            compareBy<HomeScoreSummary> { it.imbalanceScore }
                .thenByDescending { it.balanceScore }
        ) ?: return null

        return HomeSummaryUiModel(
            lastAssessmentDate = formatTimestamp(
                timestamp = snapshot.completedAt ?: snapshot.startedAt,
                locale = currentLocaleProvider.current()
            ),
            daysSinceLastAssessment = daysSince(snapshot.completedAt ?: snapshot.startedAt),
            needsAttentionSephiraName = needsAttention.sephiraName,
            mostBalancedSephiraName = mostBalanced.sephiraName,
            currentFocus = HomeFocusUiModel(
                sephiraName = needsAttention.sephiraName,
                dominantPole = needsAttention.dominantPole
            )
        )
    }

    private fun formatTimestamp(timestamp: Long, locale: Locale): String {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(locale)
            .format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
    }

    private fun daysSince(timestamp: Long): Int {
        val assessmentDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now(ZoneId.systemDefault())
        return ChronoUnit.DAYS.between(assessmentDate, today).toInt().coerceAtLeast(0)
    }
}

private data class HomeScoreSummary(
    val sephiraName: String,
    val dominantPole: Pole,
    val balanceScore: Double,
    val imbalanceScore: Double
)
