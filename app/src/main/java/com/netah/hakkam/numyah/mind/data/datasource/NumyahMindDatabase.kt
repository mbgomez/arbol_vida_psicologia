package com.netah.hakkam.numyah.mind.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DATABASE_NAME = "numyah_mind.db"

@Database(
    entities = [
        AssessmentSessionTable::class,
        ResponseTable::class,
        SephiraScoreTable::class,
        QuestionnaireTable::class,
        AnswerOptionTable::class,
        SephiraSectionTable::class,
        SephiraPracticeTable::class,
        QuestionPageTable::class,
        QuestionTable::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class NumyahMindDatabase : RoomDatabase() {

    abstract fun getAssessmentSessionDao(): AssessmentSessionDao
    abstract fun getQuestionnaireContentDao(): QuestionnaireContentDao

}

object NumyahMindDatabaseFactory {
    fun getDBInstance(context: Context) =
        Room.databaseBuilder(context, NumyahMindDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
}
