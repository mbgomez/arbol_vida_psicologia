package com.netah.hakkam.numyah.mind.di

import android.content.Context
import com.netah.hakkam.numyah.mind.data.datasource.DatabaseFactory
import com.netah.hakkam.numyah.mind.data.datasource.FoundationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = DatabaseFactory.getDBInstance(context = context)

    @Singleton
    @Provides
    fun providePostDao(db: FoundationDatabase) = db.getPostDao()

}
