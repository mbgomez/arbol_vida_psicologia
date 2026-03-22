package com.netah.hakkam.numyah.mind.data.local.content

import android.content.Context
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

internal data class LocalizedText(
    val en: String,
    val es: String
) {
    fun resolve(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) es else en
    }
}

internal data class SeedAnswerOption(
    val id: String,
    val label: LocalizedText,
    val numericValue: Int
)

internal data class SeedQuestion(
    val id: String,
    val sephiraId: SephiraId,
    val pageId: String,
    val prompt: LocalizedText,
    val format: QuestionFormat,
    val targetPole: Pole,
    val weight: Double = 1.0
)

internal data class SeedQuestionPage(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val questionIds: List<String>
)

internal data class SeedPractice(
    val id: String,
    val text: LocalizedText
)

internal data class SeedSephiraSection(
    val sephiraId: SephiraId,
    val displayName: LocalizedText,
    val shortMeaning: LocalizedText,
    val introText: LocalizedText,
    val healthyExpression: LocalizedText? = null,
    val deficiencyPattern: LocalizedText? = null,
    val excessPattern: LocalizedText? = null,
    val suggestedPractices: List<SeedPractice> = emptyList(),
    val pages: List<SeedQuestionPage>,
    val questions: List<SeedQuestion>
)

internal data class SeedQuestionnaire(
    val version: String,
    val title: LocalizedText,
    val responseScale: List<SeedAnswerOption>,
    val sections: List<SeedSephiraSection>
)

@Singleton
class JsonAssessmentContentDataSource(
    private val jsonLoader: () -> String,
    moshi: Moshi
) {

    @Inject
    constructor(
        @ApplicationContext context: Context,
        moshi: Moshi
    ) : this(
        jsonLoader = {
            context.assets.open(QUESTIONNAIRE_ASSET_PATH).bufferedReader().use { it.readText() }
        },
        moshi = moshi
    )

    private val questionnaireAdapter = moshi.adapter(SeedQuestionnaire::class.java)

    internal fun getCurrentQuestionnaire(): SeedQuestionnaire {
        val rawJson = jsonLoader()
        return questionnaireAdapter.fromJson(rawJson)
            ?: error("Unable to parse assessment questionnaire JSON from $QUESTIONNAIRE_ASSET_PATH")
    }

    private companion object {
        const val QUESTIONNAIRE_ASSET_PATH = "questionnaires/tree_of_life_questionnaire.json"
    }
}
