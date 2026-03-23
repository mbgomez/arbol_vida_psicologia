package com.netah.hakkam.numyah.mind.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalAppPreferencesRepositoryTests {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: LocalAppPreferencesRepository

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        val file = File.createTempFile("preferences-test", ".preferences_pb").apply {
            deleteOnExit()
        }
        dataStore = PreferenceDataStoreFactory.create(
            scope = coroutinesRule.testScope,
            produceFile = { file }
        )
        repository = LocalAppPreferencesRepository(dataStore)
    }

    @Test
    fun setOnboardingCompleted_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setOnboardingCompleted(true).first()

        assertEquals(true, result)
        assertEquals(true, repository.hasCompletedOnboarding().first())
    }

    @Test
    fun hasCompletedOnboarding_defaultsToFalse() = coroutinesRule.runBlockingTest {
        val result = repository.hasCompletedOnboarding().first()

        assertEquals(false, result)
    }

    @Test
    fun setAssessmentHonestyNoticeVisible_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setAssessmentHonestyNoticeVisible(false).first()

        assertEquals(false, result)
        assertEquals(false, repository.shouldShowAssessmentHonestyNotice().first())
    }

    @Test
    fun shouldShowAssessmentHonestyNotice_defaultsToTrue() = coroutinesRule.runBlockingTest {
        val result = repository.shouldShowAssessmentHonestyNotice().first()

        assertEquals(true, result)
    }

    @Test
    fun setUseMockHistory_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setUseMockHistory(true).first()

        assertEquals(true, result)
        assertEquals(true, repository.shouldUseMockHistory().first())
    }

    @Test
    fun shouldUseMockHistory_defaultsToFalse() = coroutinesRule.runBlockingTest {
        val result = repository.shouldUseMockHistory().first()

        assertEquals(false, result)
    }

    @Test
    fun setLanguageMode_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setLanguageMode(AppLanguageMode.SPANISH).first()

        assertEquals(AppLanguageMode.SPANISH, result)
        assertEquals(AppLanguageMode.SPANISH, repository.getLanguageMode().first())
    }

    @Test
    fun getLanguageMode_defaultsToSystem() = coroutinesRule.runBlockingTest {
        val result = repository.getLanguageMode().first()

        assertEquals(AppLanguageMode.SYSTEM, result)
    }

    @Test
    fun setThemeMode_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setThemeMode(AppThemeMode.DARK).first()

        assertEquals(AppThemeMode.DARK, result)
        assertEquals(AppThemeMode.DARK, repository.getThemeMode().first())
    }

    @Test
    fun getThemeMode_defaultsToSystem() = coroutinesRule.runBlockingTest {
        val result = repository.getThemeMode().first()

        assertEquals(AppThemeMode.SYSTEM, result)
    }

    @Test
    fun markLearningSectionCompleted_savesSectionKey() = coroutinesRule.runBlockingTest {
        val result = repository.markLearningSectionCompleted("tree-course", "intro").first()

        assertEquals(setOf("tree-course::intro"), result)
        assertEquals(setOf("tree-course::intro"), repository.getCompletedLearningSections().first())
    }
}
