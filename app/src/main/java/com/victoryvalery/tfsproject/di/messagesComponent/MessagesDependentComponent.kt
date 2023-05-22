package com.victoryvalery.tfsproject.di.messagesComponent

import com.victoryvalery.tfsproject.di.AppComponent
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesActor
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesFragment
import com.victoryvalery.tfsproject.presentation.screenMessages.StoreFactory
import dagger.Component
import io.noties.markwon.Markwon

@MessagesScope
@Component(
    dependencies = [AppComponent::class],
    modules = [MessagesModule::class]
)
interface MessagesDependentComponent {

    fun inject(fragment: MessagesFragment)
    fun getMessagesActor(): MessagesActor
    fun getStoreFactory(): StoreFactory
    fun getMarkwon(): Markwon

}
