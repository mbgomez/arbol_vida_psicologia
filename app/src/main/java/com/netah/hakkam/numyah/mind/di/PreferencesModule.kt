package com.netah.hakkam.numyah.mind.di

import android.content.Context
import android.content.SharedPreferences
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
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("arbol_vida_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAppPreferencesRepository(
        sharedPreferences: SharedPreferences
    ): AppPreferencesRepository = LocalAppPreferencesRepository(sharedPreferences)
}
