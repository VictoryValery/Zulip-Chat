package com.victoryvalery.tfsproject.di.usersComponent

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import com.victoryvalery.tfsproject.domain.usecases.GetAllUsersPresenceUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetAllUsersUseCase
import com.victoryvalery.tfsproject.domain.usecases.PostMePresenceUseCase
import dagger.Module
import dagger.Provides

@Module
class UsersModule {

    @UsersScope
    @Provides
    fun provideGetAllUsersUseCase(repository: ZulipRepository): GetAllUsersUseCase {
        return GetAllUsersUseCase(repository)
    }

    @UsersScope
    @Provides
    fun provideGetAllUsersPresenceUseCase(repository: ZulipRepository): GetAllUsersPresenceUseCase {
        return GetAllUsersPresenceUseCase(repository)
    }

    @UsersScope
    @Provides
    fun providePostMePresenceUseCase(repository: ZulipRepository): PostMePresenceUseCase {
        return PostMePresenceUseCase(repository)
    }

}
