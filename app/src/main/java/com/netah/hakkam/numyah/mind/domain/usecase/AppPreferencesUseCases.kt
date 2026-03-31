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

class GetStartupLegalDisclaimerVisibilityUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<Boolean>() {
    override fun buildUseCase(): Flow<Boolean> {
        return appPreferencesRepository.shouldShowStartupLegalDisclaimer()
    }
}

class SetStartupLegalDisclaimerVisibilityUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<Boolean, Boolean>() {
    override fun buildUseCase(params: Boolean): Flow<Boolean> {
        return appPreferencesRepository.setStartupLegalDisclaimerVisible(params)
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

class GetAssessmentExitConfirmationVisibilityUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<Boolean>() {
    override fun buildUseCase(): Flow<Boolean> {
        return appPreferencesRepository.shouldShowAssessmentExitConfirmation()
    }
}

class SetAssessmentExitConfirmationVisibilityUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<Boolean, Boolean>() {
    override fun buildUseCase(params: Boolean): Flow<Boolean> {
        return appPreferencesRepository.setAssessmentExitConfirmationVisible(params)
    }
}

class GetMockHistoryModeUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<Boolean>() {
    override fun buildUseCase(): Flow<Boolean> {
        return appPreferencesRepository.shouldUseMockHistory()
    }
}

class SetMockHistoryModeUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<Boolean, Boolean>() {
    override fun buildUseCase(params: Boolean): Flow<Boolean> {
        return appPreferencesRepository.setUseMockHistory(params)
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

class GetCompletedLearningSectionsUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractorNoParams<Set<String>>() {
    override fun buildUseCase(): Flow<Set<String>> {
        return appPreferencesRepository.getCompletedLearningSections()
    }
}

data class MarkLearningSectionCompletedParams(
    val courseId: String,
    val sectionId: String
)

class MarkLearningSectionCompletedUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : FlowInteractor<MarkLearningSectionCompletedParams, Set<String>>() {
    override fun buildUseCase(params: MarkLearningSectionCompletedParams): Flow<Set<String>> {
        return appPreferencesRepository.markLearningSectionCompleted(
            courseId = params.courseId,
            sectionId = params.sectionId
        )
    }
}
