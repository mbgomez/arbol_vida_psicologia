package com.netah.hakkam.numyah.mind.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.distinctUntilChanged

interface AppPreferencesRepository {
    fun hasCompletedOnboarding(): Flow<Boolean>
    fun setOnboardingCompleted(completed: Boolean): Flow<Boolean>
    fun shouldShowAssessmentHonestyNotice(): Flow<Boolean>
    fun setAssessmentHonestyNoticeVisible(visible: Boolean): Flow<Boolean>
    fun getLanguageMode(): Flow<AppLanguageMode>
    fun setLanguageMode(languageMode: AppLanguageMode): Flow<AppLanguageMode>
    fun getThemeMode(): Flow<AppThemeMode>
    fun setThemeMode(themeMode: AppThemeMode): Flow<AppThemeMode>
}

class LocalAppPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AppPreferencesRepository {

    override fun hasCompletedOnboarding(): Flow<Boolean> =
        observeBooleanPreference(KEY_ONBOARDING_COMPLETED, false)

    override fun setOnboardingCompleted(completed: Boolean): Flow<Boolean> = flow {
        sharedPreferences.edit { putBoolean(KEY_ONBOARDING_COMPLETED, completed) }
        emit(completed)
    }

    override fun shouldShowAssessmentHonestyNotice(): Flow<Boolean> =
        observeBooleanPreference(KEY_SHOW_ASSESSMENT_HONESTY_NOTICE, true)

    override fun setAssessmentHonestyNoticeVisible(visible: Boolean): Flow<Boolean> = flow {
        sharedPreferences.edit { putBoolean(KEY_SHOW_ASSESSMENT_HONESTY_NOTICE, visible) }
        emit(visible)
    }

    override fun getLanguageMode(): Flow<AppLanguageMode> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == KEY_LANGUAGE_MODE) {
                trySend(readLanguageMode())
            }
        }
        trySend(readLanguageMode())
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()

    override fun setLanguageMode(languageMode: AppLanguageMode): Flow<AppLanguageMode> = flow {
        sharedPreferences.edit { putString(KEY_LANGUAGE_MODE, languageMode.name) }
        emit(languageMode)
    }

    override fun getThemeMode(): Flow<AppThemeMode> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == KEY_THEME_MODE) {
                trySend(readThemeMode())
            }
        }
        trySend(readThemeMode())
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()

    override fun setThemeMode(themeMode: AppThemeMode): Flow<AppThemeMode> = flow {
        sharedPreferences.edit { putString(KEY_THEME_MODE, themeMode.name) }
        emit(themeMode)
    }

    private fun observeBooleanPreference(key: String, defaultValue: Boolean): Flow<Boolean> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
                if (changedKey == key) {
                    trySend(sharedPreferences.getBoolean(key, defaultValue))
                }
            }
            trySend(sharedPreferences.getBoolean(key, defaultValue))
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }.distinctUntilChanged()

    private fun readThemeMode(): AppThemeMode {
        val storedValue = sharedPreferences.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        return storedValue?.let {
            runCatching { AppThemeMode.valueOf(it) }.getOrDefault(AppThemeMode.SYSTEM)
        } ?: AppThemeMode.SYSTEM
    }

    private fun readLanguageMode(): AppLanguageMode {
        val storedValue = sharedPreferences.getString(KEY_LANGUAGE_MODE, AppLanguageMode.SYSTEM.name)
        return AppLanguageMode.fromStoredValue(storedValue)
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_SHOW_ASSESSMENT_HONESTY_NOTICE = "show_assessment_honesty_notice"
        const val KEY_LANGUAGE_MODE = "language_mode"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
