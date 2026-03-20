package com.netah.hakkam.numyah.mind.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.netah.hakkam.numyah.mind.data.datasource.FoundationDatabase
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import kotlinx.coroutines.flow.first
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

    private lateinit var db: FoundationDatabase
    private lateinit var repository: LocalAssessmentSessionRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FoundationDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = LocalAssessmentSessionRepository(db.getAssessmentSessionDao())
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
            totalQuestions = 6
        ).first()

        val second = repository.startOrResumeSession(
            questionnaireVersion = "malkuth-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6
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
            totalQuestions = 6
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
            totalQuestions = 6
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
            totalQuestions = 6
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
    fun completeSession_savesScoreAndEndsActiveSession() = runBlocking {
        val session = repository.startOrResumeSession(
            questionnaireVersion = "malkuth-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6
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
}
