package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingStatusUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<Boolean>() {
    override fun buildUseCase(): Flow<Boolean> {
        return appPreferencesRepository.hasCompletedOnboarding()
    }
}

class SetOnboardingCompletedUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<Boolean, Boolean>() {
    override fun buildUseCase(params: Boolean): Flow<Boolean> {
        return appPreferencesRepository.setOnboardingCompleted(params)
    }
}

class GetAssessmentHonestyNoticeVisibilityUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<Boolean>() {
    override fun buildUseCase(): Flow<Boolean> {
        return appPreferencesRepository.shouldShowAssessmentHonestyNotice()
    }
}

class SetAssessmentHonestyNoticeVisibilityUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<Boolean, Boolean>() {
    override fun buildUseCase(params: Boolean): Flow<Boolean> {
        return appPreferencesRepository.setAssessmentHonestyNoticeVisible(params)
    }
}
