package com.netah.hakkam.numyah.mind.di
import com.netah.hakkam.numyah.mind.data.repository.AssessmentContentRepository
import com.netah.hakkam.numyah.mind.data.repository.AssessmentSessionRepository
import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import com.netah.hakkam.numyah.mind.domain.usecase.AdvanceAssessmentSectionUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.CompleteAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetOnboardingStatusUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentAnswerUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentScoreUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentHonestyNoticeVisibilityUseCase
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
    fun provideGetOnboardingStatusUseCase(appPreferencesRepository: AppPreferencesRepository) =
        GetOnboardingStatusUseCase(appPreferencesRepository = appPreferencesRepository)

    @Singleton
    @Provides
    fun provideSetOnboardingCompletedUseCase(appPreferencesRepository: AppPreferencesRepository) =
        SetOnboardingCompletedUseCase(appPreferencesRepository = appPreferencesRepository)

    @Singleton
    @Provides
    fun provideGetAssessmentHonestyNoticeVisibilityUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = GetAssessmentHonestyNoticeVisibilityUseCase(
        appPreferencesRepository = appPreferencesRepository
    )

    @Singleton
    @Provides
    fun provideSetAssessmentHonestyNoticeVisibilityUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = SetAssessmentHonestyNoticeVisibilityUseCase(
        appPreferencesRepository = appPreferencesRepository
    )

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
    fun provideObserveLatestCompletedAssessmentUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = ObserveLatestCompletedAssessmentUseCase(
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
    fun provideSaveAssessmentScoreUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = SaveAssessmentScoreUseCase(
        assessmentSessionRepository = assessmentSessionRepository
    )

    @Singleton
    @Provides
    fun provideAdvanceAssessmentSectionUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = AdvanceAssessmentSectionUseCase(
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
