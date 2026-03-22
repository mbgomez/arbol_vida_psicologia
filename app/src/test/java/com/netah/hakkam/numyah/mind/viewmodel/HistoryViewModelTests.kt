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
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveAssessmentHistoryUseCase
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
class HistoryViewModelTests {

    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase
    private lateinit var observeAssessmentHistoryUseCase: ObserveAssessmentHistoryUseCase
    private lateinit var currentLocaleProvider: CurrentLocaleProvider

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getCurrentQuestionnaireUseCase = mockk(relaxed = true)
        observeAssessmentHistoryUseCase = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
    }

    @Test
    fun init_withoutSavedHistory_emitsEmptyState() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeAssessmentHistoryUseCase.run() } returns flowOf(emptyList())

        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value is HistoryUiState.Empty)
    }

    @Test
    fun init_withSavedHistory_buildsSessionCards() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeAssessmentHistoryUseCase.run() } returns flowOf(
            listOf(
                testCompletedSnapshot(
                    sessionId = 10L,
                    completedAt = 200L,
                    scores = listOf(
                        testScore(10L, SephiraId.MALKUTH, 0.60, 0.20, 0.20),
                        testScore(10L, SephiraId.YESOD, 0.25, 0.45, 0.30)
                    )
                )
            )
        )

        val viewModel = createViewModel()
        val state = viewModel.uiState.value as HistoryUiState.Loaded

        assertEquals(1, state.model.totalSessions)
        assertEquals(10L, state.model.sessions.first().sessionId)
        assertEquals("Yesod", state.model.sessions.first().needsAttentionSephiraName)
        assertEquals("Malkuth", state.model.sessions.first().mostBalancedSephiraName)
        assertEquals(75, state.model.sessions.first().needsAttentionImbalancePercent)
        assertEquals(60, state.model.sessions.first().mostBalancedBalancePercent)
    }

    private fun createViewModel() = HistoryViewModel(
        getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
        observeAssessmentHistoryUseCase = observeAssessmentHistoryUseCase,
        currentLocaleProvider = currentLocaleProvider
    )

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
                    pages = listOf(QuestionPageContent("malkuth_page", "Title", "Body", listOf("m1"))),
                    questions = emptyList()
                ),
                SephiraSectionContent(
                    sephiraId = SephiraId.YESOD,
                    displayName = "Yesod",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    pages = listOf(QuestionPageContent("yesod_page", "Title", "Body", listOf("y1"))),
                    questions = emptyList()
                )
            )
        )
    }

    private fun testCompletedSnapshot(
        sessionId: Long,
        completedAt: Long,
        scores: List<SephiraScore>
    ): AssessmentSessionSnapshot {
        return AssessmentSessionSnapshot(
            sessionId = sessionId,
            questionnaireVersion = "tree-v1",
            status = AssessmentStatus.COMPLETED,
            currentSephiraId = SephiraId.YESOD,
            currentPageIndex = 0,
            currentQuestionIndex = 0,
            totalQuestions = 6,
            startedAt = 100L,
            completedAt = completedAt,
            responses = emptyList(),
            scores = scores
        )
    }

    private fun testScore(
        sessionId: Long,
        sephiraId: SephiraId,
        balanceScore: Double,
        deficiencyScore: Double,
        excessScore: Double
    ) = SephiraScore(
        sessionId = sessionId,
        sephiraId = sephiraId,
        balanceScore = balanceScore,
        deficiencyScore = deficiencyScore,
        excessScore = excessScore,
        dominantPole = Pole.BALANCE,
        confidence = ConfidenceLevel.HIGH,
        isLowConfidence = false
    )
}
