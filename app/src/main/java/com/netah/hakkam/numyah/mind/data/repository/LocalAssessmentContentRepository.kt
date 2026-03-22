package com.netah.hakkam.numyah.mind.data.repository

import com.netah.hakkam.numyah.mind.data.local.content.JsonAssessmentContentDataSource
import com.netah.hakkam.numyah.mind.data.local.database.AnswerOptionTable
import com.netah.hakkam.numyah.mind.data.local.database.QuestionPageTable
import com.netah.hakkam.numyah.mind.data.local.database.QuestionTable
import com.netah.hakkam.numyah.mind.data.local.database.QuestionnaireContentDao
import com.netah.hakkam.numyah.mind.data.local.database.QuestionnaireTable
import com.netah.hakkam.numyah.mind.data.local.database.SephiraSectionTable
import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.QuestionContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
                SephiraSectionTable(
                    questionnaireVersion = seedQuestionnaire.version,
                    sephiraId = section.sephiraId,
                    displayNameEn = section.displayName.en,
                    displayNameEs = section.displayName.es,
                    shortMeaningEn = section.shortMeaning.en,
                    shortMeaningEs = section.shortMeaning.es,
                    introTextEn = section.introText.en,
                    introTextEs = section.introText.es,
                    displayOrder = index
                )
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
                SephiraSectionContent(
                    sephiraId = section.sephiraId,
                    displayName = section.resolveDisplayName(localeLanguage),
                    shortMeaning = section.resolveShortMeaning(localeLanguage),
                    introText = section.resolveIntroText(localeLanguage),
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

    private fun QuestionPageTable.resolveTitle(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) titleEs else titleEn
    }

    private fun QuestionPageTable.resolveDescription(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) descriptionEs else descriptionEn
    }

    private fun QuestionTable.resolvePrompt(localeLanguage: String): String {
        return if (localeLanguage.equals("es", ignoreCase = true)) promptEs else promptEn
    }

    private fun resolveResponseScaleFormat(seedQuestionnaire: com.netah.hakkam.numyah.mind.data.local.content.SeedQuestionnaire): QuestionFormat {
        return seedQuestionnaire.sections
            .flatMap { section -> section.questions }
            .firstOrNull()
            ?.format ?: QuestionFormat.LIKERT_5
    }
}
