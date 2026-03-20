package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AssessmentContentRepository
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentContentUseCaseTests {

    private lateinit var assessmentContentRepository: AssessmentContentRepository
    private lateinit var getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        assessmentContentRepository = mockk(relaxed = true)
        getCurrentQuestionnaireUseCase = GetCurrentQuestionnaireUseCase(assessmentContentRepository)
    }

    @Test
    fun getCurrentQuestionnaireUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = QuestionnaireContent(
            version = "test",
            title = "Test questionnaire",
            responseScale = ResponseScaleDefinition(
                format = QuestionFormat.LIKERT_5,
                options = emptyList()
            ),
            sections = emptyList()
        )
        every {
            assessmentContentRepository.getCurrentQuestionnaire(Locale.ENGLISH)
        } returns flowOf(expected)

        val result = getCurrentQuestionnaireUseCase.run(Locale.ENGLISH).toList()

        verify(exactly = 1) {
            assessmentContentRepository.getCurrentQuestionnaire(Locale.ENGLISH)
        }
        assertEquals(listOf(expected), result)
    }
}
