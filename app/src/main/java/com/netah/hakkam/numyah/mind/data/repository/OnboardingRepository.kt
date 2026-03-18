package com.netah.hakkam.numyah.mind.data.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun hasCompletedOnboarding(): Flow<Boolean>
    fun setOnboardingCompleted(completed: Boolean): Flow<Boolean>
}
