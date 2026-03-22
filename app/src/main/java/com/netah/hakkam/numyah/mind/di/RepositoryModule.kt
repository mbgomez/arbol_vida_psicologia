package com.netah.hakkam.numyah.mind.di

import com.netah.hakkam.numyah.mind.data.local.database.AssessmentSessionDao
import com.netah.hakkam.numyah.mind.data.local.database.QuestionnaireContentDao
import com.netah.hakkam.numyah.mind.data.local.content.JsonAssessmentContentDataSource
import com.netah.hakkam.numyah.mind.data.local.content.JsonLearningContentDataSource
import com.netah.hakkam.numyah.mind.data.repository.AssessmentContentRepository
import com.netah.hakkam.numyah.mind.data.repository.AssessmentSessionRepository
import com.netah.hakkam.numyah.mind.data.repository.LearningContentRepository
import com.netah.hakkam.numyah.mind.data.repository.LocalAssessmentContentRepository
import com.netah.hakkam.numyah.mind.data.repository.LocalAssessmentSessionRepository
import com.netah.hakkam.numyah.mind.data.repository.LocalLearningContentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideAssessmentContentRepository(
        jsonAssessmentContentDataSource: JsonAssessmentContentDataSource,
        questionnaireContentDao: QuestionnaireContentDao
    ): AssessmentContentRepository = LocalAssessmentContentRepository(
        jsonAssessmentContentDataSource = jsonAssessmentContentDataSource,
        questionnaireContentDao = questionnaireContentDao
    )

    @Singleton
    @Provides
    fun provideAssessmentSessionRepository(
        assessmentSessionDao: AssessmentSessionDao
    ): AssessmentSessionRepository = LocalAssessmentSessionRepository(
        assessmentSessionDao = assessmentSessionDao
    )

    @Singleton
    @Provides
    fun provideLearningContentRepository(
        jsonLearningContentDataSource: JsonLearningContentDataSource
    ): LearningContentRepository = LocalLearningContentRepository(
        jsonLearningContentDataSource = jsonLearningContentDataSource
    )

}
