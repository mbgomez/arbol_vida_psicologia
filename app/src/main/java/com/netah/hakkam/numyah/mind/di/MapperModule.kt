package com.netah.hakkam.numyah.mind.di

import com.netah.hakkam.numyah.mind.mapper.PostMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MapperModule {

    @Singleton
    @Provides
    fun providePostMapper() = PostMapper()

}
