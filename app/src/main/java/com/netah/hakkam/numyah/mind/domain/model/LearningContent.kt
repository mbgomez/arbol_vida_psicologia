package com.netah.hakkam.numyah.mind.domain.model

data class LearningCatalog(
    val version: String,
    val title: String,
    val courses: List<LearningCourse>
)

data class LearningCourse(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val estimatedMinutes: Int,
    val totalSectionCount: Int,
    val sections: List<LearningSection>
) {
    val availableSectionCount: Int
        get() = sections.size
}

data class LearningSection(
    val id: String,
    val title: String,
    val summary: String,
    val readingTimeMinutes: Int,
    val order: Int,
    val content: List<String>
)
