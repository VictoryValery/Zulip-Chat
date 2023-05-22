package com.victoryvalery.tfsproject.presentation.screenMessages

import com.victoryvalery.tfsproject.data.apiStorage.ME_USER_ID_CONST
import com.victoryvalery.tfsproject.data.apiStorage.mappers.EmojiNCS
import com.victoryvalery.tfsproject.data.repository.ZulipRepositoryImpl.Companion.ANCHOR_NEWEST
import com.victoryvalery.tfsproject.data.repository.ZulipRepositoryImpl.Companion.MESSAGES_CACHE_SIZE
import com.victoryvalery.tfsproject.domain.models.EmojiEventItem
import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.domain.models.ReactionItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.message.MessageDelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.message.toDelegateItemListWithDateSeparator
import com.victoryvalery.tfsproject.presentation.screenMessages.Event.Ui
import com.victoryvalery.tfsproject.presentation.screenMessages.Event.Internal
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

internal object MessagesReducer : ScreenDslReducer<Event, Ui, Internal, State, Effect, Command>(
    Ui::class, Internal::class
) {
    override fun Result.ui(event: Ui) = when (event) {
        is Ui.SubscriptionOnEvents -> {
            state {
                copy(
                    streamName = event.streamName,
                    streamId = event.streamId,
                    topicName = event.topicName,
                    isSubscribed = event.isSubscribed
                )
            }
            commands { +Command.SubscribeOnEvents(event.streamName, event.topicName) }
            commands { +Command.LoadTopics(state.streamId) }
        }
        is Ui.MessagesLoading -> {
            if (state.messagesList.isEmpty())
                state { copy(isLoading = true, isUpperLoaderVisible = false, isError = false, updateMessageId = null) }
            else
                state { copy(isLoading = false, isUpperLoaderVisible = true, isError = false, updateMessageId = null) }
            commands { +Command.LoadMessages(state.streamName, state.topicName, event.anchor) }
        }
        is Ui.PostingMessage -> {
            state { copy(isLoading = false, isError = false, updateMessageId = null) }
            commands { +Command.PostMessage(state.streamName, event.topicName, event.content) }
        }
        is Ui.EmojiClick -> {
            val messages = state.messagesList.filterIsInstance<MessageDelegateItem>()
                .map { it.content() as MessageItem }
            state { copy(isLoading = false, isError = false, updateMessageId = null) }
            commands { +Command.PostEmojiClick(event.messageId, event.emojiCode, messages) }
        }
        Ui.ExitClick -> {
            val messages = state.messagesList.filterIsInstance<MessageDelegateItem>()
                .take(MESSAGES_CACHE_SIZE)
                .map { it.content() as MessageItem }
            commands { +Command.SaveCacheMessages(state.streamName, state.topicName, messages) }
            effects { +Effect.ClickExit }
        }
        Ui.Initial -> {
            state { copy(isLoading = true, updateMessageId = null) }
        }
        is Ui.SendFile -> {
            effects { +Effect.ShowInfo("File is uploading...") }
            commands { +Command.SendFile(event.filePart, event.fileName, event.topicName) }
        }
        is Ui.DeleteMessage -> {
            commands { +Command.DeleteMessage(event.messageId) }
        }
        is Ui.EditMessage -> {
            commands { +Command.EditMessage(event.messageId, event.content) }
        }
        is Ui.MoveMessage -> {
            val messages = state.messagesList.filterIsInstance<MessageDelegateItem>()
                .map { it.content() as MessageItem }
                .toMutableList()
            if (messages.find { it.id == event.messageId } != null) {
                messages.removeIf { it.id == event.messageId }
                val allTopics = state.topicsList.union(messages.map { it.topic }).toList()
                state {
                    copy(
                        updateMessageId = null,
                        messagesList = messages.toDelegateItemListWithDateSeparator(state.topicName != null),
                        topicsList = allTopics
                    )
                }
                commands { +Command.MoveMessage(event.messageId, event.topic) }
            } else {
                Unit
            }
        }
        is Ui.TopicClick -> {
            effects { +Effect.NavigateToMessages(event.topicName, state.isSubscribed) }
        }
        is Ui.DownloadFile -> {
            effects { +Effect.ShowInfo("File is downloading...") }
            commands { +Command.DownloadFile(event.fileUrl) }
        }
        Ui.StreamSubscribe -> {
            commands { +Command.StreamSubscribe(state.streamName) }
        }
    }

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.ErrorLoading -> {
            state { copy(isLoading = false, isUpperLoaderVisible = false, updateMessageId = null, isError = true) }
            effects { +Effect.ShowError(event.errorMessage) }
        }
        is Internal.ValueLoaded -> {
            val newPageList = if (event.messagesList.first == ANCHOR_NEWEST) {
                event.messagesList.second
            } else {
                event.messagesList.second + state.messagesList.filterIsInstance<MessageDelegateItem>()
                    .map { it.content() as MessageItem }
            }
            val allTopics = state.topicsList.union(newPageList.map { it.topic }).toList()
            state {
                copy(
                    isLoading = event.messagesList.second.isEmpty() && state.isAllDataLoaded == null, isUpperLoaderVisible = false, updateMessageId = null,
                    messagesList = newPageList.toDelegateItemListWithDateSeparator(state.topicName != null),
                    topicsList = allTopics, isAllDataLoaded = event.messagesList.second.isEmpty()
                )
            }
        }
        is Internal.UpdateMessage -> {
            if (state.topicName == null || state.topicName == event.messageItem.topic) {
                val updatedMessages = updateMessages(state.messagesList, event.messageItem)
                val allTopics = state.topicsList.union(updatedMessages.map { it.topic }).toList()
                state {
                    copy(
                        updateMessageId = null,
                        messagesList = updatedMessages.toDelegateItemListWithDateSeparator(state.topicName != null),
                        topicsList = allTopics
                    )
                }
            } else {
                Unit
            }
        }
        is Internal.DeleteMessage -> {
            val messages = state.messagesList.filterIsInstance<MessageDelegateItem>()
                .map { it.content() as MessageItem }
                .toMutableList()
            if (messages.find { it.id == event.messageId } != null) {
                messages.removeIf { it.id == event.messageId }
                val allTopics = state.topicsList.union(messages.map { it.topic }).toList()
                state {
                    copy(
                        updateMessageId = null,
                        messagesList = messages.toDelegateItemListWithDateSeparator(state.topicName != null),
                        topicsList = allTopics
                    )
                }
            } else {
                Unit
            }
        }
        is Internal.UpdateReactions -> {
            event.reactionsItemList.forEach { emojiEvent ->
                val updatedReactions = updateReactions(state.messagesList, emojiEvent, state.topicName != null)
                state { copy(updateMessageId = updatedReactions.first, messagesList = updatedReactions.second) }
            }
        }
        is Internal.FileSent -> {
            commands { +Command.PostMessage(state.streamName, event.topicName, event.link) }
        }
        is Internal.EditContentMessage -> {
            val updatedMessages = state.messagesList.filterIsInstance<MessageDelegateItem>()
                .map { it.content() as MessageItem }
                .map { message ->
                    if (message.id == event.messageId) {
                        message.copy(message = event.content)
                    } else {
                        message
                    }
                }

            val allTopics = state.topicsList.union(updatedMessages.map { it.topic }).toList()
            state {
                copy(
                    updateMessageId = event.messageId,
                    messagesList = updatedMessages.toDelegateItemListWithDateSeparator(state.topicName != null),
                    topicsList = allTopics
                )
            }
        }
        is Internal.TopicsLoaded -> {
            state { copy(topicsList = event.topicsList) }
        }
        is Internal.FileDownloaded -> {
            effects { +Effect.FileDownloadedEffect(event.response, event.fileName, event.contentType) }
            effects { +Effect.ShowInfo("File ${event.fileName} loaded to Downloads") }
        }
        Internal.StreamSubscribed -> {
            state { copy(isSubscribed = true) }
        }
    }

    private fun updateReactions(
        messagesList: List<DelegateItem>, emojiEvent: EmojiEventItem,
        topicChat: Boolean
    ): Pair<Int?, List<DelegateItem>> {
        val messages = messagesList.filterIsInstance<MessageDelegateItem>()
            .map { it.content() as MessageItem }
            .toMutableList()

        val message = messages.find { messageItem -> messageItem.id == emojiEvent.messageId }

        message?.let { messageItem ->
            val messageReactions = messageItem.listReactions

            when (emojiEvent.operation) {
                ADD -> {
                    messageReactions.add(
                        ReactionItem(
                            userId = emojiEvent.userId,
                            emoji = EmojiNCS(emojiEvent.emojiName, emojiEvent.emojiCode).getCodeString(),
                            emojiName = emojiEvent.emojiName,
                            isClicked = emojiEvent.userId == ME_USER_ID_CONST
                        )
                    )
                }
                REMOVE -> {
                    val reaction = messageReactions.find {
                        it.userId == emojiEvent.userId && it.emojiName == emojiEvent.emojiName
                    }
                    messageReactions.remove(reaction)
                }
                else -> {
                    null to messages.toDelegateItemListWithDateSeparator(topicChat = topicChat)
                }
            }
            return messageItem.id to messages.toDelegateItemListWithDateSeparator(topicChat)
        }
        return null to messages.toDelegateItemListWithDateSeparator(topicChat)
    }

    private fun updateMessages(
        messagesList: List<DelegateItem>, messageItem: MessageItem
    ): List<MessageItem> {
        val messages = messagesList.filterIsInstance<MessageDelegateItem>()
            .map { it.content() as MessageItem }
            .toMutableList()
        messages.add(messageItem)
        return messages.sortedBy { it.id }
    }

    private const val ADD = "add"
    private const val REMOVE = "remove"
}
