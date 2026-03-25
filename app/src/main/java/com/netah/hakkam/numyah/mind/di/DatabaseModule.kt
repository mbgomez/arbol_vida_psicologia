package com.netah.hakkam.numyah.mind.di

import android.content.Context
import com.netah.hakkam.numyah.mind.data.datasource.local.AssessmentSessionDao
import com.netah.hakkam.numyah.mind.data.local.database.NumyahMindDatabase
import com.netah.hakkam.numyah.mind.data.local.database.NumyahMindDatabaseFactory
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireContentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = NumyahMindDatabaseFactory.getDBInstance(context = context)

    @Singleton
    @Provides
    fun provideAssessmentSessionDao(db: NumyahMindDatabase): AssessmentSessionDao =
        db.getAssessmentSessionDao()

    @Singleton
    @Provides
    fun provideQuestionnaireContentDao(db: NumyahMindDatabase): QuestionnaireContentDao =
        db.getQuestionnaireContentDao()

}
