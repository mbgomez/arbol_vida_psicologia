package com.netah.hakkam.numyah.mind.di


import com.netah.hakkam.numyah.mind.data.repository.AssessmentContentRepository
import com.netah.hakkam.numyah.mind.data.repository.AssessmentSessionRepository
import com.netah.hakkam.numyah.mind.data.repository.IPostRepository
import com.netah.hakkam.numyah.mind.data.repository.OnboardingRepository
import com.netah.hakkam.numyah.mind.domain.usecase.CompleteAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCachedPostsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetOnboardingStatusUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetPostsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentAnswerUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetOnboardingCompletedUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.StartOrResumeAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.UpdateAssessmentProgressUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideGetPostsUseCase(postRepository: IPostRepository) =
        GetPostsUseCase(postRepository = postRepository)

    @Singleton
    @Provides
    fun provideGetCachedPostsUseCase(postRepository: IPostRepository) =
        GetCachedPostsUseCase(postRepository = postRepository)

    @Singleton
    @Provides
    fun provideGetOnboardingStatusUseCase(onboardingRepository: OnboardingRepository) =
        GetOnboardingStatusUseCase(onboardingRepository = onboardingRepository)

    @Singleton
    @Provides
    fun provideSetOnboardingCompletedUseCase(onboardingRepository: OnboardingRepository) =
        SetOnboardingCompletedUseCase(onboardingRepository = onboardingRepository)

    @Singleton
    @Provides
    fun provideGetCurrentQuestionnaireUseCase(
        assessmentContentRepository: AssessmentContentRepository
    ) = GetCurrentQuestionnaireUseCase(
        assessmentContentRepository = assessmentContentRepository
    )

    @Singleton
    @Provides
    fun provideStartOrResumeAssessmentUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = StartOrResumeAssessmentUseCase(
        assessmentSessionRepository = assessmentSessionRepository
    )

    @Singleton
    @Provides
    fun provideObserveActiveAssessmentUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = ObserveActiveAssessmentUseCase(
        assessmentSessionRepository = assessmentSessionRepository
    )

    @Singleton
    @Provides
    fun provideSaveAssessmentAnswerUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = SaveAssessmentAnswerUseCase(
        assessmentSessionRepository = assessmentSessionRepository
    )

    @Singleton
    @Provides
    fun provideUpdateAssessmentProgressUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = UpdateAssessmentProgressUseCase(
        assessmentSessionRepository = assessmentSessionRepository
    )

    @Singleton
    @Provides
    fun provideCompleteAssessmentUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = CompleteAssessmentUseCase(
        assessmentSessionRepository = assessmentSessionRepository
    )

}
