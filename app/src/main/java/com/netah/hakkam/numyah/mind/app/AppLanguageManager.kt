package com.netah.hakkam.numyah.mind.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

@Singleton
class AppLanguageManager @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) {

    fun applyLanguageMode(languageMode: AppLanguageMode) {
        val locales = languageMode.languageTag?.let(LocaleListCompat::forLanguageTags)
            ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun applyStoredLanguageMode() {
        val languageMode = runBlocking {
            appPreferencesRepository.getLanguageMode().first()
        }
        applyLanguageMode(languageMode)
    }
}
