package com.netah.hakkam.numyah.mind.di

import android.content.Context
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.observability.DefaultAppTelemetry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ObservabilityModule {

    @Provides
    @Singleton
    fun provideAppTelemetry(
        @ApplicationContext context: Context
    ): AppTelemetry = DefaultAppTelemetry(context)
}
