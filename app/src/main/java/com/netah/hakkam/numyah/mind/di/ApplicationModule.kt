package com.netah.hakkam.numyah.mind.di

import android.content.Context
import com.netah.hakkam.numyah.mind.NumyahMindApplication
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): NumyahMindApplication {
        return app as NumyahMindApplication
    }

    @Provides
    @Singleton
    fun provideContext(application: NumyahMindApplication): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideCurrentLocaleProvider(
        @ApplicationContext context: Context
    ): CurrentLocaleProvider {
        return CurrentLocaleProvider(context)
    }
}
