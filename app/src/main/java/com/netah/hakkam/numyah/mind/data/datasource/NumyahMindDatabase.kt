package com.netah.hakkam.numyah.mind.data.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netah.hakkam.numyah.mind.data.datasource.local.AssessmentSessionDao
import com.netah.hakkam.numyah.mind.data.datasource.local.AssessmentSessionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.AnswerOptionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionPageTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireContentDao
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireTable
import com.netah.hakkam.numyah.mind.data.datasource.local.ResponseTable
import com.netah.hakkam.numyah.mind.data.datasource.local.RoomTypeConverters
import com.netah.hakkam.numyah.mind.data.datasource.local.SephiraSectionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.SephiraScoreTable

private const val DATABASE_NAME = "numyah_mind.db"

@Database(
    entities = [
        AssessmentSessionTable::class,
        ResponseTable::class,
        SephiraScoreTable::class,
        QuestionnaireTable::class,
        AnswerOptionTable::class,
        SephiraSectionTable::class,
        QuestionPageTable::class,
        QuestionTable::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class NumyahMindDatabase : RoomDatabase() {

    abstract fun getAssessmentSessionDao(): AssessmentSessionDao
    abstract fun getQuestionnaireContentDao(): QuestionnaireContentDao

}

object DatabaseFactory {
    fun getDBInstance(context: Context) =
        Room.databaseBuilder(context, NumyahMindDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
}
