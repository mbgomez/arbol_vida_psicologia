package com.netah.hakkam.numyah.mind.data.local.content

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

internal data class LearningLocalizedText(
    val en: String,
    val es: String
) {
    fun resolve(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) es else en
    }
}

internal data class SeedLearningSection(
    val id: String,
    val title: LearningLocalizedText,
    val summary: LearningLocalizedText,
    val readingTimeMinutes: Int,
    val order: Int,
    val content: List<LearningLocalizedText>
)

internal data class SeedLearningCourse(
    val id: String,
    val title: LearningLocalizedText,
    val subtitle: LearningLocalizedText,
    val description: LearningLocalizedText,
    val estimatedMinutes: Int,
    val totalSectionCount: Int,
    val sections: List<SeedLearningSection>
)

internal data class SeedLearningCatalog(
    val version: String,
    val title: LearningLocalizedText,
    val courses: List<SeedLearningCourse>
)

@Singleton
class JsonLearningContentDataSource(
    private val jsonLoader: () -> String,
    moshi: Moshi
) {

    @Inject
    constructor(
        @ApplicationContext context: Context,
        moshi: Moshi
    ) : this(
        jsonLoader = {
            context.assets.open(LEARNING_ASSET_PATH).bufferedReader().use { it.readText() }
        },
        moshi = moshi
    )

    private val catalogAdapter = moshi.adapter(SeedLearningCatalog::class.java)

    internal fun getCatalog(): SeedLearningCatalog {
        val rawJson = jsonLoader()
        return catalogAdapter.fromJson(rawJson)
            ?: error("Unable to parse learning catalog JSON from $LEARNING_ASSET_PATH")
    }

    private companion object {
        const val LEARNING_ASSET_PATH = "courses/tree_of_life_courses.json"
    }
}
