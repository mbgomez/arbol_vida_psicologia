package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.LearningContentRepository
import com.netah.hakkam.numyah.mind.domain.model.LearningCatalog
import com.netah.hakkam.numyah.mind.domain.model.LearningCourse
import com.netah.hakkam.numyah.mind.domain.model.LearningSection
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

data class GetLearningCourseParams(
    val courseId: String,
    val locale: Locale
)

data class GetLearningSectionParams(
    val courseId: String,
    val sectionId: String,
    val locale: Locale
)

class GetLearningCatalogUseCase @Inject constructor(
    private val learningContentRepository: LearningContentRepository
) : FlowInteractor<Locale, LearningCatalog>() {
    override fun buildUseCase(params: Locale): Flow<LearningCatalog> {
        return learningContentRepository.getCatalog(params)
    }
}

class GetLearningCourseUseCase @Inject constructor(
    private val learningContentRepository: LearningContentRepository
) : FlowInteractor<GetLearningCourseParams, LearningCourse?>() {
    override fun buildUseCase(params: GetLearningCourseParams): Flow<LearningCourse?> {
        return learningContentRepository.getCourse(
            courseId = params.courseId,
            locale = params.locale
        )
    }
}

class GetLearningSectionUseCase @Inject constructor(
    private val learningContentRepository: LearningContentRepository
) : FlowInteractor<GetLearningSectionParams, LearningSection?>() {
    override fun buildUseCase(params: GetLearningSectionParams): Flow<LearningSection?> {
        return learningContentRepository.getSection(
            courseId = params.courseId,
            sectionId = params.sectionId,
            locale = params.locale
        )
    }
}
