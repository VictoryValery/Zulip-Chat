package com.victoryvalery.tfsproject.di.profileComponent

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import com.victoryvalery.tfsproject.domain.usecases.GetMeUserUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetUserPresenceUseCase
import dagger.Module
import dagger.Provides

@Module
class ProfileModule {

    @ProfileScope
    @Provides
    fun provideGetMeUserUseCase(repository: ZulipRepository): GetMeUserUseCase {
        return GetMeUserUseCase(repository)
    }

    @ProfileScope
    @Provides
    fun provideGetUserPresenceUseCase(repository: ZulipRepository): GetUserPresenceUseCase {
        return GetUserPresenceUseCase(repository)
    }

}
