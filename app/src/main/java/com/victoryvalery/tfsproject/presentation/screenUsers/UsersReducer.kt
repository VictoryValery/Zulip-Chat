package com.victoryvalery.tfsproject.presentation.screenUsers

import com.victoryvalery.tfsproject.presentation.screenUsers.Event.Internal
import com.victoryvalery.tfsproject.presentation.screenUsers.Event.Ui
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

internal object UsersReducer : ScreenDslReducer<Event, Ui, Internal, State, Effect, Command>(
    Ui::class, Internal::class
) {

    override fun Result.ui(event: Ui) = when (event) {
        Ui.GetUsers -> {
            state { copy(isLoading = true) }
            commands {
                +Command.SetMePresence
                +Command.LoadUsers
            }
        }
        is Ui.OnUserClick -> {
            state { copy(isLoading = false) }
            effects { +Effect.NavigateToUser(event.user) }
        }
        is Ui.SearchClick -> {
            state { copy(isSearching = event.isSearching) }
        }
        is Ui.SearchTextChange -> {
            state { copy(searchText = if (event.searchText == "") null else event.searchText) }
        }
    }

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.ErrorLoading -> {
            state { copy(isLoading = false) }
            effects { +Effect.ShowError(event.errorMessage) }
        }
        is Internal.ValueLoaded -> {
            state { copy(isLoading = false, usersInfo = event.value) }
        }
    }
}
