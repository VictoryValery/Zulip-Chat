package com.victoryvalery.tfsproject.di.usersComponent

import com.victoryvalery.tfsproject.di.AppComponent
import com.victoryvalery.tfsproject.presentation.screenUsers.StoreFactory
import com.victoryvalery.tfsproject.presentation.screenUsers.UsersActor
import com.victoryvalery.tfsproject.presentation.screenUsers.UsersFragment
import dagger.Component

@UsersScope
@Component(
    dependencies = [AppComponent::class],
    modules = [UsersModule::class]
)
interface UsersDependentComponent {

    fun inject(fragment: UsersFragment)
    fun getUsersActor(): UsersActor
    fun getStoreFactory(): StoreFactory

}
