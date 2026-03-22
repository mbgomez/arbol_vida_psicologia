package com.netah.hakkam.numyah.mind.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import com.netah.hakkam.numyah.mind.data.repository.LocalAppPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            migrations = listOf(
                SharedPreferencesMigration(context, SHARED_PREFERENCES_NAME)
            ),
            produceFile = { context.preferencesDataStoreFile(DATASTORE_FILE_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideAppPreferencesRepository(
        dataStore: DataStore<Preferences>
    ): AppPreferencesRepository = LocalAppPreferencesRepository(dataStore)

    private const val SHARED_PREFERENCES_NAME = "arbol_vida_preferences"
    private const val DATASTORE_FILE_NAME = "arbol_vida_preferences.preferences_pb"
}
