package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.observability.NonFatalIssueKey
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ScoreInput
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.scoring.AssessmentScoringEngine
import com.netah.hakkam.numyah.mind.domain.usecase.AdvanceAssessmentSectionParams
import com.netah.hakkam.numyah.mind.domain.usecase.AdvanceAssessmentSectionUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.CompleteAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAnswerParams
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentAnswerUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentScoreUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.StartOrResumeAssessmentParams
import com.netah.hakkam.numyah.mind.domain.usecase.StartOrResumeAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.UpdateAssessmentProgressParams
import com.netah.hakkam.numyah.mind.domain.usecase.UpdateAssessmentProgressUseCase
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface AssessmentUiState {
    data object Loading : AssessmentUiState
    data class HonestyNotice(val model: AssessmentHonestyNoticeUiModel) : AssessmentUiState
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

data class AssessmentHonestyNoticeUiModel(
    val isDoNotShowAgainChecked: Boolean
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
    val sephiraId: SephiraId,
    val sephiraName: String,
    val shortMeaning: String,
    val introText: String,
    val isResumeSession: Boolean,
    val progress: AssessmentProgressUiModel
)

data class AssessmentQuestionUiModel(
    val questionnaireTitle: String,
    val sephiraId: SephiraId,
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
    val sephiraId: SephiraId,
    val sephiraName: String,
    val sectionSummary: String,
    val completionReflection: String,
    val practiceSuggestion: String?,
    val dominantPole: Pole,
    val confidence: ConfidenceLevel,
    val balanceScore: Double,
    val deficiencyScore: Double,
    val excessScore: Double,
    val isLowConfidence: Boolean,
    val hasNextSephira: Boolean,
    val nextSephiraName: String?
)

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val getAssessmentHonestyNoticeVisibilityUseCase: GetAssessmentHonestyNoticeVisibilityUseCase,
    private val setAssessmentHonestyNoticeVisibilityUseCase: SetAssessmentHonestyNoticeVisibilityUseCase,
    private val startOrResumeAssessmentUseCase: StartOrResumeAssessmentUseCase,
    private val saveAssessmentAnswerUseCase: SaveAssessmentAnswerUseCase,
    private val updateAssessmentProgressUseCase: UpdateAssessmentProgressUseCase,
    private val saveAssessmentScoreUseCase: SaveAssessmentScoreUseCase,
    private val advanceAssessmentSectionUseCase: AdvanceAssessmentSectionUseCase,
    private val completeAssessmentUseCase: CompleteAssessmentUseCase,
    private val assessmentScoringEngine: AssessmentScoringEngine,
    private val currentLocaleProvider: CurrentLocaleProvider,
    private val appTelemetry: AppTelemetry,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<AssessmentUiState>(AssessmentUiState.Loading)
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    private var questionnaireContent: QuestionnaireContent? = null
    private var currentSnapshot: AssessmentSessionSnapshot? = null
    private var honestyNoticeDismissed: Boolean = false
    private var doNotShowHonestyNoticeAgain: Boolean = false
    private var introDismissed: Boolean = false
    private var pendingNextSection: SephiraSectionContent? = null
    private val forceStartFresh = savedStateHandle[AppDestination.Assessment.startFreshArg] ?: false

    init {
        initialize()
    }

    fun initialize() {
        viewModelScope.launch {
            _uiState.value = AssessmentUiState.Loading
            try {
                val questionnaire = getCurrentQuestionnaireUseCase.run(currentLocaleProvider.current()).first()
                val firstSection = questionnaire.sections.firstOrNull()
                if (firstSection == null) {
                    _uiState.value = AssessmentUiState.Error(AssessmentErrorType.LOAD)
                    return@launch
                }

                val sessionSnapshot = startOrResumeAssessmentUseCase.run(
                    StartOrResumeAssessmentParams(
                        questionnaireVersion = questionnaire.version,
                        initialSephiraId = firstSection.sephiraId,
                        totalQuestions = firstSection.questions.size,
                        forceStartFresh = forceStartFresh
                    )
                ).first()

                if (currentSection(questionnaire, sessionSnapshot) == null) {
                    _uiState.value = AssessmentUiState.Error(AssessmentErrorType.LOAD)
                    return@launch
                }

                questionnaireContent = questionnaire
                currentSnapshot = sessionSnapshot
                pendingNextSection = null
                val currentSection = currentSection(questionnaire, sessionSnapshot)
                    ?: run {
                        _uiState.value = AssessmentUiState.Error(AssessmentErrorType.LOAD)
                        return@launch
                    }
                val hasCurrentSectionProgress = hasProgressInCurrentSection(currentSection, sessionSnapshot)
                val shouldShowHonestyNotice = getAssessmentHonestyNoticeVisibilityUseCase.run().first()
                honestyNoticeDismissed = !shouldShowHonestyNotice ||
                    sessionSnapshot.responses.isNotEmpty() ||
                    sessionSnapshot.currentPageIndex > 0 ||
                    sessionSnapshot.currentQuestionIndex > 0
                doNotShowHonestyNoticeAgain = false
                introDismissed = hasCurrentSectionProgress
                emitPhaseState()
            } catch (throwable: Throwable) {
                appTelemetry.recordNonFatal(
                    key = NonFatalIssueKey.ASSESSMENT_LOAD_FAILED,
                    throwable = throwable
                )
                _uiState.value = AssessmentUiState.Error(AssessmentErrorType.LOAD)
            }
        }
    }

    fun startAssessment() {
        introDismissed = true
        emitPhaseState()
    }

    fun setDoNotShowHonestyNoticeAgain(checked: Boolean) {
        doNotShowHonestyNoticeAgain = checked
        val state = _uiState.value as? AssessmentUiState.HonestyNotice ?: return
        _uiState.value = state.copy(
            model = state.model.copy(isDoNotShowAgainChecked = checked)
        )
    }

    fun continueFromHonestyNotice() {
        viewModelScope.launch {
            setAssessmentHonestyNoticeVisibilityUseCase
                .run(!doNotShowHonestyNoticeAgain)
                .first()
            honestyNoticeDismissed = true
            emitPhaseState()
        }
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
            } catch (throwable: Throwable) {
                appTelemetry.recordNonFatal(
                    key = NonFatalIssueKey.ASSESSMENT_SAVE_ANSWER_FAILED,
                    throwable = throwable,
                    attributes = mapOf("sephira_id" to snapshot.currentSephiraId.name.lowercase())
                )
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
                            val activeSephira = activeSephira(updatedSnapshot)
                            val score = assessmentScoringEngine.score(
                                input = ScoreInput(
                                    questionnaire = questionnaire,
                                    sephiraId = activeSephira,
                                    responses = updatedSnapshot.responses
                                ),
                                sessionId = updatedSnapshot.sessionId
                            )
                            val nextSection = nextSection(questionnaire, activeSephira)
                            if (nextSection == null) {
                                val completedSnapshot = completeAssessmentUseCase.run(updatedSnapshot.sessionId to score).first()
                                currentSnapshot = completedSnapshot
                                pendingNextSection = null
                                appTelemetry.trackAssessmentCompleted(completedSnapshot.scores.size)
                                emitCompletedState(completedSnapshot, nextSection = null)
                            } else {
                                val savedScoreSnapshot = saveAssessmentScoreUseCase
                                    .run(updatedSnapshot.sessionId to score)
                                    .first()
                                currentSnapshot = savedScoreSnapshot
                                pendingNextSection = nextSection
                                emitCompletedState(savedScoreSnapshot, nextSection = nextSection)
                            }
                        } else {
                            currentSnapshot = updatedSnapshot
                            emitPhaseState()
                        }
                    } catch (throwable: Throwable) {
                        appTelemetry.recordNonFatal(
                            key = NonFatalIssueKey.ASSESSMENT_CONTINUE_FAILED,
                            throwable = throwable,
                            attributes = mapOf("sephira_id" to snapshot.currentSephiraId.name.lowercase())
                        )
                        _uiState.value = AssessmentUiState.Error(AssessmentErrorType.CONTINUE)
                    }
                }
            }
            else -> Unit
        }
    }

    fun continueFromCompletedResult() {
        val snapshot = currentSnapshot ?: return
        val nextSection = pendingNextSection ?: return

        viewModelScope.launch {
            try {
                val advancedSnapshot = advanceAssessmentSectionUseCase.run(
                    AdvanceAssessmentSectionParams(
                        sessionId = snapshot.sessionId,
                        sephiraId = nextSection.sephiraId,
                        totalQuestions = nextSection.questions.size
                    )
                ).first()
                currentSnapshot = advancedSnapshot
                pendingNextSection = null
                introDismissed = false
                emitPhaseState()
            } catch (throwable: Throwable) {
                appTelemetry.recordNonFatal(
                    key = NonFatalIssueKey.ASSESSMENT_CONTINUE_FAILED,
                    throwable = throwable,
                    attributes = mapOf("sephira_id" to snapshot.currentSephiraId.name.lowercase())
                )
                _uiState.value = AssessmentUiState.Error(AssessmentErrorType.CONTINUE)
            }
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
            } catch (throwable: Throwable) {
                appTelemetry.recordNonFatal(
                    key = NonFatalIssueKey.ASSESSMENT_GO_BACK_FAILED,
                    throwable = throwable,
                    attributes = mapOf("sephira_id" to snapshot.currentSephiraId.name.lowercase())
                )
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
        val section = currentSection(questionnaire, snapshot) ?: return

        if (!honestyNoticeDismissed) {
            _uiState.value = AssessmentUiState.HonestyNotice(
                AssessmentHonestyNoticeUiModel(
                    isDoNotShowAgainChecked = doNotShowHonestyNoticeAgain
                )
            )
            return
        }

        if (!introDismissed) {
            val hasSectionProgress = hasProgressInCurrentSection(section, snapshot)
            _uiState.value = AssessmentUiState.Intro(
                AssessmentIntroUiModel(
                    questionnaireTitle = questionnaire.title,
                    sephiraId = section.sephiraId,
                    sephiraName = section.displayName,
                    shortMeaning = section.shortMeaning,
                    introText = section.introText,
                    isResumeSession = hasSectionProgress,
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
                sephiraId = section.sephiraId,
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

    private fun emitCompletedState(
        snapshot: AssessmentSessionSnapshot,
        nextSection: SephiraSectionContent?
    ) {
        val questionnaire = questionnaireContent ?: return
        val activeSephira = activeSephira(snapshot)
        val section = questionnaire.sections.first { it.sephiraId == activeSephira }
        val score = snapshot.scores.firstOrNull { it.sephiraId == activeSephira } ?: return
        val completionState = when (score.dominantPole) {
            Pole.BALANCE -> section.completionContent.balanced
            Pole.DEFICIENCY -> section.completionContent.deficiency
            Pole.EXCESS -> section.completionContent.excess
        }

        _uiState.value = AssessmentUiState.Completed(
            AssessmentCompletedUiModel(
                sephiraId = section.sephiraId,
                sephiraName = section.displayName,
                sectionSummary = section.completionContent.sectionSummary,
                completionReflection = completionState.reflection,
                practiceSuggestion = completionState.practice,
                dominantPole = score.dominantPole,
                confidence = score.confidence,
                balanceScore = score.balanceScore,
                deficiencyScore = score.deficiencyScore,
                excessScore = score.excessScore,
                isLowConfidence = score.isLowConfidence,
                hasNextSephira = nextSection != null,
                nextSephiraName = nextSection?.displayName
            )
        )
    }

    private fun currentQuestion(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ) = currentSection(questionnaire, snapshot)
        ?.let { section ->
            val page = section.pages[snapshot.currentPageIndex]
            val questionId = page.questionIds[snapshot.currentQuestionIndex]
            section.questions.first { it.id == questionId }
        }

    private fun nextPosition(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): AssessmentPosition {
        val section = currentSection(questionnaire, snapshot)
            ?: return AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex, true)
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
        val section = currentSection(questionnaire, snapshot) ?: return null
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

    private fun currentSection(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): SephiraSectionContent? = questionnaire.sections.firstOrNull { it.sephiraId == activeSephira(snapshot) }

    private fun hasProgressInCurrentSection(
        section: SephiraSectionContent,
        snapshot: AssessmentSessionSnapshot
    ): Boolean {
        if (snapshot.currentPageIndex > 0 || snapshot.currentQuestionIndex > 0) {
            return true
        }

        val sectionQuestionIds = section.questions.map { it.id }.toSet()
        return snapshot.responses.any { response ->
            response.questionId in sectionQuestionIds
        }
    }

    private fun nextSection(
        questionnaire: QuestionnaireContent,
        currentSephiraId: SephiraId
    ): SephiraSectionContent? {
        val currentIndex = questionnaire.sections.indexOfFirst { it.sephiraId == currentSephiraId }
        if (currentIndex == -1) {
            return null
        }
        return questionnaire.sections.getOrNull(currentIndex + 1)
    }

    private fun activeSephira(snapshot: AssessmentSessionSnapshot): SephiraId = snapshot.currentSephiraId

    private data class AssessmentPosition(
        val pageIndex: Int,
        val questionIndex: Int,
        val isComplete: Boolean
    )
}
