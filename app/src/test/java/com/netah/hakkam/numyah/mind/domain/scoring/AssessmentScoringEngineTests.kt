package com.netah.hakkam.numyah.mind.domain.scoring

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.netah.hakkam.numyah.mind.data.local.database.NumyahMindDatabase
import com.netah.hakkam.numyah.mind.data.repository.LocalAssessmentContentRepository
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.SavedResponse
import com.netah.hakkam.numyah.mind.domain.model.ScoreInput
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import com.netah.hakkam.numyah.mind.data.local.content.JsonAssessmentContentDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AssessmentScoringEngineTests {

    private lateinit var db: NumyahMindDatabase
    private lateinit var questionnaireRepository: LocalAssessmentContentRepository
    private lateinit var scoringEngine: AssessmentScoringEngine

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NumyahMindDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        questionnaireRepository = LocalAssessmentContentRepository(
            jsonAssessmentContentDataSource = JsonAssessmentContentDataSource(
                jsonLoader = { TEST_QUESTIONNAIRE_JSON },
                moshi = moshi
            ),
            questionnaireContentDao = db.getQuestionnaireContentDao()
        )
        scoringEngine = AssessmentScoringEngine()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun score_returnsBalancedWhenBalanceItemsLeadClearly() = coroutinesRule.runBlockingTest {
        val questionnaire = questionnaireRepository.getCurrentQuestionnaire(Locale.ENGLISH).first()

        val score = scoringEngine.score(
            input = ScoreInput(
                questionnaire = questionnaire,
                sephiraId = SephiraId.MALKUTH,
                responses = listOf(
                    response("malkuth_resources_excess", 1, 0),
                    response("malkuth_resources_deficiency", 1, 1),
                    response("malkuth_resources_balance", 3, 2),
                    response("malkuth_body_excess", 1, 3),
                    response("malkuth_body_deficiency", 0, 4),
                    response("malkuth_body_balance", 4, 5)
                )
            ),
            sessionId = 10L
        )

        assertEquals(Pole.BALANCE, score.dominantPole)
        assertEquals(ConfidenceLevel.HIGH, score.confidence)
        assertFalse(score.isLowConfidence)
    }

    @Test
    fun score_marksLowConfidenceWhenPolesAreTooClose() = coroutinesRule.runBlockingTest {
        val questionnaire = questionnaireRepository.getCurrentQuestionnaire(Locale.ENGLISH).first()

        val score = scoringEngine.score(
            input = ScoreInput(
                questionnaire = questionnaire,
                sephiraId = SephiraId.MALKUTH,
                responses = listOf(
                    response("malkuth_resources_excess", 3, 0),
                    response("malkuth_resources_deficiency", 2, 1),
                    response("malkuth_resources_balance", 3, 2),
                    response("malkuth_body_excess", 2, 3),
                    response("malkuth_body_deficiency", 3, 4),
                    response("malkuth_body_balance", 2, 5)
                )
            ),
            sessionId = 11L
        )

        assertTrue(score.isLowConfidence)
        assertEquals(ConfidenceLevel.LOW, score.confidence)
    }

    @Test
    fun score_returnsDeficiencyWhenDeficiencyItemsLead() = coroutinesRule.runBlockingTest {
        val questionnaire = questionnaireRepository.getCurrentQuestionnaire(Locale.ENGLISH).first()

        val score = scoringEngine.score(
            input = ScoreInput(
                questionnaire = questionnaire,
                sephiraId = SephiraId.MALKUTH,
                responses = listOf(
                    response("malkuth_resources_excess", 0, 0),
                    response("malkuth_resources_deficiency", 4, 1),
                    response("malkuth_resources_balance", 1, 2),
                    response("malkuth_body_excess", 1, 3),
                    response("malkuth_body_deficiency", 4, 4),
                    response("malkuth_body_balance", 1, 5)
                )
            ),
            sessionId = 12L
        )

        assertEquals(Pole.DEFICIENCY, score.dominantPole)
        assertFalse(score.isLowConfidence)
    }

    private fun response(questionId: String, numericValue: Int, questionOrder: Int): SavedResponse {
        return SavedResponse(
            questionId = questionId,
            selectedOptionId = "option_$numericValue",
            numericValue = numericValue,
            questionOrder = questionOrder,
            answeredAt = questionOrder.toLong()
        )
    }

    private companion object {
        val TEST_QUESTIONNAIRE_JSON = """
            {
              "version": "malkuth-v1",
              "title": {
                "en": "Malkuth reflection",
                "es": "Reflexion de Malkuth"
              },
              "responseScale": [
                { "id": "strongly_disagree", "label": { "en": "Strongly disagree", "es": "Muy en desacuerdo" }, "numericValue": 0 },
                { "id": "disagree", "label": { "en": "Disagree", "es": "En desacuerdo" }, "numericValue": 1 },
                { "id": "neither", "label": { "en": "Neither agree nor disagree", "es": "Ni de acuerdo ni en desacuerdo" }, "numericValue": 2 },
                { "id": "agree", "label": { "en": "Agree", "es": "De acuerdo" }, "numericValue": 3 },
                { "id": "strongly_agree", "label": { "en": "Strongly agree", "es": "Muy de acuerdo" }, "numericValue": 4 }
              ],
              "sections": [
                {
                  "sephiraId": "MALKUTH",
                  "displayName": { "en": "Malkuth", "es": "Malkuth" },
                  "shortMeaning": {
                    "en": "Malkuth represents your relationship with the material world. In psychological terms, it reflects how you relate to the body, daily life, possessions, and practical reality.",
                    "es": "Malkuth representa tu relacion con el mundo material. En terminos psicologicos, refleja como te relacionas con el cuerpo, la vida cotidiana, las posesiones y la realidad practica."
                  },
                  "introText": {
                    "en": "This section explores how you relate to material life through money, possessions, health, and the body. It looks for balance, deficiency, and excess without reducing you to a fixed label.",
                    "es": "Esta seccion explora como te relacionas con la vida material a traves del dinero, las posesiones, la salud y el cuerpo. Busca balance, carencia y exceso sin reducirte a una etiqueta fija."
                  },
                  "pages": [
                    {
                      "id": "malkuth_resources",
                      "title": { "en": "Money, riches, and possessions", "es": "Dinero, riqueza y posesiones" },
                      "description": { "en": "Reflect on how you care for resources, security, and material stability.", "es": "Reflexiona sobre como cuidas tus recursos, tu seguridad y tu estabilidad material." },
                      "questionIds": ["malkuth_resources_excess", "malkuth_resources_deficiency", "malkuth_resources_balance"]
                    },
                    {
                      "id": "malkuth_body",
                      "title": { "en": "Body, health, and diet", "es": "Cuerpo, salud y dieta" },
                      "description": { "en": "Reflect on the way you care for your body through daily physical habits.", "es": "Reflexiona sobre la forma en que cuidas tu cuerpo a traves de habitos fisicos cotidianos." },
                      "questionIds": ["malkuth_body_excess", "malkuth_body_deficiency", "malkuth_body_balance"]
                    }
                  ],
                  "questions": [
                    {
                      "id": "malkuth_resources_excess",
                      "sephiraId": "MALKUTH",
                      "pageId": "malkuth_resources",
                      "prompt": { "en": "I spend a great deal of my time and energy trying to accumulate money or possessions.", "es": "Dedico gran parte de mi tiempo y energia a intentar acumular dinero o posesiones." },
                      "format": "LIKERT_5",
                      "targetPole": "EXCESS",
                      "weight": 1.0
                    },
                    {
                      "id": "malkuth_resources_deficiency",
                      "sephiraId": "MALKUTH",
                      "pageId": "malkuth_resources",
                      "prompt": { "en": "I avoid taking care of my money or possessions because I feel other parts of life matter more.", "es": "Evito cuidar mi dinero o mis posesiones porque siento que otras areas de la vida importan mas." },
                      "format": "LIKERT_5",
                      "targetPole": "DEFICIENCY",
                      "weight": 1.0
                    },
                    {
                      "id": "malkuth_resources_balance",
                      "sephiraId": "MALKUTH",
                      "pageId": "malkuth_resources",
                      "prompt": { "en": "I appreciate what I have while still looking for practical ways to improve my material stability.", "es": "Aprecio lo que tengo mientras sigo buscando formas practicas de mejorar mi estabilidad material." },
                      "format": "LIKERT_5",
                      "targetPole": "BALANCE",
                      "weight": 1.0
                    },
                    {
                      "id": "malkuth_body_excess",
                      "sephiraId": "MALKUTH",
                      "pageId": "malkuth_body",
                      "prompt": { "en": "I am very strict with my diet, exercise, or health routines because I feel my body must be carefully controlled.", "es": "Soy muy estricto con mi dieta, el ejercicio o mis rutinas de salud porque siento que mi cuerpo debe estar cuidadosamente controlado." },
                      "format": "LIKERT_5",
                      "targetPole": "EXCESS",
                      "weight": 1.0
                    },
                    {
                      "id": "malkuth_body_deficiency",
                      "sephiraId": "MALKUTH",
                      "pageId": "malkuth_body",
                      "prompt": { "en": "I focus more on enjoying the moment than on keeping track of my diet, exercise, or physical health.", "es": "Me enfoco mas en disfrutar el momento que en llevar un seguimiento de mi dieta, ejercicio o salud fisica." },
                      "format": "LIKERT_5",
                      "targetPole": "DEFICIENCY",
                      "weight": 1.0
                    },
                    {
                      "id": "malkuth_body_balance",
                      "sephiraId": "MALKUTH",
                      "pageId": "malkuth_body",
                      "prompt": { "en": "I try to care for my body through balanced habits and a realistic weekly routine.", "es": "Intento cuidar mi cuerpo a traves de habitos equilibrados y una rutina semanal realista." },
                      "format": "LIKERT_5",
                      "targetPole": "BALANCE",
                      "weight": 1.0
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
    }
}
