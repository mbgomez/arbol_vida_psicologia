package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.LearningContentRepository
import com.netah.hakkam.numyah.mind.domain.model.LearningCatalog
import com.netah.hakkam.numyah.mind.domain.model.LearningCourse
import com.netah.hakkam.numyah.mind.domain.model.LearningSection
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
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

@OptIn(ExperimentalCoroutinesApi::class)
class LearningContentUseCaseTests {

    private lateinit var learningContentRepository: LearningContentRepository
    private lateinit var getLearningCatalogUseCase: GetLearningCatalogUseCase
    private lateinit var getLearningCourseUseCase: GetLearningCourseUseCase
    private lateinit var getLearningSectionUseCase: GetLearningSectionUseCase

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        learningContentRepository = mockk(relaxed = true)
        getLearningCatalogUseCase = GetLearningCatalogUseCase(learningContentRepository)
        getLearningCourseUseCase = GetLearningCourseUseCase(learningContentRepository)
        getLearningSectionUseCase = GetLearningSectionUseCase(learningContentRepository)
    }

    @Test
    fun getLearningCatalogUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = LearningCatalog(version = "v1", title = "Courses", courses = emptyList())
        every { learningContentRepository.getCatalog(Locale.ENGLISH) } returns flowOf(expected)

        val result = getLearningCatalogUseCase.run(Locale.ENGLISH).toList()

        verify(exactly = 1) { learningContentRepository.getCatalog(Locale.ENGLISH) }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun getLearningCourseUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = LearningCourse(
            id = "tree",
            title = "Tree",
            subtitle = "Subtitle",
            description = "Description",
            estimatedMinutes = 24,
            totalSectionCount = 11,
            sections = emptyList()
        )
        val params = GetLearningCourseParams("tree", Locale.ENGLISH)
        every { learningContentRepository.getCourse("tree", Locale.ENGLISH) } returns flowOf(expected)

        val result = getLearningCourseUseCase.run(params).toList()

        verify(exactly = 1) { learningContentRepository.getCourse("tree", Locale.ENGLISH) }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun getLearningSectionUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = LearningSection(
            id = "intro",
            title = "Introduction",
            summary = "Summary",
            readingTimeMinutes = 8,
            order = 1,
            content = listOf("One paragraph")
        )
        val params = GetLearningSectionParams("tree", "intro", Locale.ENGLISH)
        every {
            learningContentRepository.getSection("tree", "intro", Locale.ENGLISH)
        } returns flowOf(expected)

        val result = getLearningSectionUseCase.run(params).toList()

        verify(exactly = 1) {
            learningContentRepository.getSection("tree", "intro", Locale.ENGLISH)
        }
        assertEquals(listOf(expected), result)
    }
}
