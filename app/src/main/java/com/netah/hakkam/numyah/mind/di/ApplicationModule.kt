package com.netah.hakkam.numyah.mind.di

import android.content.Context
import com.netah.hakkam.numyah.mind.NumyahMindApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
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
    fun provideLocal(application: NumyahMindApplication): Locale {
        return application.applicationContext.resources.configuration.locales[0]
    }
}
