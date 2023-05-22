package com.victoryvalery.tfsproject.presentation.screenUsers

import com.victoryvalery.tfsproject.domain.models.UserItem
import vivid.money.elmslie.coroutines.ElmStoreCompat
import javax.inject.Inject

data class State(
    val isLoading: Boolean = false,
    val usersInfo: List<UserItem> = emptyList(),
    val isError: Boolean = false,
    val isSearching: Boolean = false,
    val searchText: String? = null
)

sealed class Event {

    sealed class Ui : Event() {
        data class OnUserClick(val user: UserItem) : Ui()
        object GetUsers : Ui()
        data class SearchClick(val isSearching: Boolean) : Ui()
        data class SearchTextChange(val searchText: String?) : Ui()
    }

    sealed class Internal : Event() {
        data class ValueLoaded(val value: List<UserItem>) : Internal()
        data class ErrorLoading(val errorMessage: String) : Internal()
    }
}

sealed class Command {
    object LoadUsers : Command()
    object SetMePresence : Command()
}

sealed class Effect {
    data class ShowError(val errorMessage: String) : Effect()
    data class NavigateToUser(val user: UserItem) : Effect()
}

class StoreFactory @Inject constructor(
    private val actor: UsersActor
) {
    private val store by lazy {
        ElmStoreCompat(
            initialState = State(),
            reducer = UsersReducer,
            actor = actor
        )
    }

    fun provide() = store
}
