package com.netah.hakkam.numyah.mind.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private val dataStore: DataStore<Preferences>
) : AppPreferencesRepository {

    override fun hasCompletedOnboarding(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] ?: false
        }.distinctUntilChanged()

    override fun setOnboardingCompleted(completed: Boolean): Flow<Boolean> =
        writePreference(KEY_ONBOARDING_COMPLETED, completed)

    override fun shouldShowAssessmentHonestyNotice(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_SHOW_ASSESSMENT_HONESTY_NOTICE] ?: true
        }.distinctUntilChanged()

    override fun setAssessmentHonestyNoticeVisible(visible: Boolean): Flow<Boolean> =
        writePreference(KEY_SHOW_ASSESSMENT_HONESTY_NOTICE, visible)

    override fun getLanguageMode(): Flow<AppLanguageMode> =
        dataStore.data.map { preferences ->
            AppLanguageMode.fromStoredValue(
                preferences[KEY_LANGUAGE_MODE] ?: AppLanguageMode.SYSTEM.name
            )
        }.distinctUntilChanged()

    override fun setLanguageMode(languageMode: AppLanguageMode): Flow<AppLanguageMode> =
        writePreference(KEY_LANGUAGE_MODE, languageMode.name).map {
            AppLanguageMode.fromStoredValue(it)
        }

    override fun getThemeMode(): Flow<AppThemeMode> =
        dataStore.data.map { preferences ->
            val storedValue = preferences[KEY_THEME_MODE] ?: AppThemeMode.SYSTEM.name
            runCatching { AppThemeMode.valueOf(storedValue) }.getOrDefault(AppThemeMode.SYSTEM)
        }.distinctUntilChanged()

    override fun setThemeMode(themeMode: AppThemeMode): Flow<AppThemeMode> =
        writePreference(KEY_THEME_MODE, themeMode.name).map {
            runCatching { AppThemeMode.valueOf(it) }.getOrDefault(AppThemeMode.SYSTEM)
        }

    private fun writePreference(
        key: Preferences.Key<Boolean>,
        value: Boolean
    ): Flow<Boolean> = kotlinx.coroutines.flow.flow {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
        emit(value)
    }

    private fun writePreference(
        key: Preferences.Key<String>,
        value: String
    ): Flow<String> = kotlinx.coroutines.flow.flow {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
        emit(value)
    }

    private companion object {
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_SHOW_ASSESSMENT_HONESTY_NOTICE = booleanPreferencesKey("show_assessment_honesty_notice")
        val KEY_LANGUAGE_MODE = stringPreferencesKey("language_mode")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
