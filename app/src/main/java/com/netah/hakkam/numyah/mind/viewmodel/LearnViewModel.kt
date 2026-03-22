package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed interface LearnUiState {
    data object Loading : LearnUiState
    data class Loaded(val model: LearnCatalogUiModel) : LearnUiState
    data object Error : LearnUiState
}

sealed interface LearnCourseUiState {
    data object Loading : LearnCourseUiState
    data class Loaded(val model: LearnCourseUiModel) : LearnCourseUiState
    data object NotFound : LearnCourseUiState
    data object Error : LearnCourseUiState
}

sealed interface LearnSectionUiState {
    data object Loading : LearnSectionUiState
    data class Loaded(val model: LearnSectionUiModel) : LearnSectionUiState
    data object Locked : LearnSectionUiState
    data object NotFound : LearnSectionUiState
    data object Error : LearnSectionUiState
}

data class LearnCatalogUiModel(
    val title: String,
    val courses: List<LearnCourseCardUiModel>
)

data class LearnCourseCardUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val estimatedMinutes: Int,
    val availableSectionCount: Int,
    val totalSectionCount: Int
)

data class LearnCourseUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val estimatedMinutes: Int,
    val availableSectionCount: Int,
    val totalSectionCount: Int,
    val sections: List<LearnSectionListItemUiModel>
)

data class LearnSectionListItemUiModel(
    val id: String,
    val title: String,
    val summary: String,
    val readingTimeMinutes: Int,
    val order: Int,
    val isCompleted: Boolean,
    val isLocked: Boolean
)

data class LearnSectionUiModel(
    val courseId: String,
    val courseTitle: String,
    val sectionId: String,
    val sectionTitle: String,
    val summary: String,
    val order: Int,
    val totalAvailableSections: Int,
    val readingTimeMinutes: Int,
    val paragraphs: List<String>,
    val isCompleted: Boolean,
    val previousSectionId: String?,
    val previousSectionTitle: String?,
    val nextSectionId: String?,
    val nextSectionTitle: String?,
    val isNextSectionLocked: Boolean
)

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val getLearningCatalogUseCase: GetLearningCatalogUseCase,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<LearnUiState>(LearnUiState.Loading)
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    init {
        loadCatalog()
    }

    fun retry() {
        loadCatalog()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            _uiState.value = LearnUiState.Loading
            try {
                val catalog = getLearningCatalogUseCase.run(currentLocaleProvider.current()).first()
                _uiState.value = LearnUiState.Loaded(catalog.toUiModel())
            } catch (_: Throwable) {
                _uiState.value = LearnUiState.Error
            }
        }
    }
}

@HiltViewModel
class LearnCourseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getLearningCourseUseCase: GetLearningCourseUseCase,
    private val getCompletedLearningSectionsUseCase: GetCompletedLearningSectionsUseCase,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val courseId: String = checkNotNull(
        savedStateHandle.get<String>(AppDestination.LearnCourse.courseIdArg)
    )

    private val _uiState = MutableStateFlow<LearnCourseUiState>(LearnCourseUiState.Loading)
    val uiState: StateFlow<LearnCourseUiState> = _uiState.asStateFlow()

    init {
        loadCourse()
    }

    fun retry() {
        loadCourse()
    }

    private fun loadCourse() {
        viewModelScope.launch {
            _uiState.value = LearnCourseUiState.Loading
            try {
                val locale = currentLocaleProvider.current()
                val state = combine(
                    getLearningCourseUseCase.run(
                        GetLearningCourseParams(
                            courseId = courseId,
                            locale = locale
                        )
                    ),
                    getCompletedLearningSectionsUseCase.run()
                ) { course, completedSectionKeys ->
                    course?.let {
                        LearnCourseUiState.Loaded(it.toUiModel(completedSectionKeys))
                    } ?: LearnCourseUiState.NotFound
                }.first()
                _uiState.value = state
            } catch (_: Throwable) {
                _uiState.value = LearnCourseUiState.Error
            }
        }
    }
}

@HiltViewModel
class LearnSectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getLearningCourseUseCase: GetLearningCourseUseCase,
    private val getLearningSectionUseCase: GetLearningSectionUseCase,
    private val getCompletedLearningSectionsUseCase: GetCompletedLearningSectionsUseCase,
    private val markLearningSectionCompletedUseCase: MarkLearningSectionCompletedUseCase,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val courseId: String = checkNotNull(
        savedStateHandle.get<String>(AppDestination.LearnSection.courseIdArg)
    )
    private val sectionId: String = checkNotNull(
        savedStateHandle.get<String>(AppDestination.LearnSection.sectionIdArg)
    )

    private val _uiState = MutableStateFlow<LearnSectionUiState>(LearnSectionUiState.Loading)
    val uiState: StateFlow<LearnSectionUiState> = _uiState.asStateFlow()

    init {
        loadSection()
    }

    fun markSectionCompleted() {
        val state = _uiState.value as? LearnSectionUiState.Loaded ?: return
        if (state.model.isCompleted) {
            return
        }
        viewModelScope.launch {
            try {
                markLearningSectionCompletedUseCase.run(
                    MarkLearningSectionCompletedParams(
                        courseId = courseId,
                        sectionId = sectionId
                    )
                ).firstOrNull()
                loadSection()
            } catch (_: Throwable) {
                _uiState.value = LearnSectionUiState.Error
            }
        }
    }

    fun retry() {
        loadSection()
    }

    private fun loadSection() {
        viewModelScope.launch {
            _uiState.value = LearnSectionUiState.Loading
            try {
                val locale = currentLocaleProvider.current()
                val course = getLearningCourseUseCase.run(
                    GetLearningCourseParams(courseId = courseId, locale = locale)
                ).first()
                val section = getLearningSectionUseCase.run(
                    GetLearningSectionParams(courseId = courseId, sectionId = sectionId, locale = locale)
                ).first()
                val completedSectionKeys = getCompletedLearningSectionsUseCase.run().first()
                _uiState.value = when {
                    course == null || section == null -> LearnSectionUiState.NotFound
                    !course.isSectionUnlocked(section.id, completedSectionKeys) -> LearnSectionUiState.Locked
                    else -> LearnSectionUiState.Loaded(course.toSectionUiModel(section, completedSectionKeys))
                }
            } catch (_: Throwable) {
                _uiState.value = LearnSectionUiState.Error
            }
        }
    }
}

private fun LearningCatalog.toUiModel(): LearnCatalogUiModel {
    return LearnCatalogUiModel(
        title = title,
        courses = courses.map { it.toCardUiModel() }
    )
}

private fun LearningCourse.toCardUiModel(): LearnCourseCardUiModel {
    return LearnCourseCardUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        estimatedMinutes = estimatedMinutes,
        availableSectionCount = availableSectionCount,
        totalSectionCount = totalSectionCount
    )
}

private fun LearningCourse.toUiModel(completedSectionKeys: Set<String>): LearnCourseUiModel {
    return LearnCourseUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        estimatedMinutes = estimatedMinutes,
        availableSectionCount = availableSectionCount,
        totalSectionCount = totalSectionCount,
        sections = sections.map { section ->
            LearnSectionListItemUiModel(
                id = section.id,
                title = section.title,
                summary = section.summary,
                readingTimeMinutes = section.readingTimeMinutes,
                order = section.order,
                isCompleted = completedSectionKeys.contains(sectionCompletionKey(id, section.id)),
                isLocked = !isSectionUnlocked(section.id, completedSectionKeys)
            )
        }
    )
}

private fun LearningCourse.toSectionUiModel(
    section: LearningSection,
    completedSectionKeys: Set<String>
): LearnSectionUiModel {
    val previousSection = sections
        .sortedBy { it.order }
        .firstOrNull { it.order == section.order - 1 }
    val nextSection = sections
        .sortedBy { it.order }
        .firstOrNull { it.order == section.order + 1 }
    return LearnSectionUiModel(
        courseId = id,
        courseTitle = title,
        sectionId = section.id,
        sectionTitle = section.title,
        summary = section.summary,
        order = section.order,
        totalAvailableSections = availableSectionCount,
        readingTimeMinutes = section.readingTimeMinutes,
        paragraphs = section.content,
        isCompleted = completedSectionKeys.contains(sectionCompletionKey(id, section.id)),
        previousSectionId = previousSection?.id,
        previousSectionTitle = previousSection?.title,
        nextSectionId = nextSection?.id,
        nextSectionTitle = nextSection?.title,
        isNextSectionLocked = nextSection?.let {
            !isSectionUnlocked(it.id, completedSectionKeys)
        } ?: false
    )
}

private fun LearningCourse.isSectionUnlocked(
    sectionId: String,
    completedSectionKeys: Set<String>
): Boolean {
    val orderedSections = sections.sortedBy { it.order }
    val sectionIndex = orderedSections.indexOfFirst { it.id == sectionId }
    if (sectionIndex <= 0) {
        return sectionIndex != -1
    }
    val previousSection = orderedSections[sectionIndex - 1]
    return completedSectionKeys.contains(sectionCompletionKey(id, previousSection.id))
}

private fun sectionCompletionKey(courseId: String, sectionId: String): String {
    return "$courseId::$sectionId"
}
