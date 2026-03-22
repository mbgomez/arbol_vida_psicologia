package com.netah.hakkam.numyah.mind.data.repository

import com.netah.hakkam.numyah.mind.data.local.content.JsonLearningContentDataSource
import com.netah.hakkam.numyah.mind.domain.model.LearningCatalog
import com.netah.hakkam.numyah.mind.domain.model.LearningCourse
import com.netah.hakkam.numyah.mind.domain.model.LearningSection
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LearningContentRepository {
    fun getCatalog(locale: Locale): Flow<LearningCatalog>
    fun getCourse(courseId: String, locale: Locale): Flow<LearningCourse?>
    fun getSection(courseId: String, sectionId: String, locale: Locale): Flow<LearningSection?>
}

class LocalLearningContentRepository @Inject constructor(
    private val jsonLearningContentDataSource: JsonLearningContentDataSource
) : LearningContentRepository {

    override fun getCatalog(locale: Locale): Flow<LearningCatalog> = flow {
        emit(resolveCatalog(locale))
    }

    override fun getCourse(courseId: String, locale: Locale): Flow<LearningCourse?> = flow {
        emit(resolveCatalog(locale).courses.firstOrNull { it.id == courseId })
    }

    override fun getSection(
        courseId: String,
        sectionId: String,
        locale: Locale
    ): Flow<LearningSection?> = flow {
        emit(
            resolveCatalog(locale).courses
                .firstOrNull { it.id == courseId }
                ?.sections
                ?.firstOrNull { it.id == sectionId }
        )
    }

    private fun resolveCatalog(locale: Locale): LearningCatalog {
        val localeLanguage = locale.language
        val seedCatalog = jsonLearningContentDataSource.getCatalog()
        return LearningCatalog(
            version = seedCatalog.version,
            title = seedCatalog.title.resolve(localeLanguage),
            courses = seedCatalog.courses.map { course ->
                LearningCourse(
                    id = course.id,
                    title = course.title.resolve(localeLanguage),
                    subtitle = course.subtitle.resolve(localeLanguage),
                    description = course.description.resolve(localeLanguage),
                    estimatedMinutes = course.estimatedMinutes,
                    totalSectionCount = course.totalSectionCount,
                    sections = course.sections.sortedBy { it.order }.map { section ->
                        LearningSection(
                            id = section.id,
                            title = section.title.resolve(localeLanguage),
                            summary = section.summary.resolve(localeLanguage),
                            readingTimeMinutes = section.readingTimeMinutes,
                            order = section.order,
                            content = section.content.map { paragraph ->
                                paragraph.resolve(localeLanguage)
                            }
                        )
                    }
                )
            }
        )
    }
}
