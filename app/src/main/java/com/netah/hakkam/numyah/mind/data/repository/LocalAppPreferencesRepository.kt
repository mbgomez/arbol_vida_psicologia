package com.netah.hakkam.numyah.mind.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AppPreferencesRepository {
    fun hasCompletedOnboarding(): Flow<Boolean>
    fun setOnboardingCompleted(completed: Boolean): Flow<Boolean>
    fun shouldShowAssessmentHonestyNotice(): Flow<Boolean>
    fun setAssessmentHonestyNoticeVisible(visible: Boolean): Flow<Boolean>
}

class LocalAppPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AppPreferencesRepository {

    override fun hasCompletedOnboarding(): Flow<Boolean> = flow {
        emit(sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false))
    }

    override fun setOnboardingCompleted(completed: Boolean): Flow<Boolean> = flow {
        sharedPreferences.edit { putBoolean(KEY_ONBOARDING_COMPLETED, completed) }
        emit(completed)
    }

    override fun shouldShowAssessmentHonestyNotice(): Flow<Boolean> = flow {
        emit(sharedPreferences.getBoolean(KEY_SHOW_ASSESSMENT_HONESTY_NOTICE, true))
    }

    override fun setAssessmentHonestyNoticeVisible(visible: Boolean): Flow<Boolean> = flow {
        sharedPreferences.edit { putBoolean(KEY_SHOW_ASSESSMENT_HONESTY_NOTICE, visible) }
        emit(visible)
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_SHOW_ASSESSMENT_HONESTY_NOTICE = "show_assessment_honesty_notice"
    }
}
