package com.netah.hakkam.numyah.mind.di

import com.netah.hakkam.numyah.mind.data.repository.AssessmentContentRepository
import com.netah.hakkam.numyah.mind.data.repository.AssessmentSessionRepository
import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import com.netah.hakkam.numyah.mind.data.repository.LearningContentRepository
import com.netah.hakkam.numyah.mind.domain.usecase.AdvanceAssessmentSectionUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.CompleteAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentExitConfirmationVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetCompletedLearningSectionsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningCatalogUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningCourseUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLearningSectionUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetOnboardingStatusUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetStartupLegalDisclaimerVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.MarkLearningSectionCompletedUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveAssessmentHistoryUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveCompletedAssessmentByIdUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveLatestCompletedAssessmentUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentAnswerUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SaveAssessmentScoreUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentExitConfirmationVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetOnboardingCompletedUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetStartupLegalDisclaimerVisibilityUseCase
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
    fun provideGetStartupLegalDisclaimerVisibilityUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = GetStartupLegalDisclaimerVisibilityUseCase(
        appPreferencesRepository = appPreferencesRepository
    )

    @Singleton
    @Provides
    fun provideSetStartupLegalDisclaimerVisibilityUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = SetStartupLegalDisclaimerVisibilityUseCase(
        appPreferencesRepository = appPreferencesRepository
    )

    @Singleton
    @Provides
    fun provideGetAssessmentHonestyNoticeVisibilityUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = GetAssessmentHonestyNoticeVisibilityUseCase(
        appPreferencesRepository = appPreferencesRepository
    )

    @Singleton
    @Provides
    fun provideGetAssessmentExitConfirmationVisibilityUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = GetAssessmentExitConfirmationVisibilityUseCase(
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
    fun provideSetAssessmentExitConfirmationVisibilityUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = SetAssessmentExitConfirmationVisibilityUseCase(
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
    fun provideGetCompletedLearningSectionsUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = GetCompletedLearningSectionsUseCase(
        appPreferencesRepository = appPreferencesRepository
    )

    @Singleton
    @Provides
    fun provideMarkLearningSectionCompletedUseCase(
        appPreferencesRepository: AppPreferencesRepository
    ) = MarkLearningSectionCompletedUseCase(
        appPreferencesRepository = appPreferencesRepository
    )

    @Singleton
    @Provides
    fun provideGetLearningCatalogUseCase(
        learningContentRepository: LearningContentRepository
    ) = GetLearningCatalogUseCase(
        learningContentRepository = learningContentRepository
    )

    @Singleton
    @Provides
    fun provideGetLearningCourseUseCase(
        learningContentRepository: LearningContentRepository
    ) = GetLearningCourseUseCase(
        learningContentRepository = learningContentRepository
    )

    @Singleton
    @Provides
    fun provideGetLearningSectionUseCase(
        learningContentRepository: LearningContentRepository
    ) = GetLearningSectionUseCase(
        learningContentRepository = learningContentRepository
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
    fun provideObserveAssessmentHistoryUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = ObserveAssessmentHistoryUseCase(
        assessmentSessionRepository = assessmentSessionRepository
    )

    @Singleton
    @Provides
    fun provideObserveCompletedAssessmentByIdUseCase(
        assessmentSessionRepository: AssessmentSessionRepository
    ) = ObserveCompletedAssessmentByIdUseCase(
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
