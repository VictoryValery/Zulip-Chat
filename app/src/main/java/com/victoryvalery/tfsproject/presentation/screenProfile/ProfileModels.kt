package com.victoryvalery.tfsproject.presentation.screenProfile

import com.victoryvalery.tfsproject.domain.models.Status
import com.victoryvalery.tfsproject.domain.models.UserItem
import vivid.money.elmslie.coroutines.ElmStoreCompat
import javax.inject.Inject

data class UserInfo(val user: UserItem, val status: Status)

data class State(
    val isLoading: Boolean = true,
    val userInfo: UserInfo? = null,
    val isError: Boolean = false
)

sealed class Event {

    sealed class Ui : Event() {
        data class SetUser(val user: UserItem) : Ui()
        object LoadUser : Ui()
        object Initial : Ui()
    }

    sealed class Internal : Event() {
        data class ValueLoaded(val value: UserInfo) : Internal()
        data class ErrorLoading(val errorMessage: String) : Internal()
    }
}

sealed class Command {
    object LoadMeUser : Command()
    class LoadOtherUser(val user: UserItem) : Command()
}

sealed class Effect {
    data class ShowError(val errorMessage: String) : Effect()
}

class StoreFactory @Inject constructor(
    private val actor: ProfileActor
) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = State(),
            reducer = ProfileReducer,
            actor = actor
        )
    }

    fun provide() = store
}
