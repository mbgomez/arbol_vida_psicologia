package com.netah.hakkam.numyah.mind.di

import android.content.Context
import com.netah.hakkam.numyah.mind.data.datasource.remote.InternetConnectionChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providePostService() = APIFactory.POST_SERVICE

    @Singleton
    @Provides
    fun provideInternetChecker(context: Context) = InternetConnectionChecker(context = context)

}
