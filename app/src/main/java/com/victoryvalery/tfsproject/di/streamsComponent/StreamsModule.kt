package com.victoryvalery.tfsproject.di.streamsComponent

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import com.victoryvalery.tfsproject.domain.usecases.GetAllStreamsUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetSubscribedStreamsUseCase
import com.victoryvalery.tfsproject.domain.usecases.PostMePresenceUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetCountedTopicsUseCase
import dagger.Module
import dagger.Provides

@Module
class StreamsModule {

    @StreamsScope
    @Provides
    fun provideGetCountedTopicsUseCase(repository: ZulipRepository): GetCountedTopicsUseCase {
        return GetCountedTopicsUseCase(repository)
    }

    @StreamsScope
    @Provides
    fun provideGetAllStreamsUseCase(repository: ZulipRepository): GetAllStreamsUseCase {
        return GetAllStreamsUseCase(repository)
    }

    @StreamsScope
    @Provides
    fun provideGetSubscribedStreamsUseCase(repository: ZulipRepository): GetSubscribedStreamsUseCase {
        return GetSubscribedStreamsUseCase(repository)
    }

    @StreamsScope
    @Provides
    fun providePostMePresenceUseCase(repository: ZulipRepository): PostMePresenceUseCase {
        return PostMePresenceUseCase(repository)
    }

}
