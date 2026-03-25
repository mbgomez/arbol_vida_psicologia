package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.observability.ResultsSessionScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.CompletionPoleContent
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SephiraCompletionContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraDetailContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveCompletedAssessmentByIdUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SephiraDetailViewModelTests {

    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase
    private lateinit var observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase
    private lateinit var observeCompletedAssessmentByIdUseCase: ObserveCompletedAssessmentByIdUseCase
    private lateinit var currentLocaleProvider: CurrentLocaleProvider
    private lateinit var appTelemetry: AppTelemetry

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getCurrentQuestionnaireUseCase = mockk(relaxed = true)
        observeLatestCompletedAssessmentUseCase = mockk(relaxed = true)
        observeCompletedAssessmentByIdUseCase = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        appTelemetry = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
    }

    @Test
    fun init_withMatchingSavedScore_emitsLoadedDetail() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(testSnapshot())

        val viewModel = createViewModel(
            SavedStateHandle(
                mapOf(AppDestination.ResultsDetail.sephiraIdArg to SephiraId.YESOD.name)
            )
        )

        val state = viewModel.uiState.value as SephiraDetailUiState.Loaded

        assertEquals("Yesod", state.model.sephiraName)
        assertEquals(46, state.model.deficiencyPercent)
        assertEquals("Healthy Yesod", state.model.healthyExpression)
        assertEquals(listOf("Practice one", "Practice two"), state.model.suggestedPractices)
        verify(exactly = 1) {
            appTelemetry.trackResultsDetailOpened(SephiraId.YESOD, ResultsSessionScope.LATEST)
        }
    }

    @Test
    fun init_withMissingScore_emitsNotFound() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeLatestCompletedAssessmentUseCase.run() } returns flowOf(
            testSnapshot().copy(scores = emptyList())
        )

        val viewModel = createViewModel(
            SavedStateHandle(
                mapOf(AppDestination.ResultsDetail.sephiraIdArg to SephiraId.YESOD.name)
            )
        )

        assertTrue(viewModel.uiState.value is SephiraDetailUiState.NotFound)
    }

    private fun createViewModel(savedStateHandle: SavedStateHandle): SephiraDetailViewModel {
        return SephiraDetailViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeLatestCompletedAssessmentUseCase = observeLatestCompletedAssessmentUseCase,
            observeCompletedAssessmentByIdUseCase = observeCompletedAssessmentByIdUseCase,
            savedStateHandle = savedStateHandle,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
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
                    sephiraId = SephiraId.YESOD,
                    displayName = "Yesod",
                    shortMeaning = "Relational foundation",
                    introText = "Intro",
                    completionContent = testCompletionContent(),
                    detailContent = SephiraDetailContent(
                        healthyExpression = "Healthy Yesod",
                        deficiencyPattern = "Deficient Yesod",
                        excessPattern = "Excessive Yesod",
                        suggestedPractices = listOf("Practice one", "Practice two")
                    ),
                    pages = listOf(QuestionPageContent("yesod_page", "Title", "Body", listOf("q1"))),
                    questions = emptyList()
                )
            )
        )
    }

    private fun testCompletionContent() = SephiraCompletionContent(
        sectionSummary = "Completion summary",
        balanced = CompletionPoleContent("Balanced reflection", "Balanced practice"),
        deficiency = CompletionPoleContent("Deficiency reflection", "Deficiency practice"),
        excess = CompletionPoleContent("Excess reflection", "Excess practice")
    )

    private fun testSnapshot() = AssessmentSessionSnapshot(
        sessionId = 4L,
        questionnaireVersion = "tree-v1",
        status = AssessmentStatus.COMPLETED,
        currentSephiraId = SephiraId.YESOD,
        currentPageIndex = 0,
        currentQuestionIndex = 0,
        totalQuestions = 6,
        startedAt = 10L,
        completedAt = 20L,
        responses = emptyList(),
        scores = listOf(
            SephiraScore(
                sessionId = 4L,
                sephiraId = SephiraId.YESOD,
                balanceScore = 0.31,
                deficiencyScore = 0.46,
                excessScore = 0.23,
                dominantPole = Pole.DEFICIENCY,
                confidence = ConfidenceLevel.MEDIUM,
                isLowConfidence = false
            )
        )
    )
}
