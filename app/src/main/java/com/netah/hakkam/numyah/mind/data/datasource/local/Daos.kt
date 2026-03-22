package com.netah.hakkam.numyah.mind.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AssessmentSessionTable): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: ResponseTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: SephiraScoreTable)

    @Query("UPDATE assessmentsessiontable SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateActiveSessions()

    @Query("SELECT * FROM assessmentsessiontable WHERE isActive = 1 AND status = 'IN_PROGRESS' ORDER BY startedAt DESC LIMIT 1")
    fun observeActiveInProgressSession(): Flow<AssessmentSessionTable?>

    @Query("SELECT * FROM assessmentsessiontable WHERE status = 'COMPLETED' ORDER BY completedAt DESC LIMIT 1")
    fun observeLatestCompletedSession(): Flow<AssessmentSessionTable?>

    @Query("SELECT * FROM assessmentsessiontable WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun observeCompletedSessions(): Flow<List<AssessmentSessionTable>>

    @Query("SELECT * FROM assessmentsessiontable WHERE id = :sessionId AND status = 'COMPLETED' LIMIT 1")
    fun observeCompletedSession(sessionId: Long): Flow<AssessmentSessionTable?>

    @Query("SELECT * FROM assessmentsessiontable WHERE id = :sessionId LIMIT 1")
    fun observeSession(sessionId: Long): Flow<AssessmentSessionTable?>

    @Query("SELECT * FROM assessmentsessiontable WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): AssessmentSessionTable?

    @Query("SELECT * FROM responsetable WHERE sessionId = :sessionId ORDER BY questionOrder ASC")
    fun observeResponses(sessionId: Long): Flow<List<ResponseTable>>

    @Query("SELECT * FROM sephirascoretable WHERE sessionId = :sessionId")
    fun observeScores(sessionId: Long): Flow<List<SephiraScoreTable>>

    @Query("SELECT * FROM assessmentsessiontable WHERE isActive = 1 AND status = 'IN_PROGRESS' ORDER BY startedAt DESC LIMIT 1")
    suspend fun getActiveInProgressSession(): AssessmentSessionTable?

    @Query("SELECT * FROM assessmentsessiontable WHERE status = 'COMPLETED' ORDER BY completedAt DESC LIMIT 1")
    suspend fun getLatestCompletedSession(): AssessmentSessionTable?

    @Query("SELECT * FROM responsetable WHERE sessionId = :sessionId ORDER BY questionOrder ASC")
    suspend fun getResponsesForSession(sessionId: Long): List<ResponseTable>

    @Query("SELECT * FROM sephirascoretable WHERE sessionId = :sessionId")
    suspend fun getScoresForSession(sessionId: Long): List<SephiraScoreTable>

    @Query("UPDATE assessmentsessiontable SET currentPageIndex = :pageIndex, currentQuestionIndex = :questionIndex WHERE id = :sessionId")
    suspend fun updateProgress(sessionId: Long, pageIndex: Int, questionIndex: Int)

    @Query(
        "UPDATE assessmentsessiontable " +
            "SET currentSephiraId = :sephiraId, currentPageIndex = :pageIndex, currentQuestionIndex = :questionIndex, totalQuestions = :totalQuestions " +
            "WHERE id = :sessionId"
    )
    suspend fun advanceToSephira(
        sessionId: Long,
        sephiraId: com.netah.hakkam.numyah.mind.domain.model.SephiraId,
        pageIndex: Int,
        questionIndex: Int,
        totalQuestions: Int
    )

    @Query("UPDATE assessmentsessiontable SET status = :status, completedAt = :completedAt, isActive = :isActive WHERE id = :sessionId")
    suspend fun updateCompletion(sessionId: Long, status: AssessmentStatus, completedAt: Long, isActive: Boolean)
}

@Dao
interface QuestionnaireContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionnaire(questionnaire: QuestionnaireTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerOptions(options: List<AnswerOptionTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<SephiraSectionTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<QuestionPageTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionTable>)

    @Query("SELECT * FROM questionnairetable ORDER BY version DESC LIMIT 1")
    suspend fun getLatestQuestionnaire(): QuestionnaireTable?

    @Query("SELECT * FROM answeroptiontable WHERE questionnaireVersion = :version ORDER BY displayOrder ASC")
    suspend fun getAnswerOptions(version: String): List<AnswerOptionTable>

    @Query("SELECT * FROM sephirasectiontable WHERE questionnaireVersion = :version ORDER BY displayOrder ASC")
    suspend fun getSections(version: String): List<SephiraSectionTable>

    @Query("SELECT * FROM questionpagetable WHERE questionnaireVersion = :version ORDER BY displayOrder ASC")
    suspend fun getPages(version: String): List<QuestionPageTable>

    @Query("SELECT * FROM questiontable WHERE questionnaireVersion = :version ORDER BY displayOrder ASC")
    suspend fun getQuestions(version: String): List<QuestionTable>

    @Query("DELETE FROM answeroptiontable WHERE questionnaireVersion = :version")
    suspend fun deleteAnswerOptions(version: String)

    @Query("DELETE FROM questiontable WHERE questionnaireVersion = :version")
    suspend fun deleteQuestions(version: String)

    @Query("DELETE FROM questionpagetable WHERE questionnaireVersion = :version")
    suspend fun deletePages(version: String)

    @Query("DELETE FROM sephirasectiontable WHERE questionnaireVersion = :version")
    suspend fun deleteSections(version: String)

    @Query("DELETE FROM questionnairetable WHERE version = :version")
    suspend fun deleteQuestionnaire(version: String)
}
