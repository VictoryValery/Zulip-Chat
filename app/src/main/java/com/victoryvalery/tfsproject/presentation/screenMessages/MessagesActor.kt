package com.victoryvalery.tfsproject.presentation.screenMessages

import android.net.Uri
import com.victoryvalery.tfsproject.data.apiStorage.API_DELAY
import com.victoryvalery.tfsproject.data.apiStorage.ME_USER_ID_CONST
import com.victoryvalery.tfsproject.data.apiStorage.mappers.toEmojiName
import com.victoryvalery.tfsproject.domain.models.MessageEventType.*
import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.domain.usecases.AddStreamUseCase
import com.victoryvalery.tfsproject.domain.usecases.DeleteEventUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetAllStreamTopicsUseCase
import com.victoryvalery.tfsproject.domain.usecases.RegisterEventUseCase
import com.victoryvalery.tfsproject.domain.usecases.messages.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import vivid.money.elmslie.coroutines.Actor
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class MessagesActor @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val postMessageUseCase: PostMessageUseCase,
    private val addEmojiUseCase: AddEmojiUseCase,
    private val removeEmojiUseCase: RemoveEmojiUseCase,
    private val registerEventUseCase: RegisterEventUseCase,
    private val getMessagesEventUseCase: GetMessagesEventUseCase,
    private val getEmojiEventUseCase: GetEmojiEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val sendFileUseCase: SendFileUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val addStreamUseCase: AddStreamUseCase,
    private val saveCacheMessagesUseCase: SaveCacheMessagesUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val moveMessageUseCase: MoveMessageUseCase,
    private val getSingleMessageUseCase: GetSingleMessageUseCase,
    private val getAllStreamTopicsUseCase: GetAllStreamTopicsUseCase
) : Actor<Command, Event> {

    private var lastEventMessageId = INITIAL_QUERY_ID
    private var lastEventReactionId = INITIAL_QUERY_ID
    private var queueMessageId = EMPTY_STRING
    private var queueReactionId = EMPTY_STRING
    override fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.SubscribeOnEvents -> {
                if (queueMessageId != EMPTY_STRING && queueReactionId != EMPTY_STRING)
                    return emptyFlow()
                merge(
                    flow { subscribeToReaction(command.streamName, command.topicName) },
                    flow { subscribeToMessages(command.streamName, command.topicName) }
                )
            }

            is Command.LoadMessages -> {
                getMessages(command.streamName, command.topicName, command.anchor)
            }
            is Command.PostEmojiClick -> {
                onEmojiClick(command.messageId, command.emojiCode, command.messages)
            }
            is Command.PostMessage -> {
                postMessage(command.streamName, command.topicName, command.content)
            }
            is Command.SendFile -> {
                sendFile(command.filePart, command.fileName, command.topicName)
            }
            is Command.SaveCacheMessages -> {
                saveCacheMessages(command.streamName, command.topicName, command.messages)
            }
            is Command.DeleteMessage -> {
                deleteMessage(command.messageId)
            }
            is Command.EditMessage -> {
                editMessage(command.messageId, command.content)
            }
            is Command.MoveMessage -> {
                moveMessage(command.messageId, command.topic)
            }
            is Command.LoadTopics -> {
                loadTopics(command.streamId)
            }
            is Command.DownloadFile -> {
                downloadFile(command.fileUrl)
            }
            is Command.StreamSubscribe -> {
                streamSubscribe(command.streamName)
            }
        }
    }

    private fun downloadFile(fileUrl: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                downloadFileUseCase(fileUrl)
            }.fold(
                onSuccess = { response ->
                    emit(
                        Event.Internal.FileDownloaded(
                            response,
                            Uri.parse(fileUrl).lastPathSegment.toString(),
                            response.body?.contentType().toString()
                        )
                    )
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun loadTopics(streamId: Int): Flow<Event> {
        return flow {
            kotlin.runCatching {
                getAllStreamTopicsUseCase(streamId)
            }.fold(
                onSuccess = {
                    emit(Event.Internal.TopicsLoaded(it))
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun streamSubscribe(streamName: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                addStreamUseCase(streamName, EMPTY_STRING)
            }.fold(
                onSuccess = {
                    emit(Event.Internal.StreamSubscribed)
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun moveMessage(messageId: Int, topicName: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                moveMessageUseCase(messageId, topicName)
            }.fold(
                onSuccess = {
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun saveCacheMessages(streamName: String, topicName: String?, messages: List<MessageItem>): Flow<Event> {
        return flow {
            kotlin.runCatching {
                saveCacheMessagesUseCase(streamName, topicName, messages)
            }.fold(
                onSuccess = {
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun sendFile(filePart: MultipartBody.Part, fileName: String, topicName: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                val res = sendFileUseCase(filePart, fileName)
                delay(API_DELAY * 3)
                res
            }.fold(
                onSuccess = {
                    emit(Event.Internal.FileSent(it, topicName))
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private suspend fun FlowCollector<Event>.subscribeToReaction(
        streamName: String,
        topicName: String?
    ) {
        kotlin.runCatching {
            registerEventUseCase(REACTION_EVENT_TYPE, streamName, topicName)
        }.fold(
            onSuccess = { registeredEvent ->
                queueReactionId = registeredEvent.queueId
                lastEventReactionId = -1
                while (true) {
                    try {
                        val events = getEmojiEventUseCase(queueReactionId, lastEventReactionId)
                        emit(Event.Internal.UpdateReactions(events.events.map { it }))
                        lastEventReactionId = events.events.last().id
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        subscribeToReaction(streamName, topicName)
                        break
                    }
                }
            },
            onFailure = {
                deleteEventUseCase(queueReactionId)
            }
        )
    }

    private suspend fun FlowCollector<Event>.subscribeToMessages(
        streamName: String,
        topicName: String?
    ) {
        kotlin.runCatching {
            registerEventUseCase(MESSAGE_EVENT_TYPE, streamName, topicName)
        }.fold(
            onSuccess = { registeredEvent ->
                queueMessageId = registeredEvent.queueId
                lastEventMessageId = -1
                while (true) {
                    try {
                        val events = getMessagesEventUseCase(queueMessageId, lastEventMessageId)

                        events.forEach { messageEvent ->
                            when (messageEvent.type) {
                                Add -> {
                                    if (messageEvent.message != null) {
                                        emit(Event.Internal.UpdateMessage(messageEvent.message))
                                    }
                                }
                                Delete -> {
                                    if (messageEvent.messageId != null)
                                        emit(Event.Internal.DeleteMessage(messageEvent.messageId))
                                }
                                Edit -> {
                                    if (messageEvent.content != null && messageEvent.messageId != null) {
                                        emit(Event.Internal.EditContentMessage(messageEvent.messageId, messageEvent.content))
                                    }
                                }
                                Move -> {
                                    if (messageEvent.messageIds != null
                                        && messageEvent.subject != null
                                    ) {
                                        messageEvent.messageIds.forEach {
                                            kotlin.runCatching {
                                                getSingleMessageUseCase(it)
                                            }.fold(
                                                onSuccess = {
                                                    emit(Event.Internal.UpdateMessage(it))
                                                },
                                                onFailure = {
                                                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        lastEventMessageId = events.last().id
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        subscribeToMessages(streamName, topicName)
                        break
                    }
                }
            },
            onFailure = {
            }
        )
    }

    private fun onEmojiClick(messageId: Int, emojiCode: String, messages: List<MessageItem>): Flow<Event> {

        val message = messages.find { messageItem -> messageItem.id == messageId }

        message?.let { messageItem ->
            val messageReactions = messageItem.listReactions
            val reaction = messageReactions.find { it.emoji == emojiCode && it.userId == ME_USER_ID_CONST }
            return flow {
                kotlin.runCatching {
                    if (reaction == null) {
                        addEmojiUseCase(messageId, emojiCode.toEmojiName())
                    } else {
                        removeEmojiUseCase(messageId, reaction.emojiName)
                    }
                }.fold(
                    onSuccess = {
                    },
                    onFailure = {
                        emit(Event.Internal.ErrorLoading(it.message.toString()))
                    }
                )
            }
        }
        return emptyFlow()
    }

    private fun postMessage(streamName: String, topicName: String, content: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                postMessageUseCase(streamName, topicName, content)
            }.fold(
                onSuccess = {
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun deleteMessage(messageId: Int): Flow<Event> {
        return flow {
            kotlin.runCatching {
                deleteMessageUseCase(messageId)
            }.fold(
                onSuccess = {
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun editMessage(messageId: Int, content: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                editMessageUseCase(messageId, content)
            }.fold(
                onSuccess = {
                    if (it.isFailure)
                        emit(Event.Internal.ErrorLoading(it.exceptionOrNull()?.message ?: "Unknown error"))
                },
                onFailure = {
                    emit(Event.Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }

    private fun getMessages(streamName: String, topicName: String?, anchor: String): Flow<Event> {
        return flow {
            kotlin.runCatching {
                val messagesFlow = getMessagesUseCase(streamName, topicName, anchor)
                messagesFlow.collect {
                    emit(Event.Internal.ValueLoaded(it))
                }
            }.getOrElse {
                emit(Event.Internal.ErrorLoading(it.message.toString()))
            }
        }
    }

    companion object {
        private const val EMPTY_STRING = ""
        private const val REACTION_EVENT_TYPE = "reaction"
        private const val MESSAGE_EVENT_TYPE = "update_message,message,delete_message"
        private const val INITIAL_QUERY_ID = -1
    }
}
