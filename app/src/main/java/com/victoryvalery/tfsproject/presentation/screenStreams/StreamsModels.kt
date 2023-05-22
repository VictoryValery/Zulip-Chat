package com.victoryvalery.tfsproject.presentation.screenStreams

import com.victoryvalery.tfsproject.domain.models.StreamItem
import com.victoryvalery.tfsproject.domain.models.TopicItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem
import vivid.money.elmslie.coroutines.ElmStoreCompat
import javax.inject.Inject

data class StreamsInfo(
    val allStreamsDelegate: List<DelegateItem> = emptyList(),
    val subscribedStreamsDelegate: List<DelegateItem> = emptyList(),
    val allStreams: List<StreamItem> = emptyList(),
    val subscribedStreams: List<StreamItem> = emptyList(),
    var activeTabIndex: Int = 0,
)

data class State(
    val isInitializing: Boolean = false,
    val isLoading: Boolean = false,
    val streamsInfo: StreamsInfo = StreamsInfo(),
    val isError: Boolean = false,
    val isSearching: Boolean = false,
    val searchText: String? = null,
    val searchResult: List<DelegateItem>? = null
)

sealed class Event {

    sealed class Ui : Event() {
        object Initial : Ui()
        object GetStreams: Ui()
        object UpdateStreamsTopics: Ui()
        object UpdateSubscribedStreams: Ui()
        data class GetStreamTopics(val streamId: Int) : Ui()
        data class SetActiveTab(val activeTabIndex: Int) : Ui()
        data class TopicClick(val streamName: String, val streamId: Int, val topicName: String) : Ui()
        data class SearchClick(val isSearching: Boolean) : Ui()
        data class Search(val searchText: String) : Ui()
        data class AddStream(val name: String, val description: String) : Ui()
        data class OpenGeneralChat(val streamName: String, val streamId: Int): Ui()
    }

    sealed class Internal : Event() {
        data class ValueLoaded(val streamsInfo: StreamsInfo) : Internal()
        data class TopicsLoaded(val topics: List<TopicItem>) : Internal()
        data class LastSearchHint(val query: String) : Internal()
        object StreamAdded: Internal()
        data class SubscribedStreamsLoaded(val subscribedStreams: List<StreamItem>): Internal()
        data class ErrorLoading(val errorMessage: String) : Internal()
    }
}

sealed class Command {
    data class GetStreams(val activeTabIndex: Int) : Command()
    data class LoadStreamTopics(val streamId: Int, val streamName: String) : Command()
    data class SearchStreams(val searchHint: String) : Command()
    object SetMePresence : Command()
    object UpdateSubscribedStreams : Command()
    data class AddStream(val name: String, val description: String) : Command()
}

sealed class Effect {
    data class ShowError(val errorMessage: String) : Effect()
    data class NavigateToMessages(val streamName: String, val streamId: Int, val isSubscribed: Boolean, val topicName: String) : Effect()
    data class NavigateToGeneralMessages(val streamName: String, val streamId: Int, val isSubscribed: Boolean) : Effect()
}

class StoreFactory @Inject constructor(
    private val actor: StreamsActor
) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = State(),
            reducer = StreamsReducer,
            actor = actor
        )
    }

    fun provide() = store
}
