package com.victoryvalery.tfsproject.di

import com.victoryvalery.tfsproject.data.repository.ZulipRepositoryImpl
import com.victoryvalery.tfsproject.data.services.NetworkConnectivityObserverImpl
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import com.victoryvalery.tfsproject.domain.services.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DomainModule {

    @Singleton
    @Binds
    fun bindZulipRepository(impl: ZulipRepositoryImpl): ZulipRepository

    @Singleton
    @Binds
    fun provideNetworkConnectivityObserver(impl: NetworkConnectivityObserverImpl): NetworkConnectivityObserver

    @Singleton
    @Binds
    fun bindApiUrlProvider(impl: ApiUrlProvider.ApiUrlProviderImpl): ApiUrlProvider


}
