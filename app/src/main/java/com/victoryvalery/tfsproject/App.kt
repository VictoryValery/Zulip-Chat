package com.victoryvalery.tfsproject

import android.app.Application
import com.victoryvalery.tfsproject.di.AppComponent
import com.victoryvalery.tfsproject.di.DaggerAppComponent

class App : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: App
    }

}
