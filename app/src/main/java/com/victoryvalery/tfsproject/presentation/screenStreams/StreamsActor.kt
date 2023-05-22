package com.victoryvalery.tfsproject.presentation.screenStreams

import com.victoryvalery.tfsproject.data.apiStorage.API_DELAY
import com.victoryvalery.tfsproject.domain.usecases.*
import com.victoryvalery.tfsproject.presentation.delegates.stream.toDelegateItemListWithTopics
import com.victoryvalery.tfsproject.presentation.screenStreams.Event.Internal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import vivid.money.elmslie.coroutines.Actor
import javax.inject.Inject

class StreamsActor @Inject constructor(
    private val getSubscribedStreamsUseCase: GetSubscribedStreamsUseCase,
    private val getAllStreamsUseCase: GetAllStreamsUseCase,
    private val getCountedTopicsUseCase: GetCountedTopicsUseCase,
    private val postMePresenceUseCase: PostMePresenceUseCase,
    private val addStreamUseCase: AddStreamUseCase,
) : Actor<Command, Event> {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.GetStreams -> {
                getStreams(command.activeTabIndex)
            }
            is Command.LoadStreamTopics -> {
                loadStreamTopics(command.streamId, command.streamName)
            }
            is Command.SearchStreams -> {
                { command.searchHint }
                    .asFlow()
                    .filter { it.isNotEmpty() }
                    .debounce(API_DELAY)
                    .flatMapLatest {
                        flow {
                            emit(Internal.LastSearchHint(it))
                        }
                    }
            }
            Command.SetMePresence -> {
                flow { postMePresenceUseCase() }
            }
            is Command.AddStream -> {
                addStream(command.name, command.description)
            }
            Command.UpdateSubscribedStreams -> {
                updateSubscribedStreams()
            }
        }
    }

    private fun updateSubscribedStreams(): Flow<Event> {
        return flow {
            try {
                getSubscribedStreamsUseCase().collect { streamsNew ->
                    emit(Internal.SubscribedStreamsLoaded(streamsNew))
                }
            } catch (throwable: Throwable) {
                emit(Internal.ErrorLoading(throwable.message.toString()))
            }
        }
    }

    private fun addStream(name: String, description: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                addStreamUseCase(name, description)
            }.fold(
                onSuccess = { emit(Internal.StreamAdded) },
                onFailure = { emit(Internal.ErrorLoading(it.message.toString())) }
            )
        }
    }

    private fun getStreams(activeTabIndex: Int): Flow<Event> {
        return flow {
            try {
                getAllStreamsUseCase().zip(getSubscribedStreamsUseCase()) { allStreams, subscribedStreams ->
                    StreamsInfo(
                        allStreams = allStreams,
                        allStreamsDelegate = allStreams.toDelegateItemListWithTopics(),
                        subscribedStreams = subscribedStreams,
                        subscribedStreamsDelegate = subscribedStreams.toDelegateItemListWithTopics(),
                        activeTabIndex = activeTabIndex
                    )
                }.collect { streamsInfoNew ->
                    emit(Internal.ValueLoaded(streamsInfoNew))
                }
            } catch (throwable: Throwable) {
                emit(Internal.ErrorLoading(throwable.message.toString()))
            }
        }
    }

    private fun loadStreamTopics(streamId: Int, streamName: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                val topicsFlow = getCountedTopicsUseCase(streamName, streamId)
                topicsFlow.collect { topics ->
                    if (topics.first == API_SOURCE || topics.second.isNotEmpty()) {
                        emit(Internal.TopicsLoaded(topics.second))
                    }
                }
            }.getOrElse {
                emit(Internal.ErrorLoading(it.message.toString()))
            }
        }
    }

    companion object {
        const val tabSubscribedIndex = 0
        private const val API_SOURCE = "api"
    }
}
