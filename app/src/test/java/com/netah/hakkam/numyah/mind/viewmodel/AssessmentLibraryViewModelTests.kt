package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.CompletionPoleContent
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SephiraCompletionContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraDetailContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentLibraryViewModelTests {

    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase
    private lateinit var observeActiveAssessmentUseCase: ObserveActiveAssessmentUseCase
    private lateinit var currentLocaleProvider: CurrentLocaleProvider

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getCurrentQuestionnaireUseCase = mockk(relaxed = true)
        observeActiveAssessmentUseCase = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
    }

    @Test
    fun init_withoutActiveAssessment_exposesReadyEntry() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeActiveAssessmentUseCase.run() } returns flowOf(null)

        val viewModel = AssessmentLibraryViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeActiveAssessmentUseCase = observeActiveAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        val state = viewModel.uiState.value as AssessmentLibraryUiState.Loaded

        assertEquals("Tree of Life reflection", state.model.entry.title)
        assertEquals(2, state.model.entry.sephiraCount)
        assertEquals(null, state.model.entry.activeAssessment)
    }

    @Test
    fun init_withActiveAssessment_exposesResumeContext() = coroutinesRule.runBlockingTest {
        every { getCurrentQuestionnaireUseCase.run(Locale.ENGLISH) } returns flowOf(testQuestionnaire())
        every { observeActiveAssessmentUseCase.run() } returns flowOf(
            AssessmentSessionSnapshot(
                sessionId = 2L,
                questionnaireVersion = "tree-v1",
                status = AssessmentStatus.IN_PROGRESS,
                currentSephiraId = SephiraId.YESOD,
                currentPageIndex = 0,
                currentQuestionIndex = 1,
                totalQuestions = 6,
                startedAt = 1L,
                completedAt = null,
                responses = emptyList(),
                scores = emptyList()
            )
        )

        val viewModel = AssessmentLibraryViewModel(
            getCurrentQuestionnaireUseCase = getCurrentQuestionnaireUseCase,
            observeActiveAssessmentUseCase = observeActiveAssessmentUseCase,
            currentLocaleProvider = currentLocaleProvider
        )

        val state = viewModel.uiState.value as AssessmentLibraryUiState.Loaded

        assertEquals("Yesod", state.model.entry.activeAssessment?.sephiraName)
        assertEquals(2, state.model.entry.activeAssessment?.currentQuestionNumber)
    }

    private fun testQuestionnaire() = QuestionnaireContent(
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

    private fun testSection(sephiraId: SephiraId, name: String) = SephiraSectionContent(
        sephiraId = sephiraId,
        displayName = name,
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
            QuestionPageContent(
                id = "${sephiraId.name.lowercase()}_page",
                title = "Page",
                description = "Description",
                questionIds = listOf("q1", "q2", "q3")
            ),
            QuestionPageContent(
                id = "${sephiraId.name.lowercase()}_page_2",
                title = "Page 2",
                description = "Description 2",
                questionIds = listOf("q4", "q5", "q6")
            )
        ),
        questions = emptyList()
    )

    private fun testCompletionContent() = SephiraCompletionContent(
        sectionSummary = "Completion summary",
        balanced = CompletionPoleContent("Balanced reflection", "Balanced practice"),
        deficiency = CompletionPoleContent("Deficiency reflection", "Deficiency practice"),
        excess = CompletionPoleContent("Excess reflection", "Excess practice")
    )
}
