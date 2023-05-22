package com.victoryvalery.tfsproject.di.profileComponent

import com.victoryvalery.tfsproject.di.AppComponent
import com.victoryvalery.tfsproject.presentation.screenProfile.ProfileActor
import com.victoryvalery.tfsproject.presentation.screenProfile.ProfileFragment
import com.victoryvalery.tfsproject.presentation.screenProfile.StoreFactory
import dagger.Component

@ProfileScope
@Component(
    dependencies = [AppComponent::class],
    modules = [ProfileModule::class]
)
interface ProfileDependentComponent {

    fun inject(fragment: ProfileFragment)
    fun getProfileActor(): ProfileActor
    fun getStoreFactory(): StoreFactory

}
