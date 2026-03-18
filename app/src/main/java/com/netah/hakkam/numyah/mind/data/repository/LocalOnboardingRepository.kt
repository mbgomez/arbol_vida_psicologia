package com.netah.hakkam.numyah.mind.data.repository

import android.content.SharedPreferences
import javax.inject.Inject

class LocalOnboardingRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : OnboardingRepository {

    override suspend fun hasCompletedOnboarding(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
