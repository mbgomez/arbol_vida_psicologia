package com.netah.hakkam.numyah.mind.di

import android.content.Context
import com.netah.hakkam.numyah.mind.data.datasource.remote.InternetConnectionChecker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

}
