package com.netah.hakkam.numyah.mind.data.repository

import com.netah.hakkam.numyah.mind.data.local.content.JsonAssessmentContentDataSource
import com.netah.hakkam.numyah.mind.data.datasource.local.AnswerOptionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionPageTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireContentDao
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireTable
import com.netah.hakkam.numyah.mind.data.datasource.local.SephiraPracticeTable
import com.netah.hakkam.numyah.mind.data.datasource.local.SephiraSectionTable
import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.CompletionPoleContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SephiraCompletionContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraDetailContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private data class CachedCompletionPole(
    val reflection: com.netah.hakkam.numyah.mind.data.local.content.LocalizedText,
    val practice: com.netah.hakkam.numyah.mind.data.local.content.LocalizedText?
)

private data class CachedCompletionContent(
    val sectionSummary: com.netah.hakkam.numyah.mind.data.local.content.LocalizedText,
    val balanced: CachedCompletionPole,
    val deficiency: CachedCompletionPole,
    val excess: CachedCompletionPole
)

interface AssessmentContentRepository {
    fun getCurrentQuestionnaire(locale: Locale): Flow<QuestionnaireContent>
}

class LocalAssessmentContentRepository @Inject constructor(
    private val jsonAssessmentContentDataSource: JsonAssessmentContentDataSource,
    private val questionnaireContentDao: QuestionnaireContentDao
) : AssessmentContentRepository {

    override fun getCurrentQuestionnaire(locale: Locale): Flow<QuestionnaireContent> = flow {
        val seedQuestionnaire = jsonAssessmentContentDataSource.getCurrentQuestionnaire()
        ensureQuestionnaireCached(seedQuestionnaire)
        emit(loadQuestionnaireFromCache(locale))
    }

    private suspend fun ensureQuestionnaireCached(seedQuestionnaire: com.netah.hakkam.numyah.mind.data.local.content.SeedQuestionnaire) {
        val cachedQuestionnaire = questionnaireContentDao.getLatestQuestionnaire()
        if (cachedQuestionnaire?.version == seedQuestionnaire.version) {
            return
        }

        cachedQuestionnaire?.let { existing ->
            questionnaireContentDao.deleteQuestions(existing.version)
            questionnaireContentDao.deletePages(existing.version)
            questionnaireContentDao.deletePractices(existing.version)
            questionnaireContentDao.deleteSections(existing.version)
            questionnaireContentDao.deleteAnswerOptions(existing.version)
            questionnaireContentDao.deleteQuestionnaire(existing.version)
        }

        questionnaireContentDao.insertQuestionnaire(
            QuestionnaireTable(
                version = seedQuestionnaire.version,
                titleEn = seedQuestionnaire.title.en,
                titleEs = seedQuestionnaire.title.es
            )
        )
        questionnaireContentDao.insertAnswerOptions(
            seedQuestionnaire.responseScale.mapIndexed { index, option ->
                AnswerOptionTable(
                    questionnaireVersion = seedQuestionnaire.version,
                    optionId = option.id,
                    labelEn = option.label.en,
                    labelEs = option.label.es,
                    numericValue = option.numericValue,
                    displayOrder = index
                )
            }
        )
        questionnaireContentDao.insertSections(
            seedQuestionnaire.sections.mapIndexed { index, section ->
                val completionContent = section.toCachedCompletionContent()
                SephiraSectionTable(
                    questionnaireVersion = seedQuestionnaire.version,
                    sephiraId = section.sephiraId,
                    displayNameEn = section.displayName.en,
                    displayNameEs = section.displayName.es,
                    shortMeaningEn = section.shortMeaning.en,
                    shortMeaningEs = section.shortMeaning.es,
                    introTextEn = section.introText.en,
                    introTextEs = section.introText.es,
                    completionSummaryEn = completionContent.sectionSummary.en,
                    completionSummaryEs = completionContent.sectionSummary.es,
                    balancedReflectionEn = completionContent.balanced.reflection.en,
                    balancedReflectionEs = completionContent.balanced.reflection.es,
                    balancedPracticeEn = completionContent.balanced.practice?.en,
                    balancedPracticeEs = completionContent.balanced.practice?.es,
                    deficiencyReflectionEn = completionContent.deficiency.reflection.en,
                    deficiencyReflectionEs = completionContent.deficiency.reflection.es,
                    deficiencyPracticeEn = completionContent.deficiency.practice?.en,
                    deficiencyPracticeEs = completionContent.deficiency.practice?.es,
                    excessReflectionEn = completionContent.excess.reflection.en,
                    excessReflectionEs = completionContent.excess.reflection.es,
                    excessPracticeEn = completionContent.excess.practice?.en,
                    excessPracticeEs = completionContent.excess.practice?.es,
                    healthyExpressionEn = section.healthyExpression?.en ?: section.shortMeaning.en,
                    healthyExpressionEs = section.healthyExpression?.es ?: section.shortMeaning.es,
                    deficiencyPatternEn = section.deficiencyPattern?.en ?: section.introText.en,
                    deficiencyPatternEs = section.deficiencyPattern?.es ?: section.introText.es,
                    excessPatternEn = section.excessPattern?.en ?: section.introText.en,
                    excessPatternEs = section.excessPattern?.es ?: section.introText.es,
                    displayOrder = index
                )
            }
        )
        questionnaireContentDao.insertPractices(
            seedQuestionnaire.sections.flatMap { section ->
                section.suggestedPractices.mapIndexed { index, practice ->
                    SephiraPracticeTable(
                        questionnaireVersion = seedQuestionnaire.version,
                        sephiraId = section.sephiraId,
                        practiceId = practice.id,
                        textEn = practice.text.en,
                        textEs = practice.text.es,
                        displayOrder = index
                    )
                }
            }
        )
        questionnaireContentDao.insertPages(
            seedQuestionnaire.sections.flatMap { section ->
                section.pages.mapIndexed { index, page ->
                    QuestionPageTable(
                        questionnaireVersion = seedQuestionnaire.version,
                        pageId = page.id,
                        sephiraId = section.sephiraId,
                        titleEn = page.title.en,
                        titleEs = page.title.es,
                        descriptionEn = page.description.en,
                        descriptionEs = page.description.es,
                        displayOrder = index
                    )
                }
            }
        )
        questionnaireContentDao.insertQuestions(
            seedQuestionnaire.sections.flatMap { section ->
                section.questions.mapIndexed { index, question ->
                    QuestionTable(
                        questionnaireVersion = seedQuestionnaire.version,
                        questionId = question.id,
                        sephiraId = question.sephiraId,
                        pageId = question.pageId,
                        promptEn = question.prompt.en,
                        promptEs = question.prompt.es,
                        format = question.format,
                        targetPole = question.targetPole,
                        weight = question.weight,
                        displayOrder = index
                    )
                }
            }
        )
    }

    private suspend fun loadQuestionnaireFromCache(locale: Locale): QuestionnaireContent {
        val localeLanguage = locale.language
        val questionnaire = questionnaireContentDao.getLatestQuestionnaire()
            ?: error("Expected questionnaire content to be cached before loading")
        val options = questionnaireContentDao.getAnswerOptions(questionnaire.version)
        val sections = questionnaireContentDao.getSections(questionnaire.version)
        val practices = questionnaireContentDao.getPractices(questionnaire.version)
        val pages = questionnaireContentDao.getPages(questionnaire.version)
        val questions = questionnaireContentDao.getQuestions(questionnaire.version)

        return QuestionnaireContent(
            version = questionnaire.version,
            title = questionnaire.resolveTitle(localeLanguage),
            responseScale = ResponseScaleDefinition(
                format = resolveResponseScaleFormat(questions),
                options = options.map { option ->
                    AnswerOption(
                        id = option.optionId,
                        label = option.resolveLabel(localeLanguage),
                        numericValue = option.numericValue
                    )
                }
            ),
            sections = sections.map { section ->
                val sectionPages = pages.filter { it.sephiraId == section.sephiraId }
                val sectionQuestions = questions.filter { it.sephiraId == section.sephiraId }
                val sectionPractices = practices.filter { it.sephiraId == section.sephiraId }
                SephiraSectionContent(
                    sephiraId = section.sephiraId,
                    displayName = section.resolveDisplayName(localeLanguage),
                    shortMeaning = section.resolveShortMeaning(localeLanguage),
                    introText = section.resolveIntroText(localeLanguage),
                    completionContent = SephiraCompletionContent(
                        sectionSummary = section.resolveCompletionSummary(localeLanguage),
                        balanced = CompletionPoleContent(
                            reflection = section.resolveBalancedReflection(localeLanguage),
                            practice = section.resolveBalancedPractice(localeLanguage)
                        ),
                        deficiency = CompletionPoleContent(
                            reflection = section.resolveDeficiencyReflection(localeLanguage),
                            practice = section.resolveDeficiencyPractice(localeLanguage)
                        ),
                        excess = CompletionPoleContent(
                            reflection = section.resolveExcessReflection(localeLanguage),
                            practice = section.resolveExcessPractice(localeLanguage)
                        )
                    ),
                    detailContent = SephiraDetailContent(
                        healthyExpression = section.resolveHealthyExpression(localeLanguage),
                        deficiencyPattern = section.resolveDeficiencyPattern(localeLanguage),
                        excessPattern = section.resolveExcessPattern(localeLanguage),
                        suggestedPractices = sectionPractices.map { practice ->
                            practice.resolveText(localeLanguage)
                        }
                    ),
                    pages = sectionPages.map { page ->
                        val pageQuestionIds = sectionQuestions
                            .filter { it.pageId == page.pageId }
                            .sortedBy { it.displayOrder }
                            .map { it.questionId }
                        QuestionPageContent(
                            id = page.pageId,
                            title = page.resolveTitle(localeLanguage),
                            description = page.resolveDescription(localeLanguage),
                            questionIds = pageQuestionIds
                        )
                    },
                    questions = sectionQuestions.sortedBy { it.displayOrder }.map { question ->
                        QuestionContent(
                            id = question.questionId,
                            sephiraId = question.sephiraId,
                            pageId = question.pageId,
                            prompt = question.resolvePrompt(localeLanguage),
                            format = question.format,
                            targetPole = question.targetPole,
                            weight = question.weight
                        )
                    }
                )
            }
        )
    }

    private fun resolveResponseScaleFormat(questions: List<QuestionTable>): QuestionFormat {
        return questions.firstOrNull()?.format ?: QuestionFormat.LIKERT_5
    }

    private fun QuestionnaireTable.resolveTitle(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) titleEs else titleEn
    }

    private fun AnswerOptionTable.resolveLabel(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) labelEs else labelEn
    }

    private fun SephiraSectionTable.resolveDisplayName(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) displayNameEs else displayNameEn
    }

    private fun SephiraSectionTable.resolveShortMeaning(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) shortMeaningEs else shortMeaningEn
    }

    private fun SephiraSectionTable.resolveIntroText(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) introTextEs else introTextEn
    }

    private fun SephiraSectionTable.resolveCompletionSummary(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) completionSummaryEs else completionSummaryEn
    }

    private fun SephiraSectionTable.resolveBalancedReflection(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) {
            balancedReflectionEs
        } else {
            balancedReflectionEn
        }
    }

    private fun SephiraSectionTable.resolveBalancedPractice(localeLanguage: String): String? {
        return if (localeLanguage.equals("es", ignoreCase = true)) balancedPracticeEs else balancedPracticeEn
    }

    private fun SephiraSectionTable.resolveDeficiencyReflection(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) {
            deficiencyReflectionEs
        } else {
            deficiencyReflectionEn
        }
    }

    private fun SephiraSectionTable.resolveDeficiencyPractice(localeLanguage: String): String? {
        return if (localeLanguage.equals("es", ignoreCase = true)) deficiencyPracticeEs else deficiencyPracticeEn
    }

    private fun SephiraSectionTable.resolveExcessReflection(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) {
            excessReflectionEs
        } else {
            excessReflectionEn
        }
    }

    private fun SephiraSectionTable.resolveExcessPractice(localeLanguage: String): String? {
        return if (localeLanguage.equals("es", ignoreCase = true)) excessPracticeEs else excessPracticeEn
    }

    private fun SephiraSectionTable.resolveHealthyExpression(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) {
            healthyExpressionEs
        } else {
            healthyExpressionEn
        }
    }

    private fun SephiraSectionTable.resolveDeficiencyPattern(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) {
            deficiencyPatternEs
        } else {
            deficiencyPatternEn
        }
    }

    private fun SephiraSectionTable.resolveExcessPattern(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) {
            excessPatternEs
        } else {
            excessPatternEn
        }
    }

    private fun SephiraPracticeTable.resolveText(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) textEs else textEn
    }

    private fun QuestionPageTable.resolveTitle(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) titleEs else titleEn
    }

    private fun QuestionPageTable.resolveDescription(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) descriptionEs else descriptionEn
    }

    private fun QuestionTable.resolvePrompt(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) promptEs else promptEn
    }

    private fun com.netah.hakkam.numyah.mind.data.local.content.SeedSephiraSection.toCachedCompletionContent():
        CachedCompletionContent {
        val firstPractice = suggestedPractices.firstOrNull()?.text
        val authoredContent = completionContent
        return CachedCompletionContent(
            sectionSummary = authoredContent?.sectionSummary ?: shortMeaning,
            balanced = CachedCompletionPole(
                reflection = authoredContent?.balanced?.reflection ?: healthyExpression ?: shortMeaning,
                practice = authoredContent?.balanced?.practice ?: firstPractice
            ),
            deficiency = CachedCompletionPole(
                reflection = authoredContent?.deficiency?.reflection ?: deficiencyPattern ?: introText,
                practice = authoredContent?.deficiency?.practice ?: firstPractice
            ),
            excess = CachedCompletionPole(
                reflection = authoredContent?.excess?.reflection ?: excessPattern ?: introText,
                practice = authoredContent?.excess?.practice ?: firstPractice
            )
        )
    }
}
