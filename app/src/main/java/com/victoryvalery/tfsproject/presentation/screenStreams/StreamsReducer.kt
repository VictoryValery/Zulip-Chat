package com.victoryvalery.tfsproject.presentation.screenStreams

import com.victoryvalery.tfsproject.domain.models.StreamItem
import com.victoryvalery.tfsproject.domain.models.TopicItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.stream.StreamDelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.stream.toDelegateItemListWithTopics
import com.victoryvalery.tfsproject.presentation.screenStreams.Event.Internal
import com.victoryvalery.tfsproject.presentation.screenStreams.Event.Ui
import com.victoryvalery.tfsproject.presentation.screenStreams.StreamsFragment.Companion.TAB_SUBSCRIBED_INDEX
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

internal object StreamsReducer : ScreenDslReducer<Event, Ui, Internal, State, Effect, Command>(
    Ui::class, Internal::class
) {
    override fun Result.ui(event: Ui) = when (event) {
        is Ui.GetStreams -> {
            state { copy(isLoading = true, isInitializing = false, searchResult = null, searchText = null) }
            commands { +Command.GetStreams(activeTabIndex = state.streamsInfo.activeTabIndex) }
        }
        is Ui.GetStreamTopics -> {
            state {
                copy(isLoading = true, isInitializing = false, searchResult = null, searchText = null, isSearching = false)
            }
            val streamsList = state.streamsInfo.activeStreamList()
            val myStream = streamsList.find { it.streamId == event.streamId }
            if (myStream != null) {
                val actualStream = myStream.copy(isClicked = !myStream.isClicked)
                val updatedList = streamsList.map { if (it.streamId == event.streamId) actualStream else it }
                state {
                    copy(streamsInfo = state.streamsInfo.updateStreamList(updatedList))
                }
                if (actualStream.isClicked)
                    commands { +Command.LoadStreamTopics(event.streamId, actualStream.name) }
            }
            state { copy(isLoading = false) }
        }
        is Ui.SetActiveTab -> {
            val streamsInfo = state.streamsInfo.copy(activeTabIndex = event.activeTabIndex)
            state {
                copy(
                    isLoading = false, streamsInfo = streamsInfo, isInitializing = false,
                    searchResult = null, searchText = null
                )
            }
        }
        is Ui.TopicClick -> {
            state { copy(isLoading = false, isInitializing = false, searchResult = null, searchText = null) }
            effects {
                +Effect.NavigateToMessages(
                    event.streamName,
                    event.streamId,
                    state.streamsInfo.subscribedStreams.any { it.name == event.streamName },
                    event.topicName
                )
            }
        }
        Ui.Initial -> {
            state { copy(isLoading = false, isInitializing = true, searchResult = null, searchText = null) }
            commands {
                +Command.SetMePresence
                +Command.GetStreams(activeTabIndex = state.streamsInfo.activeTabIndex)
            }
        }
        is Ui.Search -> {
            state {
                copy(
                    isLoading = true, isInitializing = false, searchResult = null,
                    searchText = if (event.searchText == "") null else event.searchText
                )
            }
            if (state.searchText != null)
                commands { +Command.SearchStreams(event.searchText) }
            else
                state { copy(isLoading = false, isInitializing = false, searchResult = null) }
        }
        is Ui.SearchClick -> {
            state { copy(isSearching = event.isSearching) }
        }
        is Ui.AddStream -> {
            commands { +Command.AddStream(event.name, event.description) }
        }
        is Ui.OpenGeneralChat -> {
            state { copy(isLoading = false, isInitializing = false, searchResult = null, searchText = null) }
            effects {
                +Effect.NavigateToGeneralMessages(
                    event.streamName,
                    event.streamId,
                    state.streamsInfo.subscribedStreams.any { it.name == event.streamName }
                )
            }
        }
        Ui.UpdateStreamsTopics -> {
            when (state.streamsInfo.activeTabIndex) {
                StreamsActor.tabSubscribedIndex -> state.streamsInfo.subscribedStreamsDelegate
                else -> state.streamsInfo.allStreamsDelegate
            }.filterIsInstance<StreamDelegateItem>()
                .map { it.content() as StreamItem }
                .filter { it.isClicked }
                .forEach { commands { +Command.LoadStreamTopics(it.streamId, it.name) } }
        }
        Ui.UpdateSubscribedStreams -> {
            commands { +Command.UpdateSubscribedStreams }
        }
    }

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.ErrorLoading -> {
            state {
                copy(
                    isError = true, isInitializing = false, isLoading = false,
                    searchResult = null, searchText = null
                )
            }
            effects { +Effect.ShowError(event.errorMessage) }
        }
        is Internal.ValueLoaded -> {
            state {
                copy(
                    isLoading = false, streamsInfo = event.streamsInfo,
                    searchResult = null, searchText = null
                )
            }
        }
        is Internal.LastSearchHint -> {
            val searchResult = search(event.query, state.streamsInfo)
            state { copy(isLoading = false, isInitializing = false, searchResult = searchResult) }
        }
        is Internal.StreamAdded -> {
            commands { +Command.GetStreams(activeTabIndex = state.streamsInfo.activeTabIndex) }
        }
        is Internal.SubscribedStreamsLoaded -> {
            val newSubscriptions = updateStreamsWithOldTopics(event.subscribedStreams, state.streamsInfo.subscribedStreams)
            state {
                copy(
                    streamsInfo = state.streamsInfo.copy(
                        subscribedStreams = newSubscriptions,
                        subscribedStreamsDelegate = newSubscriptions.toDelegateItemListWithTopics()
                    )
                )
            }
        }
        is Internal.TopicsLoaded -> {
            if (event.topics.isNotEmpty()) {
                state { updateTopics(state, event.topics.first().parentStreamId, event.topics) }
            } else
                state {
                    copy(
                        isLoading = false, isInitializing = false, searchResult = null,
                        searchText = null, isSearching = false
                    )
                }
        }
    }

    private fun updateStreamsWithOldTopics(newStreams: List<StreamItem>, oldStreams: List<StreamItem>): List<StreamItem> {
        return newStreams.map { newStream ->
            val oldStream = oldStreams.find { it.streamId == newStream.streamId && it.topics.isNotEmpty() }
            oldStream?.let {
                newStream.copy(isClicked = it.isClicked, topics = it.topics)
            } ?: newStream
        }
    }

    private fun updateTopics(state: State, streamId: Int, topics: List<TopicItem>): State {
        val streamsList = state.streamsInfo.activeStreamList()
        val actualStream = streamsList.find { it.streamId == streamId }?.copy(topics = topics) ?: return state
        return state.copy(streamsInfo = state.streamsInfo.updateStreamList(streamsList.map { if (it.streamId == streamId) actualStream else it }))
    }

    private fun StreamsInfo.activeStreamList() = when (activeTabIndex) {
        TAB_SUBSCRIBED_INDEX -> subscribedStreamsDelegate
        else -> allStreamsDelegate
    }.filterIsInstance<StreamDelegateItem>()
        .map { it.content() as StreamItem }
        .toMutableList()

    private fun StreamsInfo.updateStreamList(updatedList: List<StreamItem>) = when (activeTabIndex) {
        StreamsActor.tabSubscribedIndex -> copy(
            subscribedStreams = updatedList,
            subscribedStreamsDelegate = updatedList.toDelegateItemListWithTopics()
        )
        else -> copy(
            allStreams = updatedList,
            allStreamsDelegate = updatedList.toDelegateItemListWithTopics()
        )
    }

    private fun search(query: String, streamsInfo: StreamsInfo): List<DelegateItem> {
        val currentList = if (streamsInfo.activeTabIndex == TAB_SUBSCRIBED_INDEX)
            streamsInfo.subscribedStreams
        else
            streamsInfo.allStreams

        val filteredList = currentList.filter {
            it.name.contains(query, ignoreCase = true)
        }

        return filteredList.toDelegateItemListWithTopics()
    }
}
