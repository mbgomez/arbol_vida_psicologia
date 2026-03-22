package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SephiraDetailContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveCompletedAssessmentByIdUseCase
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import io.mockk.every
import io.mockk.mockk
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResultsViewModelTests {

    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase
    private lateinit var observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase
    private lateinit var observeCompletedAssessmentByIdUseCase: ObserveCompletedAssessmentByIdUseCase
    private lateinit var currentLocaleProvider: CurrentLocaleProvider

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getCurrentQuestionnaireUseCase = mockk(relaxed = true)
        observeLatestCompletedAssessmentUseCase = mockk(relaxed = true)
        observeCompletedAssessmentByIdUseCase = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
    }

    @Test
    fun init_withoutCompletedAssessment_emitsEmptyState() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(null)

        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value is ResultsUiState.Empty)
    }

    @Test
    fun init_withCompletedAssessment_buildsRankedOverview() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(testCompletedSnapshot())

        val viewModel = createViewModel()
        val state = viewModel.uiState.value as ResultsUiState.Loaded

        assertEquals(3, state.model.completedCount)
        assertEquals(3, state.model.totalCount)
        assertEquals(false, state.model.isHistoricalSession)
        assertEquals("Hod", state.model.needsAttention?.sephiraName)
        assertEquals("Malkuth", state.model.mostBalanced?.sephiraName)
        assertEquals(listOf("Hod", "Yesod", "Malkuth"), state.model.sephirot.map { it.sephiraName })
        assertEquals(40, state.model.mostBalanced?.imbalancePercent)
        assertEquals(120, state.model.needsAttention?.imbalancePercent)
        assertEquals(60, state.model.mostBalanced?.balancePercent)
    }

    @Test
    fun init_withSelectedSessionId_observesRequestedSavedAssessment() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeCompletedAssessmentByIdUseCase.run(7L) } returns flowOf(testCompletedSnapshot())

        val viewModel = createViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.Results.sessionIdArg to 7L)
            )
        )

        val state = viewModel.uiState.value as ResultsUiState.Loaded

        assertEquals(true, state.model.isHistoricalSession)
        assertEquals("Hod", state.model.needsAttention?.sephiraName)
    }

    private fun createViewModel(
        locale: Locale = Locale.ENGLISH,
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ): ResultsViewModel {
        every { currentLocaleProvider.current() } returns locale
        return ResultsViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            observeCompletedAssessmentByIdUseCase = observeCompletedAssessmentByIdUseCase,
            savedStateHandle = savedStateHandle,
            currentLocaleProvider = currentLocaleProvider
        )
    }

    private fun testQuestionnaire(): QuestionnaireContent {
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
                    pages = listOf(QuestionPageContent("malkuth_page", "Title", "Body", listOf("m1"))),
                    questions = emptyList()
                ),
                SephiraSectionContent(
                    sephiraId = SephiraId.YESOD,
                    displayName = "Yesod",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    detailContent = testDetailContent(),
                    pages = listOf(QuestionPageContent("yesod_page", "Title", "Body", listOf("y1"))),
                    questions = emptyList()
                ),
                SephiraSectionContent(
                    sephiraId = SephiraId.HOD,
                    displayName = "Hod",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    detailContent = testDetailContent(),
                    pages = listOf(QuestionPageContent("hod_page", "Title", "Body", listOf("h1"))),
                    questions = emptyList()
                )
            )
        )
    }

    private fun testCompletedSnapshot(): AssessmentSessionSnapshot {
        return AssessmentSessionSnapshot(
            sessionId = 7L,
            questionnaireVersion = "tree-v1",
            status = AssessmentStatus.COMPLETED,
            currentSephiraId = SephiraId.HOD,
            currentPageIndex = 0,
            currentQuestionIndex = 0,
            totalQuestions = 6,
            startedAt = 1L,
            completedAt = 2L,
            responses = emptyList(),
            scores = listOf(
                SephiraScore(
                    sessionId = 7L,
                    sephiraId = SephiraId.MALKUTH,
                    balanceScore = 0.60,
                    deficiencyScore = 0.20,
                    excessScore = 0.20,
                    dominantPole = Pole.BALANCE,
                    confidence = ConfidenceLevel.HIGH,
                    isLowConfidence = false
                ),
                SephiraScore(
                    sessionId = 7L,
                    sephiraId = SephiraId.YESOD,
                    balanceScore = 0.35,
                    deficiencyScore = 0.45,
                    excessScore = 0.30,
                    dominantPole = Pole.DEFICIENCY,
                    confidence = ConfidenceLevel.MEDIUM,
                    isLowConfidence = false
                ),
                SephiraScore(
                    sessionId = 7L,
                    sephiraId = SephiraId.HOD,
                    balanceScore = 0.20,
                    deficiencyScore = 0.70,
                    excessScore = 0.50,
                    dominantPole = Pole.DEFICIENCY,
                    confidence = ConfidenceLevel.HIGH,
                    isLowConfidence = false
                )
            )
        )
    }

    private fun testDetailContent() = SephiraDetailContent(
        healthyExpression = "Healthy",
        deficiencyPattern = "Deficiency",
        excessPattern = "Excess",
        suggestedPractices = listOf("Practice")
    )
}
