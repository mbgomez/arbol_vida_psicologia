package com.netah.hakkam.numyah.mind.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppLanguageManagerTests {

    private lateinit var appPreferencesRepository: AppPreferencesRepository
    private lateinit var appLanguageManager: AppLanguageManager

    @Before
    fun setup() {
        mockkStatic(AppCompatDelegate::class)
        every { AppCompatDelegate.setApplicationLocales(any()) } returns Unit

        appPreferencesRepository = mockk(relaxed = true)
        appLanguageManager = AppLanguageManager(appPreferencesRepository)
    }

    @After
    fun tearDown() {
        unmockkStatic(AppCompatDelegate::class)
    }

    @Test
    fun applyLanguageMode_withSpanish_setsSpanishLocales() {
        appLanguageManager.applyLanguageMode(AppLanguageMode.SPANISH)

        verify(exactly = 1) {
            AppCompatDelegate.setApplicationLocales(
                match<LocaleListCompat> { it.toLanguageTags() == "es" }
            )
        }
    }

    @Test
    fun applyLanguageMode_withSystem_clearsApplicationLocales() {
        appLanguageManager.applyLanguageMode(AppLanguageMode.SYSTEM)

        verify(exactly = 1) {
            AppCompatDelegate.setApplicationLocales(
                match<LocaleListCompat> { it.toLanguageTags().isEmpty() }
            )
        }
    }

    @Test
    fun applyStoredLanguageMode_readsRepositoryAndAppliesStoredLocale() {
        every { appPreferencesRepository.getLanguageMode() } returns flowOf(AppLanguageMode.ENGLISH)

        appLanguageManager.applyStoredLanguageMode()

        verify(exactly = 1) { appPreferencesRepository.getLanguageMode() }
        verify(exactly = 1) {
            AppCompatDelegate.setApplicationLocales(
                match<LocaleListCompat> { it.toLanguageTags() == "en" }
            )
        }
    }
}
