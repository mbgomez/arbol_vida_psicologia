package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Loaded(val model: HomeSummaryUiModel) : HomeUiState
    data object Error : HomeUiState
}

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
    private val observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeHomeSummary()
    }

    private fun observeHomeSummary() {
        viewModelScope.launch {
            try {
                combine(
                    getCurrentQuestionnaireUseCase.run(currentLocaleProvider.current()),
                    observeLatestCompletedAssessmentUseCase.run()
                ) { questionnaire, latestAssessment ->
                    questionnaire to latestAssessment
                }.collect { (questionnaire, latestAssessment) ->
                    _uiState.value = latestAssessment?.let {
                        buildModel(questionnaire, it)?.let(HomeUiState::Loaded) ?: HomeUiState.Empty
                    } ?: HomeUiState.Empty
                }
            } catch (_: Throwable) {
                _uiState.value = HomeUiState.Error
            }
        }
    }

    private fun buildModel(
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
