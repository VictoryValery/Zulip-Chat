package com.victoryvalery.tfsproject.di

import android.content.Context
import com.github.terrakok.cicerone.Router
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import com.victoryvalery.tfsproject.domain.services.NetworkConnectivityObserver
import com.victoryvalery.tfsproject.presentation.screenMainActivity.MainActivity
import com.victoryvalery.tfsproject.presentation.screenProfile.ProfileFragment
import dagger.BindsInstance
import dagger.Component
import okhttp3.Interceptor
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationModule::class,
        RetrofitModule::class,
        DomainModule::class,
        RoomModule::class
    ]
)
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(fragment: ProfileFragment)

    fun getRouter(): Router

    fun getZulipRepository(): ZulipRepository

    fun getNetworkConnectivityObserver(): NetworkConnectivityObserver

    fun getApiUrlProvider(): ApiUrlProvider

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AppComponent
    }

    fun getContext(): Context

    fun getZulipInterceptor(): Interceptor

}
