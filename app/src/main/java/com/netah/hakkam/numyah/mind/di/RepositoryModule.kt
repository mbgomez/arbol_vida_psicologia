package com.netah.hakkam.numyah.mind.di


import com.netah.hakkam.numyah.mind.data.datasource.local.PostDao
import com.netah.hakkam.numyah.mind.data.datasource.local.AssessmentSessionDao
import com.netah.hakkam.numyah.mind.data.datasource.local.QuestionnaireContentDao
import com.netah.hakkam.numyah.mind.data.datasource.remote.InternetConnectionChecker
import com.netah.hakkam.numyah.mind.data.local.content.JsonAssessmentContentDataSource
import com.netah.hakkam.numyah.mind.data.repository.AssessmentContentRepository
import com.netah.hakkam.numyah.mind.data.repository.AssessmentSessionRepository
import com.netah.hakkam.numyah.mind.data.repository.LocalAssessmentContentRepository
import com.netah.hakkam.numyah.mind.data.repository.LocalAssessmentSessionRepository
import com.netah.hakkam.numyah.mind.data.datasource.remote.PostService
import com.netah.hakkam.numyah.mind.data.repository.IPostRepository
import com.netah.hakkam.numyah.mind.data.repository.PostRepository
import com.netah.hakkam.numyah.mind.mapper.PostMapper
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
    fun providePostRepository(
        postService: PostService,
        internetConnectionChecker: InternetConnectionChecker,
        postDao: PostDao,
        postMapper: PostMapper
    ): IPostRepository = PostRepository(
        postService = postService,
        internetConnectionChecker = internetConnectionChecker,
        postDao = postDao,
        postMapper = postMapper
    )

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

}
