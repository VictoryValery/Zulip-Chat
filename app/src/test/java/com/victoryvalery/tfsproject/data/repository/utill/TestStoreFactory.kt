package com.victoryvalery.tfsproject.data.repository.utill

import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesActor
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesReducer
import com.victoryvalery.tfsproject.presentation.screenMessages.State
import vivid.money.elmslie.coroutines.ElmStoreCompat

class TestStoreFactory(
    private val actor: MessagesActor,
    private val initialState: State
) {
    private val store by lazy {
        ElmStoreCompat(
            initialState = initialState,
            reducer = MessagesReducer,
            actor = actor
        )
    }

    fun provide() = store
}
