package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ScoreInput
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.scoring.AssessmentScoringEngine
import com.netah.hakkam.numyah.mind.domain.usecase.CompleteAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAnswerParams
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentAnswerUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.StartOrResumeAssessmentParams
import com.netah.hakkam.numyah.mind.domain.usecase.StartOrResumeAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.UpdateAssessmentProgressParams
import com.netah.hakkam.numyah.mind.domain.usecase.UpdateAssessmentProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface AssessmentUiState {
    data object Loading : AssessmentUiState
    data class Intro(val model: AssessmentIntroUiModel) : AssessmentUiState
    data class Question(val model: AssessmentQuestionUiModel) : AssessmentUiState
    data class Completed(val model: AssessmentCompletedUiModel) : AssessmentUiState
    data class Error(val errorType: AssessmentErrorType) : AssessmentUiState
}

enum class AssessmentErrorType {
    LOAD,
    SAVE_ANSWER,
    CONTINUE,
    GO_BACK
}

data class AssessmentAnswerOptionUiModel(
    val id: String,
    val label: String,
    val numericValue: Int,
    val isSelected: Boolean
)

data class AssessmentProgressUiModel(
    val currentPageIndex: Int,
    val totalPages: Int,
    val currentQuestionNumber: Int,
    val totalQuestions: Int,
    val overallProgress: Float
)

data class AssessmentNavigationUiModel(
    val canGoBack: Boolean,
    val canContinue: Boolean,
    val isFirstQuestion: Boolean,
    val isLastQuestion: Boolean
)

data class AssessmentIntroUiModel(
    val questionnaireTitle: String,
    val sephiraName: String,
    val shortMeaning: String,
    val introText: String,
    val isResumeSession: Boolean,
    val progress: AssessmentProgressUiModel
)

data class AssessmentQuestionUiModel(
    val questionnaireTitle: String,
    val sephiraName: String,
    val currentPageTitle: String,
    val currentPageDescription: String,
    val currentQuestionPrompt: String,
    val answerOptions: List<AssessmentAnswerOptionUiModel>,
    val selectedOptionId: String?,
    val progress: AssessmentProgressUiModel,
    val navigation: AssessmentNavigationUiModel
)

data class AssessmentCompletedUiModel(
    val sephiraName: String,
    val dominantPole: Pole,
    val confidence: ConfidenceLevel,
    val balanceScore: Double,
    val deficiencyScore: Double,
    val excessScore: Double,
    val isLowConfidence: Boolean
)

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val startOrResumeAssessmentUseCase: StartOrResumeAssessmentUseCase,
    private val saveAssessmentAnswerUseCase: SaveAssessmentAnswerUseCase,
    private val updateAssessmentProgressUseCase: UpdateAssessmentProgressUseCase,
    private val completeAssessmentUseCase: CompleteAssessmentUseCase,
    private val assessmentScoringEngine: AssessmentScoringEngine,
    private val locale: Locale
) : ViewModel() {

    private val _uiState = MutableStateFlow<AssessmentUiState>(AssessmentUiState.Loading)
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    private var questionnaireContent: QuestionnaireContent? = null
    private var currentSnapshot: AssessmentSessionSnapshot? = null
    private var introDismissed: Boolean = false

    init {
        initialize()
    }

    fun initialize() {
        viewModelScope.launch {
            _uiState.value = AssessmentUiState.Loading
            try {
                val questionnaire = getCurrentQuestionnaireUseCase.run(locale).first()
                val section = questionnaire.sections.firstOrNull { it.sephiraId == targetSephira() }
                if (section == null) {
                    _uiState.value = AssessmentUiState.Error(AssessmentErrorType.LOAD)
                    return@launch
                }
                val sessionSnapshot = startOrResumeAssessmentUseCase.run(
                    StartOrResumeAssessmentParams(
                        questionnaireVersion = questionnaire.version,
                        initialSephiraId = targetSephira(),
                        totalQuestions = section.questions.size
                    )
                ).first()
                questionnaireContent = questionnaire
                currentSnapshot = sessionSnapshot
                introDismissed = sessionSnapshot.responses.isNotEmpty() ||
                    sessionSnapshot.currentPageIndex > 0 ||
                    sessionSnapshot.currentQuestionIndex > 0
                emitPhaseState()
            } catch (_: Throwable) {
                _uiState.value = AssessmentUiState.Error(AssessmentErrorType.LOAD)
            }
        }
    }

    fun startAssessment() {
        introDismissed = true
        emitPhaseState()
    }

    fun selectAnswer(optionId: String) {
        val questionnaire = questionnaireContent ?: return
        val snapshot = currentSnapshot ?: return
        val questionModel = (_uiState.value as? AssessmentUiState.Question)?.model ?: return
        val option = questionModel.answerOptions.firstOrNull { it.id == optionId } ?: return
        val question = currentQuestion(questionnaire, snapshot) ?: return

        viewModelScope.launch {
            try {
                val updatedSnapshot = saveAssessmentAnswerUseCase.run(
                    SaveAnswerParams(
                        sessionId = snapshot.sessionId,
                        questionId = question.id,
                        selectedOptionId = option.id,
                        numericValue = option.numericValue,
                        questionOrder = questionModel.progress.currentQuestionNumber - 1,
                        nextPageIndex = snapshot.currentPageIndex,
                        nextQuestionIndex = snapshot.currentQuestionIndex
                    )
                ).first()
                currentSnapshot = updatedSnapshot
                emitPhaseState()
            } catch (_: Throwable) {
                _uiState.value = AssessmentUiState.Error(AssessmentErrorType.SAVE_ANSWER)
            }
        }
    }

    fun continueAssessment() {
        val questionnaire = questionnaireContent ?: return
        val snapshot = currentSnapshot ?: return

        when (val state = _uiState.value) {
            is AssessmentUiState.Intro -> {
                startAssessment()
                return
            }
            is AssessmentUiState.Question -> {
                val selectedOption = state.model.answerOptions.firstOrNull { it.isSelected } ?: return
                val question = currentQuestion(questionnaire, snapshot) ?: return
                val nextPosition = nextPosition(questionnaire, snapshot)

                viewModelScope.launch {
                    try {
                        val updatedSnapshot = saveAssessmentAnswerUseCase.run(
                            SaveAnswerParams(
                                sessionId = snapshot.sessionId,
                                questionId = question.id,
                                selectedOptionId = selectedOption.id,
                                numericValue = selectedOption.numericValue,
                                questionOrder = state.model.progress.currentQuestionNumber - 1,
                                nextPageIndex = nextPosition.pageIndex,
                                nextQuestionIndex = nextPosition.questionIndex
                            )
                        ).first()

                        if (nextPosition.isComplete) {
                            val score = assessmentScoringEngine.score(
                                input = ScoreInput(
                                    questionnaire = questionnaire,
                                    sephiraId = targetSephira(),
                                    responses = updatedSnapshot.responses
                                ),
                                sessionId = updatedSnapshot.sessionId
                            )
                            val completedSnapshot = completeAssessmentUseCase.run(updatedSnapshot.sessionId to score).first()
                            currentSnapshot = completedSnapshot
                            emitCompletedState(completedSnapshot)
                        } else {
                            currentSnapshot = updatedSnapshot
                            emitPhaseState()
                        }
                    } catch (_: Throwable) {
                        _uiState.value = AssessmentUiState.Error(AssessmentErrorType.CONTINUE)
                    }
                }
            }
            else -> Unit
        }
    }

    fun goBack() {
        val questionnaire = questionnaireContent ?: return
        val snapshot = currentSnapshot ?: return
        if (_uiState.value is AssessmentUiState.Intro) {
            return
        }

        val previous = previousPosition(questionnaire, snapshot)
        if (previous == null) {
            introDismissed = false
            emitPhaseState()
            return
        }

        viewModelScope.launch {
            try {
                val updatedSnapshot = updateAssessmentProgressUseCase.run(
                    UpdateAssessmentProgressParams(
                        sessionId = snapshot.sessionId,
                        pageIndex = previous.pageIndex,
                        questionIndex = previous.questionIndex
                    )
                ).first()
                currentSnapshot = updatedSnapshot
                emitPhaseState()
            } catch (_: Throwable) {
                _uiState.value = AssessmentUiState.Error(AssessmentErrorType.GO_BACK)
            }
        }
    }

    fun retry() {
        initialize()
    }

    private fun emitPhaseState() {
        val questionnaire = questionnaireContent ?: return
        val snapshot = currentSnapshot ?: return
        val section = questionnaire.sections.first { it.sephiraId == targetSephira() }

        if (!introDismissed) {
            _uiState.value = AssessmentUiState.Intro(
                AssessmentIntroUiModel(
                    questionnaireTitle = questionnaire.title,
                    sephiraName = section.displayName,
                    shortMeaning = section.shortMeaning,
                    introText = section.introText,
                    isResumeSession = snapshot.responses.isNotEmpty() ||
                        snapshot.currentPageIndex > 0 ||
                        snapshot.currentQuestionIndex > 0,
                    progress = AssessmentProgressUiModel(
                        currentPageIndex = snapshot.currentPageIndex,
                        totalPages = section.pages.size,
                        currentQuestionNumber = 0,
                        totalQuestions = section.questions.size,
                        overallProgress = 0f
                    )
                )
            )
            return
        }

        val page = section.pages.getOrNull(snapshot.currentPageIndex) ?: section.pages.first()
        val questionId = page.questionIds.getOrNull(snapshot.currentQuestionIndex) ?: page.questionIds.first()
        val question = section.questions.first { it.id == questionId }
        val selectedResponse = snapshot.responses.firstOrNull { it.questionId == question.id }
        val questionNumber = absoluteQuestionNumber(section.pages, snapshot.currentPageIndex, snapshot.currentQuestionIndex)
        val totalQuestions = section.questions.size

        _uiState.value = AssessmentUiState.Question(
            AssessmentQuestionUiModel(
                questionnaireTitle = questionnaire.title,
                sephiraName = section.displayName,
                currentPageTitle = page.title,
                currentPageDescription = page.description,
                currentQuestionPrompt = question.prompt,
                answerOptions = questionnaire.responseScale.options.map { option ->
                    AssessmentAnswerOptionUiModel(
                        id = option.id,
                        label = option.label,
                        numericValue = option.numericValue,
                        isSelected = option.id == selectedResponse?.selectedOptionId
                    )
                },
                selectedOptionId = selectedResponse?.selectedOptionId,
                progress = AssessmentProgressUiModel(
                    currentPageIndex = snapshot.currentPageIndex,
                    totalPages = section.pages.size,
                    currentQuestionNumber = questionNumber,
                    totalQuestions = totalQuestions,
                    overallProgress = questionNumber.toFloat() / totalQuestions.toFloat()
                ),
                navigation = AssessmentNavigationUiModel(
                    canGoBack = questionNumber > 1,
                    canContinue = selectedResponse != null,
                    isFirstQuestion = questionNumber == 1,
                    isLastQuestion = questionNumber == totalQuestions
                )
            )
        )
    }

    private fun emitCompletedState(snapshot: AssessmentSessionSnapshot) {
        val questionnaire = questionnaireContent ?: return
        val section = questionnaire.sections.first { it.sephiraId == targetSephira() }
        val score = snapshot.scores.firstOrNull { it.sephiraId == targetSephira() } ?: return

        _uiState.value = AssessmentUiState.Completed(
            AssessmentCompletedUiModel(
                sephiraName = section.displayName,
                dominantPole = score.dominantPole,
                confidence = score.confidence,
                balanceScore = score.balanceScore,
                deficiencyScore = score.deficiencyScore,
                excessScore = score.excessScore,
                isLowConfidence = score.isLowConfidence
            )
        )
    }

    private fun currentQuestion(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ) = questionnaire.sections.first { it.sephiraId == targetSephira() }
        .let { section ->
            val page = section.pages[snapshot.currentPageIndex]
            val questionId = page.questionIds[snapshot.currentQuestionIndex]
            section.questions.first { it.id == questionId }
        }

    private fun nextPosition(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): AssessmentPosition {
        val section = questionnaire.sections.first { it.sephiraId == targetSephira() }
        val currentPage = section.pages[snapshot.currentPageIndex]
        return if (snapshot.currentQuestionIndex < currentPage.questionIds.lastIndex) {
            AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex + 1, false)
        } else if (snapshot.currentPageIndex < section.pages.lastIndex) {
            AssessmentPosition(snapshot.currentPageIndex + 1, 0, false)
        } else {
            AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex, true)
        }
    }

    private fun previousPosition(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): AssessmentPosition? {
        val section = questionnaire.sections.first { it.sephiraId == targetSephira() }
        return when {
            snapshot.currentQuestionIndex > 0 -> {
                AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex - 1, false)
            }
            snapshot.currentPageIndex > 0 -> {
                val previousPageIndex = snapshot.currentPageIndex - 1
                val previousPage = section.pages[previousPageIndex]
                AssessmentPosition(previousPageIndex, previousPage.questionIds.lastIndex, false)
            }
            else -> null
        }
    }

    private fun absoluteQuestionNumber(
        pages: List<QuestionPageContent>,
        pageIndex: Int,
        questionIndex: Int
    ): Int {
        val previousQuestions = pages.take(pageIndex).sumOf { it.questionIds.size }
        return previousQuestions + questionIndex + 1
    }

    private fun targetSephira(): SephiraId = MALKUTH_SEPHIRA

    private data class AssessmentPosition(
        val pageIndex: Int,
        val questionIndex: Int,
        val isComplete: Boolean
    )

    private companion object {
        val MALKUTH_SEPHIRA: SephiraId = SephiraId.MALKUTH
    }
}
