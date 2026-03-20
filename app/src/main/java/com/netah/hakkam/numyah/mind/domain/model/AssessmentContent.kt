package com.netah.hakkam.numyah.mind.domain.model

enum class SephiraId {
    KETER,
    CHOKHMAH,
    BINAH,
    CHESED,
    GEVURAH,
    TIFERET,
    NETZACH,
    HOD,
    YESOD,
    MALKUTH
}

enum class Pole {
    BALANCE,
    DEFICIENCY,
    EXCESS
}

enum class QuestionFormat {
    LIKERT_5,
    LIKERT_7,
    SINGLE_CHOICE
}

data class QuestionnaireContent(
    val version: String,
    val title: String,
    val responseScale: ResponseScaleDefinition,
    val sections: List<SephiraSectionContent>
)

data class ResponseScaleDefinition(
    val format: QuestionFormat,
    val options: List<AnswerOption>
)

data class AnswerOption(
    val id: String,
    val label: String,
    val numericValue: Int
)

data class SephiraSectionContent(
    val sephiraId: SephiraId,
    val displayName: String,
    val shortMeaning: String,
    val introText: String,
    val pages: List<QuestionPageContent>,
    val questions: List<QuestionContent>
)

data class QuestionPageContent(
    val id: String,
    val title: String,
    val description: String,
    val questionIds: List<String>
)

data class QuestionContent(
    val id: String,
    val sephiraId: SephiraId,
    val pageId: String,
    val prompt: String,
    val format: QuestionFormat,
    val targetPole: Pole,
    val weight: Double = 1.0
)
