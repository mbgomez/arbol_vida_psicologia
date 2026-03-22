package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
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

class GetLanguageModeUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<AppLanguageMode>() {
    override fun buildUseCase(): Flow<AppLanguageMode> {
        return appPreferencesRepository.getLanguageMode()
    }
}

class SetLanguageModeUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<AppLanguageMode, AppLanguageMode>() {
    override fun buildUseCase(params: AppLanguageMode): Flow<AppLanguageMode> {
        return appPreferencesRepository.setLanguageMode(params)
    }
}

class GetThemeModeUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<AppThemeMode>() {
    override fun buildUseCase(): Flow<AppThemeMode> {
        return appPreferencesRepository.getThemeMode()
    }
}

class SetThemeModeUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<AppThemeMode, AppThemeMode>() {
    override fun buildUseCase(params: AppThemeMode): Flow<AppThemeMode> {
        return appPreferencesRepository.setThemeMode(params)
    }
}
