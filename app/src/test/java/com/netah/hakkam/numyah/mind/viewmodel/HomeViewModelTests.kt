package com.netah.hakkam.numyah.mind.viewmodel

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
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
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
class HomeViewModelTests {

    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase
    private lateinit var observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase
    private lateinit var currentLocaleProvider: CurrentLocaleProvider

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getCurrentQuestionnaireUseCase = mockk(relaxed = true)
        observeLatestCompletedAssessmentUseCase = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
    }

    @Test
    fun init_withoutLatestAssessment_emitsEmpty() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(null)

        val viewModel = HomeViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        assertTrue(viewModel.uiState.value is HomeUiState.Empty)
    }

    @Test
    fun init_withLatestAssessment_buildsReflectionSummary() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(testSnapshot())

        val viewModel = HomeViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        val state = viewModel.uiState.value as HomeUiState.Loaded

        assertEquals("Yesod", state.model.needsAttentionSephiraName)
        assertEquals("Malkuth", state.model.mostBalancedSephiraName)
        assertEquals(Pole.DEFICIENCY, state.model.currentFocus.dominantPole)
        assertEquals("Yesod", state.model.currentFocus.sephiraName)
        assertTrue(state.model.daysSinceLastAssessment >= 0)
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
        detailContent = SephiraDetailContent(
            healthyExpression = "Healthy",
            deficiencyPattern = "Deficiency",
            excessPattern = "Excess",
            suggestedPractices = listOf("Practice")
        ),
        pages = listOf(
            QuestionPageContent("${sephiraId.name.lowercase()}_page", "Title", "Body", listOf("q1"))
        ),
        questions = emptyList()
    )

    private fun testSnapshot() = AssessmentSessionSnapshot(
        sessionId = 7L,
        questionnaireVersion = "tree-v1",
        status = AssessmentStatus.COMPLETED,
        currentSephiraId = SephiraId.YESOD,
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
                balanceScore = 0.25,
                deficiencyScore = 0.50,
                excessScore = 0.25,
                dominantPole = Pole.DEFICIENCY,
                confidence = ConfidenceLevel.MEDIUM,
                isLowConfidence = false
            )
        )
    )
}
