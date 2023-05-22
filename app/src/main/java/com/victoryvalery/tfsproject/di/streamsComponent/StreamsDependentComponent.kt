package com.victoryvalery.tfsproject.di.streamsComponent

import com.victoryvalery.tfsproject.di.AppComponent
import com.victoryvalery.tfsproject.presentation.screenStreams.StoreFactory
import com.victoryvalery.tfsproject.presentation.screenStreams.StreamsActor
import com.victoryvalery.tfsproject.presentation.screenStreams.StreamsFragment
import dagger.Component

@StreamsScope
@Component(
    dependencies = [AppComponent::class],
    modules = [StreamsModule::class]
)
interface StreamsDependentComponent {

    fun inject(fragment: StreamsFragment)
    fun getStreamsActor(): StreamsActor
    fun getStoreFactory(): StoreFactory

}
