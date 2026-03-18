package com.netah.hakkam.numyah.mind.di


import com.netah.hakkam.numyah.mind.data.repository.IPostRepository
import com.netah.hakkam.numyah.mind.data.repository.OnboardingRepository
import com.netah.hakkam.numyah.mind.domain.usecase.GetCachedPostsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetOnboardingStatusUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetPostsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetOnboardingCompletedUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideGetPostsUseCase(postRepository: IPostRepository) =
        GetPostsUseCase(postRepository = postRepository)

    @Singleton
    @Provides
    fun provideGetCachedPostsUseCase(postRepository: IPostRepository) =
        GetCachedPostsUseCase(postRepository = postRepository)

    @Singleton
    @Provides
    fun provideGetOnboardingStatusUseCase(onboardingRepository: OnboardingRepository) =
        GetOnboardingStatusUseCase(onboardingRepository = onboardingRepository)

    @Singleton
    @Provides
    fun provideSetOnboardingCompletedUseCase(onboardingRepository: OnboardingRepository) =
        SetOnboardingCompletedUseCase(onboardingRepository = onboardingRepository)

}
