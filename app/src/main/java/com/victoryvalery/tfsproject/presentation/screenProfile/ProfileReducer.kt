package com.victoryvalery.tfsproject.presentation.screenProfile

import com.victoryvalery.tfsproject.presentation.screenProfile.Event.Internal
import com.victoryvalery.tfsproject.presentation.screenProfile.Event.Ui
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer


internal object ProfileReducer : ScreenDslReducer<Event, Ui, Internal, State, Effect, Command>(
    Ui::class, Internal::class
) {

    override fun Result.ui(event: Ui) = when (event) {
        Ui.LoadUser -> {
            state { copy(isLoading = true) }
            commands { +Command.LoadMeUser }
        }
        is Ui.SetUser -> {
            state { copy(isLoading = true) }
            commands { +Command.LoadOtherUser(event.user) }
        }
        Ui.Initial -> Unit
    }

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.ErrorLoading -> {
            state { copy(isError = true, isLoading = false) }
            effects { +Effect.ShowError(event.errorMessage) }
        }
        is Internal.ValueLoaded -> {
            state { copy(isLoading = false, isError = false, userInfo = event.value) }
        }
    }
}
