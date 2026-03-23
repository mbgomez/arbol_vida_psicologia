package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.model.LearningCatalog
import com.netah.hakkam.numyah.mind.domain.model.LearningCourse
import com.netah.hakkam.numyah.mind.domain.model.LearningSection
import com.netah.hakkam.numyah.mind.domain.usecase.GetCompletedLearningSectionsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningCatalogUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningCourseParams
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningCourseUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningSectionParams
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningSectionUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.MarkLearningSectionCompletedParams
import com.netah.hakkam.numyah.mind.domain.usecase.MarkLearningSectionCompletedUseCase
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
class LearnViewModelTests {

    private lateinit var getLearningCatalogUseCase: GetLearningCatalogUseCase
    private lateinit var getLearningCourseUseCase: GetLearningCourseUseCase
    private lateinit var getLearningSectionUseCase: GetLearningSectionUseCase
    private lateinit var getCompletedLearningSectionsUseCase: GetCompletedLearningSectionsUseCase
    private lateinit var markLearningSectionCompletedUseCase: MarkLearningSectionCompletedUseCase
    private lateinit var currentLocaleProvider: CurrentLocaleProvider
    private lateinit var appTelemetry: AppTelemetry

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getLearningCatalogUseCase = mockk(relaxed = true)
        getLearningCourseUseCase = mockk(relaxed = true)
        getLearningSectionUseCase = mockk(relaxed = true)
        getCompletedLearningSectionsUseCase = mockk(relaxed = true)
        markLearningSectionCompletedUseCase = mockk(relaxed = true)
        currentLocaleProvider = mockk(relaxed = true)
        appTelemetry = mockk(relaxed = true)
        every { currentLocaleProvider.current() } returns Locale.ENGLISH
        every { getCompletedLearningSectionsUseCase.run() } returns flowOf(emptySet())
    }

    @Test
    fun learnViewModel_loadsCatalogCards() = coroutinesRule.runBlockingTest {
        every { getLearningCatalogUseCase.run(Locale.ENGLISH) } returns flowOf(testCatalog())

        val viewModel = LearnViewModel(
            getLearningCatalogUseCase = getLearningCatalogUseCase,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
        )
        val state = viewModel.uiState.value as LearnUiState.Loaded

        assertEquals("Courses", state.model.title)
        assertEquals(1, state.model.courses.size)
        assertEquals("Tree of Life overview", state.model.courses.first().title)
        assertEquals(3, state.model.courses.first().availableSectionCount)
    }

    @Test
    fun learnCourseViewModel_withMissingCourse_emitsNotFound() = coroutinesRule.runBlockingTest {
        every {
            getLearningCourseUseCase.run(GetLearningCourseParams("missing", Locale.ENGLISH))
        } returns flowOf(null)

        val viewModel = LearnCourseViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.LearnCourse.courseIdArg to "missing")
            ),
            getLearningCourseUseCase = getLearningCourseUseCase,
            getCompletedLearningSectionsUseCase = getCompletedLearningSectionsUseCase,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
        )

        assertTrue(viewModel.uiState.value is LearnCourseUiState.NotFound)
    }

    @Test
    fun learnSectionViewModel_buildsNextSectionNavigation() = coroutinesRule.runBlockingTest {
        val course = testCatalog().courses.first()
        val section = course.sections.first()
        every {
            getLearningCourseUseCase.run(GetLearningCourseParams("tree-course", Locale.ENGLISH))
        } returns flowOf(course)
        every { getCompletedLearningSectionsUseCase.run() } returns flowOf(setOf("tree-course::intro"))
        every {
            getLearningSectionUseCase.run(
                GetLearningSectionParams("tree-course", "intro", Locale.ENGLISH)
            )
        } returns flowOf(section)

        val viewModel = LearnSectionViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    AppDestination.LearnSection.courseIdArg to "tree-course",
                    AppDestination.LearnSection.sectionIdArg to "intro"
                )
            ),
            getLearningCourseUseCase = getLearningCourseUseCase,
            getLearningSectionUseCase = getLearningSectionUseCase,
            getCompletedLearningSectionsUseCase = getCompletedLearningSectionsUseCase,
            markLearningSectionCompletedUseCase = markLearningSectionCompletedUseCase,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
        )
        val state = viewModel.uiState.value as LearnSectionUiState.Loaded

        assertEquals("Introduction", state.model.sectionTitle)
        assertEquals(null, state.model.previousSectionId)
        assertEquals("malkuth", state.model.nextSectionId)
        assertEquals("Malkuth", state.model.nextSectionTitle)
        assertEquals(3, state.model.totalAvailableSections)
        assertEquals(true, state.model.isCompleted)
        verify(exactly = 1) {
            appTelemetry.trackLearnSectionOpened("tree-course", "intro", 1)
        }
    }

    @Test
    fun learnSectionViewModel_forLaterSection_exposesPreviousChapterNavigation() = coroutinesRule.runBlockingTest {
        val course = testCatalog().courses.first()
        val section = course.sections[1]
        every {
            getLearningCourseUseCase.run(GetLearningCourseParams("tree-course", Locale.ENGLISH))
        } returns flowOf(course)
        every { getCompletedLearningSectionsUseCase.run() } returns flowOf(setOf("tree-course::intro"))
        every {
            getLearningSectionUseCase.run(
                GetLearningSectionParams("tree-course", "malkuth", Locale.ENGLISH)
            )
        } returns flowOf(section)

        val viewModel = LearnSectionViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    AppDestination.LearnSection.courseIdArg to "tree-course",
                    AppDestination.LearnSection.sectionIdArg to "malkuth"
                )
            ),
            getLearningCourseUseCase = getLearningCourseUseCase,
            getLearningSectionUseCase = getLearningSectionUseCase,
            getCompletedLearningSectionsUseCase = getCompletedLearningSectionsUseCase,
            markLearningSectionCompletedUseCase = markLearningSectionCompletedUseCase,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
        )
        val state = viewModel.uiState.value as LearnSectionUiState.Loaded

        assertEquals("intro", state.model.previousSectionId)
        assertEquals("Introduction", state.model.previousSectionTitle)
        assertEquals("yesod", state.model.nextSectionId)
    }

    @Test
    fun learnCourseViewModel_locksSectionsUntilPreviousIsFinished() = coroutinesRule.runBlockingTest {
        val course = testCatalog().courses.first()
        every {
            getLearningCourseUseCase.run(GetLearningCourseParams("tree-course", Locale.ENGLISH))
        } returns flowOf(course)
        every { getCompletedLearningSectionsUseCase.run() } returns flowOf(emptySet())

        val viewModel = LearnCourseViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.LearnCourse.courseIdArg to "tree-course")
            ),
            getLearningCourseUseCase = getLearningCourseUseCase,
            getCompletedLearningSectionsUseCase = getCompletedLearningSectionsUseCase,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
        )
        val state = viewModel.uiState.value as LearnCourseUiState.Loaded

        assertEquals(false, state.model.sections[0].isLocked)
        assertEquals(true, state.model.sections[1].isLocked)
        assertEquals(true, state.model.sections[2].isLocked)
    }

    @Test
    fun learnSectionViewModel_withLockedSection_emitsLockedState() = coroutinesRule.runBlockingTest {
        val course = testCatalog().courses.first()
        val section = course.sections[1]
        every {
            getLearningCourseUseCase.run(GetLearningCourseParams("tree-course", Locale.ENGLISH))
        } returns flowOf(course)
        every { getCompletedLearningSectionsUseCase.run() } returns flowOf(emptySet())
        every {
            getLearningSectionUseCase.run(
                GetLearningSectionParams("tree-course", "malkuth", Locale.ENGLISH)
            )
        } returns flowOf(section)

        val viewModel = LearnSectionViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    AppDestination.LearnSection.courseIdArg to "tree-course",
                    AppDestination.LearnSection.sectionIdArg to "malkuth"
                )
            ),
            getLearningCourseUseCase = getLearningCourseUseCase,
            getLearningSectionUseCase = getLearningSectionUseCase,
            getCompletedLearningSectionsUseCase = getCompletedLearningSectionsUseCase,
            markLearningSectionCompletedUseCase = markLearningSectionCompletedUseCase,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
        )

        assertTrue(viewModel.uiState.value is LearnSectionUiState.Locked)
    }

    @Test
    fun markSectionCompleted_callsUseCaseForCurrentSection() = coroutinesRule.runBlockingTest {
        val course = testCatalog().courses.first()
        val section = course.sections[1]
        every {
            getLearningCourseUseCase.run(GetLearningCourseParams("tree-course", Locale.ENGLISH))
        } returns flowOf(course)
        every { getCompletedLearningSectionsUseCase.run() } returnsMany listOf(
            flowOf(setOf("tree-course::intro")),
            flowOf(setOf("tree-course::intro", "tree-course::malkuth"))
        )
        every {
            getLearningSectionUseCase.run(
                GetLearningSectionParams("tree-course", "malkuth", Locale.ENGLISH)
            )
        } returns flowOf(section)
        every {
            markLearningSectionCompletedUseCase.run(
                MarkLearningSectionCompletedParams("tree-course", "malkuth")
            )
        } returns flowOf(setOf("tree-course::intro", "tree-course::malkuth"))

        val viewModel = LearnSectionViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    AppDestination.LearnSection.courseIdArg to "tree-course",
                    AppDestination.LearnSection.sectionIdArg to "malkuth"
                )
            ),
            getLearningCourseUseCase = getLearningCourseUseCase,
            getLearningSectionUseCase = getLearningSectionUseCase,
            getCompletedLearningSectionsUseCase = getCompletedLearningSectionsUseCase,
            markLearningSectionCompletedUseCase = markLearningSectionCompletedUseCase,
            currentLocaleProvider = currentLocaleProvider,
            appTelemetry = appTelemetry
        )

        viewModel.markSectionCompleted()

        verify {
            markLearningSectionCompletedUseCase.run(
                MarkLearningSectionCompletedParams("tree-course", "malkuth")
            )
        }
    }

    private fun testCatalog(): LearningCatalog {
        return LearningCatalog(
            version = "learning-v1",
            title = "Courses",
            courses = listOf(
                LearningCourse(
                    id = "tree-course",
                    title = "Tree of Life overview",
                    subtitle = "A guided introduction",
                    description = "Introduction, Malkuth, and Yesod are available.",
                    estimatedMinutes = 24,
                    totalSectionCount = 11,
                    sections = listOf(
                        LearningSection(
                            id = "intro",
                            title = "Introduction",
                            summary = "A first orientation.",
                            readingTimeMinutes = 8,
                            order = 1,
                            content = listOf("Paragraph 1")
                        ),
                        LearningSection(
                            id = "malkuth",
                            title = "Malkuth",
                            summary = "Material life.",
                            readingTimeMinutes = 8,
                            order = 2,
                            content = listOf("Paragraph 2")
                        ),
                        LearningSection(
                            id = "yesod",
                            title = "Yesod",
                            summary = "Relationship and foundation.",
                            readingTimeMinutes = 8,
                            order = 3,
                            content = listOf("Paragraph 3")
                        )
                    )
                )
            )
        )
    }
}
