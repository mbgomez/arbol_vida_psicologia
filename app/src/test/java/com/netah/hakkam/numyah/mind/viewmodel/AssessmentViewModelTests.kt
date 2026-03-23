package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SavedResponse
import com.netah.hakkam.numyah.mind.domain.model.ScoreInput
import com.netah.hakkam.numyah.mind.domain.model.SephiraDetailContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.scoring.AssessmentScoringEngine
import com.netah.hakkam.numyah.mind.domain.usecase.CompleteAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.AdvanceAssessmentSectionParams
import com.netah.hakkam.numyah.mind.domain.usecase.AdvanceAssessmentSectionUseCase
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
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentViewModelTests {

    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase
    private lateinit var getAssessmentHonestyNoticeVisibilityUseCase: GetAssessmentHonestyNoticeVisibilityUseCase
    private lateinit var setAssessmentHonestyNoticeVisibilityUseCase: SetAssessmentHonestyNoticeVisibilityUseCase
    private lateinit var startOrResumeAssessmentUseCase: StartOrResumeAssessmentUseCase
    private lateinit var saveAssessmentAnswerUseCase: SaveAssessmentAnswerUseCase
    private lateinit var updateAssessmentProgressUseCase: UpdateAssessmentProgressUseCase
    private lateinit var saveAssessmentScoreUseCase: SaveAssessmentScoreUseCase
    private lateinit var advanceAssessmentSectionUseCase: AdvanceAssessmentSectionUseCase
    private lateinit var completeAssessmentUseCase: CompleteAssessmentUseCase
    private lateinit var assessmentScoringEngine: AssessmentScoringEngine
    private lateinit var currentLocaleProvider: CurrentLocaleProvider
    private lateinit var appTelemetry: AppTelemetry

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getCurrentQuestionnaireUseCase = mockk(relaxed = true)
        getAssessmentHonestyNoticeVisibilityUseCase = mockk(relaxed = true)
        setAssessmentHonestyNoticeVisibilityUseCase = mockk(relaxed = true)
        startOrResumeAssessmentUseCase = mockk(relaxed = true)
        saveAssessmentAnswerUseCase = mockk(relaxed = true)
        updateAssessmentProgressUseCase = mockk(relaxed = true)
        saveAssessmentScoreUseCase = mockk(relaxed = true)
        advanceAssessmentSectionUseCase = mockk(relaxed = true)
        completeAssessmentUseCase = mockk(relaxed = true)
        assessmentScoringEngine = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        appTelemetry = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
    }

    @Test
    fun init_withFreshSessionAndHonestyEnabled_emitsHonestyNoticeState() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(true)
        every {
            startOrResumeAssessmentUseCase.run(
                StartOrResumeAssessmentParams(
                    questionnaireVersion = "malkuth-v1",
                    initialSephiraId = SephiraId.MALKUTH,
                    totalQuestions = 6
                )
            )
        } returns flowOf(testSnapshot())

        val viewModel = createViewModel()
        val state = viewModel.uiState.value as AssessmentUiState.HonestyNotice

        assertFalse(state.model.isDoNotShowAgainChecked)
    }

    @Test
    fun init_withSavedResponses_emitsQuestionState() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(true)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(
            testSnapshot(
                currentQuestionIndex = 1,
                responses = listOf(
                    SavedResponse(
                        questionId = "q1",
                        selectedOptionId = "agree",
                        numericValue = 3,
                        questionOrder = 0,
                        answeredAt = 1L
                    )
                )
            )
        )

        val viewModel = createViewModel()
        val state = viewModel.uiState.value as AssessmentUiState.Question

        assertEquals("Q2", state.model.currentQuestionPrompt)
        assertEquals(2, state.model.progress.currentQuestionNumber)
        assertTrue(state.model.navigation.canGoBack)
    }

    @Test
    fun init_withoutMalkuthSection_emitsLoadError() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(
            QuestionnaireContent(
                version = "other-v1",
                title = "Other questionnaire",
                responseScale = ResponseScaleDefinition(
                    format = QuestionFormat.LIKERT_5,
                    options = listOf(AnswerOption("agree", "Agree", 3))
                ),
                sections = emptyList()
            )
        )
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(true)

        val viewModel = createViewModel()
        val state = viewModel.uiState.value as AssessmentUiState.Error

        assertEquals(AssessmentErrorType.LOAD, state.errorType)
    }

    @Test
    fun continueFromHonestyNotice_movesToIntroAndPersistsPreference() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(true)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(testSnapshot())
        every { setAssessmentHonestyNoticeVisibilityUseCase.run(false) } returns flowOf(false)

        val viewModel = createViewModel()
        viewModel.setDoNotShowHonestyNoticeAgain(true)
        viewModel.continueFromHonestyNotice()

        val state = viewModel.uiState.value as AssessmentUiState.Intro
        verify(exactly = 1) { setAssessmentHonestyNoticeVisibilityUseCase.run(false) }
        assertEquals("Malkuth", state.model.sephiraName)
    }

    @Test
    fun selectAnswer_savesCurrentAnswerWithoutAdvancing() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(false)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(testSnapshot())
        every {
            saveAssessmentAnswerUseCase.run(
                SaveAnswerParams(
                    sessionId = 1L,
                    questionId = "q1",
                    selectedOptionId = "agree",
                    numericValue = 3,
                    questionOrder = 0,
                    nextPageIndex = 0,
                    nextQuestionIndex = 0
                )
            )
        } returns flowOf(
            testSnapshot(
                responses = listOf(SavedResponse("q1", "agree", 3, 0, 1L))
            )
        )

        val viewModel = createViewModel()
        viewModel.startAssessment()
        viewModel.selectAnswer("agree")

        val state = viewModel.uiState.value as AssessmentUiState.Question
        verify(exactly = 1) {
            saveAssessmentAnswerUseCase.run(
                SaveAnswerParams(
                    sessionId = 1L,
                    questionId = "q1",
                    selectedOptionId = "agree",
                    numericValue = 3,
                    questionOrder = 0,
                    nextPageIndex = 0,
                    nextQuestionIndex = 0
                )
            )
        }
        assertEquals("agree", state.model.selectedOptionId)
        assertTrue(state.model.navigation.canContinue)
        assertEquals("Q1", state.model.currentQuestionPrompt)
    }

    @Test
    fun continueAssessment_onLastQuestion_completesState() = coroutinesRule.runBlockingTest {
        val questionnaire = singleQuestionnaire()
        val inProgressSnapshot = testSnapshot(
            questionnaireVersion = "single-v1",
            totalQuestions = 1,
            responses = listOf(
                SavedResponse("q_last", "agree", 3, 0, 1L)
            )
        )
        val score = SephiraScore(
            sessionId = 1L,
            sephiraId = SephiraId.MALKUTH,
            balanceScore = 0.40,
            deficiencyScore = 0.30,
            excessScore = 0.30,
            dominantPole = Pole.BALANCE,
            confidence = ConfidenceLevel.LOW,
            isLowConfidence = true
        )
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(questionnaire)
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(false)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(inProgressSnapshot)
        every {
            saveAssessmentAnswerUseCase.run(
                SaveAnswerParams(
                    sessionId = 1L,
                    questionId = "q_last",
                    selectedOptionId = "agree",
                    numericValue = 3,
                    questionOrder = 0,
                    nextPageIndex = 0,
                    nextQuestionIndex = 0
                )
            )
        } returns flowOf(inProgressSnapshot)
        every { assessmentScoringEngine.score(any<ScoreInput>(), 1L) } returns score
        every { completeAssessmentUseCase.run(1L to score) } returns flowOf(
            inProgressSnapshot.copy(
                status = AssessmentStatus.COMPLETED,
                completedAt = 2L,
                scores = listOf(score)
            )
        )

        val viewModel = createViewModel(locale = Locale.ENGLISH)
        viewModel.startAssessment()
        viewModel.continueAssessment()

        val state = viewModel.uiState.value as AssessmentUiState.Completed
        assertEquals(Pole.BALANCE, state.model.dominantPole)
        assertTrue(state.model.isLowConfidence)
        assertEquals(ConfidenceLevel.LOW, state.model.confidence)
        assertFalse(state.model.hasNextSephira)
        assertEquals(null, state.model.nextSephiraName)
        verify(exactly = 1) { appTelemetry.trackAssessmentCompleted(1) }
    }

    @Test
    fun continueAssessment_onLastQuestionWithNextSection_showsCurrentSephiraResultBeforeAdvance() = coroutinesRule.runBlockingTest {
        val questionnaire = questionnaireWithTwoSections()
        val completedMalkuthSnapshot = testSnapshot(
            questionnaireVersion = "tree-v1",
            totalQuestions = 1,
            responses = listOf(
                SavedResponse("q_last", "agree", 3, 0, 1L)
            )
        )
        val savedScore = SephiraScore(
            sessionId = 1L,
            sephiraId = SephiraId.MALKUTH,
            balanceScore = 0.30,
            deficiencyScore = 0.20,
            excessScore = 0.50,
            dominantPole = Pole.EXCESS,
            confidence = ConfidenceLevel.MEDIUM,
            isLowConfidence = false
        )
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(questionnaire)
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(false)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(completedMalkuthSnapshot)
        every {
            saveAssessmentAnswerUseCase.run(
                SaveAnswerParams(
                    sessionId = 1L,
                    questionId = "q_last",
                    selectedOptionId = "agree",
                    numericValue = 3,
                    questionOrder = 0,
                    nextPageIndex = 0,
                    nextQuestionIndex = 0
                )
            )
        } returns flowOf(completedMalkuthSnapshot)
        every { assessmentScoringEngine.score(any<ScoreInput>(), 1L) } returns savedScore
        every { saveAssessmentScoreUseCase.run(1L to savedScore) } returns flowOf(
            completedMalkuthSnapshot.copy(scores = listOf(savedScore))
        )

        val viewModel = createViewModel(locale = Locale.ENGLISH)
        viewModel.startAssessment()
        viewModel.continueAssessment()

        val state = viewModel.uiState.value as AssessmentUiState.Completed
        assertEquals("Malkuth", state.model.sephiraName)
        assertTrue(state.model.hasNextSephira)
        assertEquals("Yesod", state.model.nextSephiraName)
        verify(exactly = 1) { saveAssessmentScoreUseCase.run(1L to savedScore) }
        verify(exactly = 0) { advanceAssessmentSectionUseCase.run(any()) }
    }

    @Test
    fun continueFromCompletedResult_advancesToNextSectionIntro() = coroutinesRule.runBlockingTest {
        val questionnaire = questionnaireWithTwoSections()
        val completedMalkuthSnapshot = testSnapshot(
            questionnaireVersion = "tree-v1",
            totalQuestions = 1,
            responses = listOf(
                SavedResponse("q_last", "agree", 3, 0, 1L)
            )
        )
        val savedScore = SephiraScore(
            sessionId = 1L,
            sephiraId = SephiraId.MALKUTH,
            balanceScore = 0.30,
            deficiencyScore = 0.20,
            excessScore = 0.50,
            dominantPole = Pole.EXCESS,
            confidence = ConfidenceLevel.MEDIUM,
            isLowConfidence = false
        )
        val advancedSnapshot = testSnapshot(
            questionnaireVersion = "tree-v1",
            totalQuestions = 1,
            currentSephiraId = SephiraId.YESOD,
            responses = listOf(
                SavedResponse("q_last", "agree", 3, 0, 1L)
            ),
            scores = listOf(savedScore)
        )
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(questionnaire)
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(false)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(completedMalkuthSnapshot)
        every { assessmentScoringEngine.score(any<ScoreInput>(), 1L) } returns savedScore
        every { saveAssessmentScoreUseCase.run(1L to savedScore) } returns flowOf(
            completedMalkuthSnapshot.copy(scores = listOf(savedScore))
        )
        every {
            saveAssessmentAnswerUseCase.run(
                SaveAnswerParams(
                    sessionId = 1L,
                    questionId = "q_last",
                    selectedOptionId = "agree",
                    numericValue = 3,
                    questionOrder = 0,
                    nextPageIndex = 0,
                    nextQuestionIndex = 0
                )
            )
        } returns flowOf(completedMalkuthSnapshot)
        every {
            advanceAssessmentSectionUseCase.run(
                AdvanceAssessmentSectionParams(
                    sessionId = 1L,
                    sephiraId = SephiraId.YESOD,
                    totalQuestions = 1
                )
            )
        } returns flowOf(advancedSnapshot)

        val viewModel = createViewModel(locale = Locale.ENGLISH)
        viewModel.startAssessment()
        viewModel.continueAssessment()
        viewModel.continueFromCompletedResult()

        val state = viewModel.uiState.value as AssessmentUiState.Intro
        assertEquals("Yesod", state.model.sephiraName)
        assertFalse(state.model.isResumeSession)
    }

    @Test
    fun init_whenCurrentSectionHasNoOwnProgress_doesNotShowResumeLabel() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(questionnaireWithTwoSections())
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(false)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(
            testSnapshot(
                questionnaireVersion = "tree-v1",
                currentSephiraId = SephiraId.YESOD,
                responses = listOf(
                    SavedResponse("q_last", "agree", 3, 0, 1L)
                )
            )
        )

        val viewModel = createViewModel()
        val state = viewModel.uiState.value as AssessmentUiState.Intro

        assertEquals("Yesod", state.model.sephiraName)
        assertFalse(state.model.isResumeSession)
    }

    @Test
    fun goBack_updatesProgressToPreviousQuestion() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(false)
        every { startOrResumeAssessmentUseCase.run(any()) } returns flowOf(
            testSnapshot(currentQuestionIndex = 1)
        )
        every {
            updateAssessmentProgressUseCase.run(
                UpdateAssessmentProgressParams(
                    sessionId = 1L,
                    pageIndex = 0,
                    questionIndex = 0
                )
            )
        } returns flowOf(testSnapshot(currentQuestionIndex = 0))

        val viewModel = createViewModel()
        viewModel.goBack()

        val state = viewModel.uiState.value as AssessmentUiState.Question
        verify(exactly = 1) {
            updateAssessmentProgressUseCase.run(
                UpdateAssessmentProgressParams(
                    sessionId = 1L,
                    pageIndex = 0,
                    questionIndex = 0
                )
            )
        }
        assertEquals("Q1", state.model.currentQuestionPrompt)
    }

    private fun createViewModel(locale: Locale = Locale.ENGLISH): AssessmentViewModel {
        every { currentLocaleProvider.current() } returns locale
        return AssessmentViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            getAssessmentHonestyNoticeVisibilityUseCase = getAssessmentHonestyNoticeVisibilityUseCase,
            setAssessmentHonestyNoticeVisibilityUseCase = setAssessmentHonestyNoticeVisibilityUseCase,
            startOrResumeAssessmentUseCase = startOrResumeAssessmentUseCase,
            saveAssessmentAnswerUseCase = saveAssessmentAnswerUseCase,
            updateAssessmentProgressUseCase = updateAssessmentProgressUseCase,
            saveAssessmentScoreUseCase = saveAssessmentScoreUseCase,
            advanceAssessmentSectionUseCase = advanceAssessmentSectionUseCase,
            completeAssessmentUseCase = completeAssessmentUseCase,
            assessmentScoringEngine = assessmentScoringEngine,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry,
            savedStateHandle = SavedStateHandle()
        )
    }

    private fun testQuestionnaire(): QuestionnaireContent {
        return QuestionnaireContent(
            version = "malkuth-v1",
            title = "Malkuth reflection",
            responseScale = ResponseScaleDefinition(
                format = QuestionFormat.LIKERT_5,
                options = listOf(
                    AnswerOption("disagree", "Disagree", 1),
                    AnswerOption("agree", "Agree", 3)
                )
            ),
            sections = listOf(
                SephiraSectionContent(
                    sephiraId = SephiraId.MALKUTH,
                    displayName = "Malkuth",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    detailContent = testDetailContent(),
                    pages = listOf(
                        QuestionPageContent("page_1", "Money", "Resources", listOf("q1", "q2", "q3")),
                        QuestionPageContent("page_2", "Body", "Habits", listOf("q4", "q5", "q6"))
                    ),
                    questions = listOf(
                        QuestionContent("q1", SephiraId.MALKUTH, "page_1", "Q1", QuestionFormat.LIKERT_5, Pole.EXCESS),
                        QuestionContent("q2", SephiraId.MALKUTH, "page_1", "Q2", QuestionFormat.LIKERT_5, Pole.DEFICIENCY),
                        QuestionContent("q3", SephiraId.MALKUTH, "page_1", "Q3", QuestionFormat.LIKERT_5, Pole.BALANCE),
                        QuestionContent("q4", SephiraId.MALKUTH, "page_2", "Q4", QuestionFormat.LIKERT_5, Pole.EXCESS),
                        QuestionContent("q5", SephiraId.MALKUTH, "page_2", "Q5", QuestionFormat.LIKERT_5, Pole.DEFICIENCY),
                        QuestionContent("q6", SephiraId.MALKUTH, "page_2", "Q6", QuestionFormat.LIKERT_5, Pole.BALANCE)
                    )
                )
            )
        )
    }

    private fun singleQuestionnaire(): QuestionnaireContent {
        return QuestionnaireContent(
            version = "single-v1",
            title = "Malkuth reflection",
            responseScale = ResponseScaleDefinition(
                format = QuestionFormat.LIKERT_5,
                options = listOf(AnswerOption("agree", "Agree", 3))
            ),
            sections = listOf(
                SephiraSectionContent(
                    sephiraId = SephiraId.MALKUTH,
                    displayName = "Malkuth",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    detailContent = testDetailContent(),
                    pages = listOf(QuestionPageContent("page_1", "Money", "Resources", listOf("q_last"))),
                    questions = listOf(
                        QuestionContent("q_last", SephiraId.MALKUTH, "page_1", "Q Last", QuestionFormat.LIKERT_5, Pole.BALANCE)
                    )
                )
            )
        )
    }

    private fun questionnaireWithTwoSections(): QuestionnaireContent {
        return QuestionnaireContent(
            version = "tree-v1",
            title = "Tree of Life reflection",
            responseScale = ResponseScaleDefinition(
                format = QuestionFormat.LIKERT_5,
                options = listOf(AnswerOption("agree", "Agree", 3))
            ),
            sections = listOf(
                SephiraSectionContent(
                    sephiraId = SephiraId.MALKUTH,
                    displayName = "Malkuth",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    detailContent = testDetailContent(),
                    pages = listOf(QuestionPageContent("page_1", "Money", "Resources", listOf("q_last"))),
                    questions = listOf(
                        QuestionContent("q_last", SephiraId.MALKUTH, "page_1", "Q Last", QuestionFormat.LIKERT_5, Pole.BALANCE)
                    )
                ),
                SephiraSectionContent(
                    sephiraId = SephiraId.YESOD,
                    displayName = "Yesod",
                    shortMeaning = "Foundation",
                    introText = "Yesod intro",
                    detailContent = testDetailContent(),
                    pages = listOf(QuestionPageContent("yesod_page_1", "Bonding", "Connection", listOf("yesod_q1"))),
                    questions = listOf(
                        QuestionContent("yesod_q1", SephiraId.YESOD, "yesod_page_1", "Yesod Q1", QuestionFormat.LIKERT_5, Pole.BALANCE)
                    )
                )
            )
        )
    }

    private fun testSnapshot(
        questionnaireVersion: String = "malkuth-v1",
        totalQuestions: Int = 6,
        currentSephiraId: SephiraId = SephiraId.MALKUTH,
        currentPageIndex: Int = 0,
        currentQuestionIndex: Int = 0,
        responses: List<SavedResponse> = emptyList(),
        scores: List<SephiraScore> = emptyList()
    ): AssessmentSessionSnapshot {
        return AssessmentSessionSnapshot(
            sessionId = 1L,
            questionnaireVersion = questionnaireVersion,
            status = AssessmentStatus.IN_PROGRESS,
            currentSephiraId = currentSephiraId,
            currentPageIndex = currentPageIndex,
            currentQuestionIndex = currentQuestionIndex,
            totalQuestions = totalQuestions,
            startedAt = 1L,
            completedAt = null,
            responses = responses,
            scores = scores
        )
    }

    private fun testDetailContent() = SephiraDetailContent(
        healthyExpression = "Healthy",
        deficiencyPattern = "Deficiency",
        excessPattern = "Excess",
        suggestedPractices = listOf("Practice")
    )
}
