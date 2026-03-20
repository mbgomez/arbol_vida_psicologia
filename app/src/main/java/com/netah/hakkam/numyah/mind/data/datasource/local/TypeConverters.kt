package com.netah.hakkam.numyah.mind.data.datasource.local

import androidx.room.TypeConverter
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.SephiraId

class RoomTypeConverters {

    @TypeConverter
    fun fromAssessmentStatus(value: AssessmentStatus): String = value.name

    @TypeConverter
    fun toAssessmentStatus(value: String): AssessmentStatus = AssessmentStatus.valueOf(value)

    @TypeConverter
    fun fromSephiraId(value: SephiraId): String = value.name

    @TypeConverter
    fun toSephiraId(value: String): SephiraId = SephiraId.valueOf(value)

    @TypeConverter
    fun fromPole(value: Pole): String = value.name

    @TypeConverter
    fun toPole(value: String): Pole = Pole.valueOf(value)

    @TypeConverter
    fun fromQuestionFormat(value: QuestionFormat): String = value.name

    @TypeConverter
    fun toQuestionFormat(value: String): QuestionFormat = QuestionFormat.valueOf(value)

    @TypeConverter
    fun fromConfidenceLevel(value: ConfidenceLevel): String = value.name

    @TypeConverter
    fun toConfidenceLevel(value: String): ConfidenceLevel = ConfidenceLevel.valueOf(value)
}
