package com.netah.hakkam.numyah.mind.data.repository

import android.content.SharedPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import androidx.core.content.edit

class LocalOnboardingRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : OnboardingRepository {

    override fun hasCompletedOnboarding(): Flow<Boolean> = flow {
        emit(sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false))
    }

    override fun setOnboardingCompleted(completed: Boolean): Flow<Boolean> = flow {
        sharedPreferences.edit { putBoolean(KEY_ONBOARDING_COMPLETED, completed) }
        emit(completed)
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
