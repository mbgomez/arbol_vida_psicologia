package com.netah.hakkam.numyah.mind.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.netah.hakkam.numyah.mind.data.local.content.JsonAssessmentContentDataSource
import com.netah.hakkam.numyah.mind.data.local.content.LocalizedText
import com.netah.hakkam.numyah.mind.data.local.content.SeedQuestion
import com.netah.hakkam.numyah.mind.data.local.content.SeedQuestionPage
import com.netah.hakkam.numyah.mind.data.local.content.SeedQuestionnaire
import com.netah.hakkam.numyah.mind.data.local.content.SeedSephiraSection
import com.netah.hakkam.numyah.mind.data.local.database.NumyahMindDatabase
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LocalAssessmentSessionRepositoryTest {

    private lateinit var db: NumyahMindDatabase
    private lateinit var repository: LocalAssessmentSessionRepository
    private lateinit var appPreferencesRepository: AppPreferencesRepository
    private lateinit var jsonAssessmentContentDataSource: JsonAssessmentContentDataSource

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NumyahMindDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        appPreferencesRepository = mockk(relaxed = true)
        jsonAssessmentContentDataSource = mockk(relaxed = true)
        every { appPreferencesRepository.shouldUseMockHistory() } returns flowOf(false)
        every { jsonAssessmentContentDataSource.getCurrentQuestionnaire() } returns testSeedQuestionnaire()
        repository = LocalAssessmentSessionRepository(
            assessmentSessionDao = db.getAssessmentSessionDao(),
            appPreferencesRepository = appPreferencesRepository,
            jsonAssessmentContentDataSource = jsonAssessmentContentDataSource
        )
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun startOrResumeSession_createsSingleActiveSession() = runBlocking {
        val first = repository.startOrResumeSession(
            questionnaireVersion = "malkuth-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        val second = repository.startOrResumeSession(
            questionnaireVersion = "malkuth-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        assertNotNull(first)
        assertEquals(first.sessionId, second.sessionId)
        assertEquals(0, second.currentQuestionIndex)
        assertEquals(6, second.totalQuestions)
    }

    @Test
    fun saveAnswer_persistsResponseAndProgress() = runBlocking {
        val session = repository.startOrResumeSession(
            questionnaireVersion = "malkuth-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        val updated = repository.saveAnswer(
            sessionId = session.sessionId,
            questionId = "malkuth_resources_excess",
            selectedOptionId = "agree",
            numericValue = 3,
            questionOrder = 0,
            nextPageIndex = 0,
            nextQuestionIndex = 1
        ).first()

        assertEquals(1, updated.responses.size)
        assertEquals("malkuth_resources_excess", updated.responses.first().questionId)
        assertEquals(1, updated.currentQuestionIndex)
    }

    @Test
    fun advanceToSephira_updatesActiveSectionPosition() = runBlocking {
        val session = repository.startOrResumeSession(
            questionnaireVersion = "tree-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        val updated = repository.advanceToSephira(
            sessionId = session.sessionId,
            sephiraId = SephiraId.YESOD,
            totalQuestions = 6
        ).first()

        assertEquals(SephiraId.YESOD, updated.currentSephiraId)
        assertEquals(0, updated.currentPageIndex)
        assertEquals(0, updated.currentQuestionIndex)
        assertEquals(6, updated.totalQuestions)
    }

    @Test
    fun observeLatestCompletedSession_returnsMostRecentCompletedSnapshot() = runBlocking {
        val session = repository.startOrResumeSession(
            questionnaireVersion = "tree-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        val score = SephiraScore(
            sessionId = session.sessionId,
            sephiraId = SephiraId.MALKUTH,
            balanceScore = 0.65,
            deficiencyScore = 0.20,
            excessScore = 0.15,
            dominantPole = Pole.BALANCE,
            confidence = ConfidenceLevel.HIGH,
            isLowConfidence = false
        )

        repository.completeSession(
            sessionId = session.sessionId,
            score = score
        ).first()

        val latest = repository.observeLatestCompletedSession().first()

        assertNotNull(latest)
        assertEquals(session.sessionId, latest?.sessionId)
        assertEquals(Pole.BALANCE, latest?.scores?.first()?.dominantPole)
    }

    @Test
    fun observeCompletedSessions_returnsCompletedHistoryInDescendingOrder() = runBlocking {
        val olderSession = repository.startOrResumeSession(
            questionnaireVersion = "tree-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()
        repository.completeSession(
            sessionId = olderSession.sessionId,
            score = completedScore(olderSession.sessionId, SephiraId.MALKUTH, Pole.BALANCE)
        ).first()
        Thread.sleep(5)

        val newerSession = repository.startOrResumeSession(
            questionnaireVersion = "tree-v1",
            initialSephiraId = SephiraId.YESOD,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()
        repository.completeSession(
            sessionId = newerSession.sessionId,
            score = completedScore(newerSession.sessionId, SephiraId.YESOD, Pole.DEFICIENCY)
        ).first()

        val history = repository.observeCompletedSessions().first()

        assertEquals(listOf(newerSession.sessionId, olderSession.sessionId), history.map { it.sessionId })
        assertEquals(SephiraId.YESOD, history.first().scores.first().sephiraId)
    }

    @Test
    fun observeCompletedSession_returnsRequestedSavedAssessment() = runBlocking {
        val session = repository.startOrResumeSession(
            questionnaireVersion = "tree-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        repository.completeSession(
            sessionId = session.sessionId,
            score = completedScore(session.sessionId, SephiraId.MALKUTH, Pole.EXCESS)
        ).first()

        val saved = repository.observeCompletedSession(session.sessionId).first()

        assertNotNull(saved)
        assertEquals(session.sessionId, saved?.sessionId)
        assertEquals(Pole.EXCESS, saved?.scores?.first()?.dominantPole)
    }

    @Test
    fun observeCompletedSessions_returnsMockHistoryWhenEnabled() = runBlocking {
        every { appPreferencesRepository.shouldUseMockHistory() } returns flowOf(true)

        val history = repository.observeCompletedSessions().first()

        assertEquals(10, history.size)
        assertEquals(9_000_000L, history.first().sessionId)
        assertEquals(2, history.first().scores.size)
    }

    @Test
    fun completeSession_savesScoreAndEndsActiveSession() = runBlocking {
        val session = repository.startOrResumeSession(
            questionnaireVersion = "malkuth-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        repository.saveAnswer(
            sessionId = session.sessionId,
            questionId = "malkuth_resources_excess",
            selectedOptionId = "agree",
            numericValue = 3,
            questionOrder = 0,
            nextPageIndex = 0,
            nextQuestionIndex = 1
        ).first()

        val completed = repository.completeSession(
            sessionId = session.sessionId,
            score = SephiraScore(
                sessionId = session.sessionId,
                sephiraId = SephiraId.MALKUTH,
                balanceScore = 0.65,
                deficiencyScore = 0.20,
                excessScore = 0.15,
                dominantPole = Pole.BALANCE,
                confidence = ConfidenceLevel.HIGH,
                isLowConfidence = false
            )
        ).first()

        assertFalse(completed.scores.isEmpty())
        assertEquals(Pole.BALANCE, completed.scores.first().dominantPole)
        assertEquals(1, completed.responses.size)
        assertTrue(repository.observeActiveSession().first() == null)
    }

    @Test
    fun startOrResumeSession_forceStartFresh_replacesExistingInProgressSession() = runBlocking {
        val first = repository.startOrResumeSession(
            questionnaireVersion = "tree-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = false
        ).first()

        repository.saveAnswer(
            sessionId = first.sessionId,
            questionId = "m1",
            selectedOptionId = "agree",
            numericValue = 3,
            questionOrder = 0,
            nextPageIndex = 0,
            nextQuestionIndex = 0
        ).first()

        val fresh = repository.startOrResumeSession(
            questionnaireVersion = "tree-v1",
            initialSephiraId = SephiraId.YESOD,
            totalQuestions = 6,
            forceStartFresh = true
        ).first()

        assertTrue(fresh.sessionId != first.sessionId)
        assertEquals(SephiraId.YESOD, fresh.currentSephiraId)
        assertTrue(fresh.responses.isEmpty())
        assertEquals(null, db.getAssessmentSessionDao().getSessionById(first.sessionId))
    }

    private fun completedScore(
        sessionId: Long,
        sephiraId: SephiraId,
        dominantPole: Pole
    ) = SephiraScore(
        sessionId = sessionId,
        sephiraId = sephiraId,
        balanceScore = 0.65,
        deficiencyScore = 0.20,
        excessScore = 0.15,
        dominantPole = dominantPole,
        confidence = ConfidenceLevel.HIGH,
        isLowConfidence = false
    )

    private fun testSeedQuestionnaire() = SeedQuestionnaire(
        version = "tree-v1",
        title = LocalizedText(en = "Tree", es = "Arbol"),
        responseScale = emptyList(),
        sections = listOf(
            SeedSephiraSection(
                sephiraId = SephiraId.MALKUTH,
                displayName = LocalizedText("Malkuth", "Malkuth"),
                shortMeaning = LocalizedText("Grounding", "Enraizamiento"),
                introText = LocalizedText("Intro", "Intro"),
                pages = listOf(
                    SeedQuestionPage(
                        id = "m1_page",
                        title = LocalizedText("Page", "Pagina"),
                        description = LocalizedText("Desc", "Desc"),
                        questionIds = listOf("m1")
                    )
                ),
                questions = listOf(
                    SeedQuestion(
                        id = "m1",
                        sephiraId = SephiraId.MALKUTH,
                        pageId = "m1_page",
                        prompt = LocalizedText("Prompt", "Pregunta"),
                        format = QuestionFormat.LIKERT_5,
                        targetPole = Pole.BALANCE
                    )
                )
            ),
            SeedSephiraSection(
                sephiraId = SephiraId.YESOD,
                displayName = LocalizedText("Yesod", "Yesod"),
                shortMeaning = LocalizedText("Bond", "Vinculo"),
                introText = LocalizedText("Intro", "Intro"),
                pages = listOf(
                    SeedQuestionPage(
                        id = "y1_page",
                        title = LocalizedText("Page", "Pagina"),
                        description = LocalizedText("Desc", "Desc"),
                        questionIds = listOf("y1")
                    )
                ),
                questions = listOf(
                    SeedQuestion(
                        id = "y1",
                        sephiraId = SephiraId.YESOD,
                        pageId = "y1_page",
                        prompt = LocalizedText("Prompt", "Pregunta"),
                        format = QuestionFormat.LIKERT_5,
                        targetPole = Pole.DEFICIENCY
                    )
                )
            )
        )
    )
}
