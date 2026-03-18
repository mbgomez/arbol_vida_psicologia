package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingStatusUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : FlowInteractorNoParams<Boolean>() {
    override fun buildUseCase(): Flow<Boolean> {
        return onboardingRepository.hasCompletedOnboarding()
    }
}

class SetOnboardingCompletedUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : FlowInteractor<Boolean, Boolean>() {
    override fun buildUseCase(params: Boolean): Flow<Boolean> {
        return onboardingRepository.setOnboardingCompleted(params)
    }
}
