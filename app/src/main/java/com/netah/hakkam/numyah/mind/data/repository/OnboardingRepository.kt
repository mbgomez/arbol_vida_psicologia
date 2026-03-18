package com.netah.hakkam.numyah.mind.data.repository

interface OnboardingRepository {
    suspend fun hasCompletedOnboarding(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
}
