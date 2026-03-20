package com.netah.hakkam.numyah.mind.data.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netah.hakkam.numyah.mind.data.datasource.local.AssessmentSessionDao
import com.netah.hakkam.numyah.mind.data.datasource.local.AssessmentSessionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.AnswerOptionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.PostDao
import com.netah.hakkam.numyah.mind.data.datasource.local.PostTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionPageTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireContentDao
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireTable
import com.netah.hakkam.numyah.mind.data.datasource.local.ResponseTable
import com.netah.hakkam.numyah.mind.data.datasource.local.RoomTypeConverters
import com.netah.hakkam.numyah.mind.data.datasource.local.SephiraSectionTable
import com.netah.hakkam.numyah.mind.data.datasource.local.SephiraScoreTable

const val databaseName = "FoundationDatabase"

@Database(
    entities = [
        PostTable::class,
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
abstract class FoundationDatabase : RoomDatabase() {

    abstract fun getPostDao(): PostDao
    abstract fun getAssessmentSessionDao(): AssessmentSessionDao
    abstract fun getQuestionnaireContentDao(): QuestionnaireContentDao

}

object DatabaseFactory {
    fun getDBInstance(context: Context) =
        Room.databaseBuilder(context, FoundationDatabase::class.java, databaseName)
            .fallbackToDestructiveMigration()
            .build()
}
