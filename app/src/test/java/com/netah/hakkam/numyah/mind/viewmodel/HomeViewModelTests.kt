package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.CompletionPoleContent
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.QuestionContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SephiraCompletionContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraDetailContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTests {

    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase
    private lateinit var observeActiveAssessmentUseCase: ObserveActiveAssessmentUseCase
    private lateinit var observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase
    private lateinit var currentLocaleProvider: CurrentLocaleProvider

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getCurrentQuestionnaireUseCase = mockk(relaxed = true)
        observeActiveAssessmentUseCase = mockk(relaxed = true)
        observeLatestCompletedAssessmentUseCase = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
    }

    @Test
    fun init_withoutLatestAssessment_emitsEmpty() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeActiveAssessmentUseCase.run() } returns flowOf(null)
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(null)

        val viewModel = HomeViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeActiveAssessmentUseCase = observeActiveAssessmentUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        assertTrue(viewModel.uiState.value is HomeUiState.Empty)
    }

    @Test
    fun init_withLatestAssessment_buildsReflectionSummary() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeActiveAssessmentUseCase.run() } returns flowOf(null)
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(testSnapshot())

        val viewModel = HomeViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeActiveAssessmentUseCase = observeActiveAssessmentUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        val state = viewModel.uiState.value as HomeUiState.Loaded

        assertEquals("Yesod", state.model.latestReflection?.needsAttentionSephiraName)
        assertEquals("Malkuth", state.model.latestReflection?.mostBalancedSephiraName)
        assertEquals(Pole.DEFICIENCY, state.model.latestReflection?.currentFocus?.dominantPole)
        assertEquals("Yesod", state.model.latestReflection?.currentFocus?.sephiraName)
        assertTrue((state.model.latestReflection?.daysSinceLastAssessment ?: -1) >= 0)
    }

    @Test
    fun init_withActiveAssessment_buildsResumeSummaryAlongsideLatestReflection() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every {
            observeActiveAssessmentUseCase.run()
        } returns flowOf(
            testSnapshot(
                currentSephiraId = SephiraId.YESOD,
                currentQuestionIndex = 1,
                scores = listOf(
                    SephiraScore(
                        sessionId = 9L,
                        sephiraId = SephiraId.MALKUTH,
                        balanceScore = 0.62,
                        deficiencyScore = 0.18,
                        excessScore = 0.20,
                        dominantPole = Pole.BALANCE,
                        confidence = ConfidenceLevel.HIGH,
                        isLowConfidence = false
                    )
                )
            )
        )
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(testSnapshot())

        val viewModel = HomeViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeActiveAssessmentUseCase = observeActiveAssessmentUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        val state = viewModel.uiState.value as HomeUiState.Loaded

        assertEquals("Yesod", state.model.activeAssessment?.sephiraName)
        assertEquals(2, state.model.activeAssessment?.currentQuestionNumber)
        assertEquals(1, state.model.activeAssessment?.completedSephirotCount)
        assertEquals("Yesod", state.model.latestReflection?.needsAttentionSephiraName)
    }

    @Test
    fun retry_afterInitialFailure_reloadsHomeSummary() = coroutinesRule.runBlockingTest {
        every {
            getCurrentQuestionnaireUseCase.run(Locale.ENGLISH)
        } returnsMany listOf(
            flow { throw IllegalStateException("boom") },
            flowOf(testQuestionnaire())
        )
        every { observeActiveAssessmentUseCase.run() } returns flowOf(null)
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(testSnapshot())

        val viewModel = HomeViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeActiveAssessmentUseCase = observeActiveAssessmentUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        assertTrue(viewModel.uiState.value is HomeUiState.Error)

        viewModel.retry()

        assertTrue(viewModel.uiState.value is HomeUiState.Loaded)
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
                testSection(SephiraId.MALKUTH, "Malkuth"),
                testSection(SephiraId.YESOD, "Yesod")
            )
        )
    }

    private fun testSection(sephiraId: SephiraId, displayName: String) = SephiraSectionContent(
        sephiraId = sephiraId,
        displayName = displayName,
        shortMeaning = "Meaning",
        introText = "Intro",
        completionContent = testCompletionContent(),
        detailContent = SephiraDetailContent(
            healthyExpression = "Healthy",
            deficiencyPattern = "Deficiency",
            excessPattern = "Excess",
            suggestedPractices = listOf("Practice")
        ),
        pages = listOf(
            QuestionPageContent("${sephiraId.name.lowercase()}_page", "Title", "Body", listOf("q1", "q2", "q3"))
        ),
        questions = listOf(
            QuestionContent("q1", sephiraId, "${sephiraId.name.lowercase()}_page", "Q1", QuestionFormat.LIKERT_5, Pole.BALANCE),
            QuestionContent("q2", sephiraId, "${sephiraId.name.lowercase()}_page", "Q2", QuestionFormat.LIKERT_5, Pole.BALANCE),
            QuestionContent("q3", sephiraId, "${sephiraId.name.lowercase()}_page", "Q3", QuestionFormat.LIKERT_5, Pole.BALANCE)
        )
    )

    private fun testCompletionContent() = SephiraCompletionContent(
        sectionSummary = "Completion summary",
        balanced = CompletionPoleContent("Balanced reflection", "Balanced practice"),
        deficiency = CompletionPoleContent("Deficiency reflection", "Deficiency practice"),
        excess = CompletionPoleContent("Excess reflection", "Excess practice")
    )

    private fun testSnapshot(
        status: AssessmentStatus = AssessmentStatus.COMPLETED,
        currentSephiraId: SephiraId = SephiraId.YESOD,
        currentQuestionIndex: Int = 0,
        scores: List<SephiraScore> = listOf(
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
                balanceScore = 0.25,
                deficiencyScore = 0.50,
                excessScore = 0.25,
                dominantPole = Pole.DEFICIENCY,
                confidence = ConfidenceLevel.MEDIUM,
                isLowConfidence = false
            )
        )
    ) = AssessmentSessionSnapshot(
        sessionId = 7L,
        questionnaireVersion = "tree-v1",
        status = status,
        currentSephiraId = currentSephiraId,
        currentPageIndex = 0,
        currentQuestionIndex = currentQuestionIndex,
        totalQuestions = 6,
        startedAt = 1L,
        completedAt = if (status == AssessmentStatus.COMPLETED) 2L else null,
        responses = emptyList(),
        scores = scores
    )
}
