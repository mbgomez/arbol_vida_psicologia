package com.netah.hakkam.numyah.mind.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.SephiraId

@Entity(
    indices = [
        Index(value = ["status", "isActive"])
    ]
)
data class AssessmentSessionTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionnaireVersion: String,
    val status: AssessmentStatus,
    val currentSephiraId: SephiraId,
    val currentPageIndex: Int,
    val currentQuestionIndex: Int,
    val totalQuestions: Int,
    val startedAt: Long,
    val completedAt: Long? = null,
    val isActive: Boolean = true
)

@Entity(
    primaryKeys = ["sessionId", "questionId"],
    foreignKeys = [
        ForeignKey(
            entity = AssessmentSessionTable::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId")
    ]
)
data class ResponseTable(
    val sessionId: Long,
    val questionId: String,
    val selectedOptionId: String,
    val numericValue: Int,
    val questionOrder: Int,
    val answeredAt: Long
)

@Entity(
    primaryKeys = ["sessionId", "sephiraId"],
    foreignKeys = [
        ForeignKey(
            entity = AssessmentSessionTable::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId")
    ]
)
data class SephiraScoreTable(
    val sessionId: Long,
    val sephiraId: SephiraId,
    val balanceScore: Double,
    val deficiencyScore: Double,
    val excessScore: Double,
    val dominantPole: Pole,
    val confidence: ConfidenceLevel,
    val isLowConfidence: Boolean
)

@Entity
data class QuestionnaireTable(
    @PrimaryKey
    val version: String,
    val titleEn: String,
    val titleEs: String
)

@Entity(
    primaryKeys = ["questionnaireVersion", "optionId"],
    foreignKeys = [
        ForeignKey(
            entity = QuestionnaireTable::class,
            parentColumns = ["version"],
            childColumns = ["questionnaireVersion"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("questionnaireVersion")
    ]
)
data class AnswerOptionTable(
    val questionnaireVersion: String,
    val optionId: String,
    val labelEn: String,
    val labelEs: String,
    val numericValue: Int,
    val displayOrder: Int
)

@Entity(
    primaryKeys = ["questionnaireVersion", "sephiraId"],
    foreignKeys = [
        ForeignKey(
            entity = QuestionnaireTable::class,
            parentColumns = ["version"],
            childColumns = ["questionnaireVersion"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("questionnaireVersion")
    ]
)
data class SephiraSectionTable(
    val questionnaireVersion: String,
    val sephiraId: SephiraId,
    val displayNameEn: String,
    val displayNameEs: String,
    val shortMeaningEn: String,
    val shortMeaningEs: String,
    val introTextEn: String,
    val introTextEs: String,
    val displayOrder: Int
)

@Entity(
    primaryKeys = ["questionnaireVersion", "pageId"],
    foreignKeys = [
        ForeignKey(
            entity = QuestionnaireTable::class,
            parentColumns = ["version"],
            childColumns = ["questionnaireVersion"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("questionnaireVersion"),
        Index(value = ["questionnaireVersion", "sephiraId"])
    ]
)
data class QuestionPageTable(
    val questionnaireVersion: String,
    val pageId: String,
    val sephiraId: SephiraId,
    val titleEn: String,
    val titleEs: String,
    val descriptionEn: String,
    val descriptionEs: String,
    val displayOrder: Int
)

@Entity(
    primaryKeys = ["questionnaireVersion", "questionId"],
    foreignKeys = [
        ForeignKey(
            entity = QuestionnaireTable::class,
            parentColumns = ["version"],
            childColumns = ["questionnaireVersion"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("questionnaireVersion"),
        Index(value = ["questionnaireVersion", "sephiraId"]),
        Index(value = ["questionnaireVersion", "pageId"])
    ]
)
data class QuestionTable(
    val questionnaireVersion: String,
    val questionId: String,
    val sephiraId: SephiraId,
    val pageId: String,
    val promptEn: String,
    val promptEs: String,
    val format: QuestionFormat,
    val targetPole: Pole,
    val weight: Double,
    val displayOrder: Int
)
