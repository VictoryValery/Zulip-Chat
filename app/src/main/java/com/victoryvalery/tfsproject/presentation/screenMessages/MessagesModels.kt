package com.victoryvalery.tfsproject.presentation.screenMessages

import com.victoryvalery.tfsproject.domain.models.EmojiEventItem
import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem
import okhttp3.MultipartBody
import okhttp3.Response
import vivid.money.elmslie.coroutines.ElmStoreCompat
import javax.inject.Inject

data class State(
    val isLoading: Boolean = false,
    val isUpperLoaderVisible: Boolean = false,
    val messagesList: List<DelegateItem> = emptyList(),
    val updateMessageId: Int? = null,
    val isError: Boolean = false,
    val streamName: String = "",
    val streamId: Int = 0,
    val topicName: String? = null,
    val topicsList: List<String> = emptyList(),
    val isAllDataLoaded: Boolean? = null,
    val isSubscribed: Boolean = false
)

sealed class Event {

    sealed class Ui : Event() {
        object Initial : Ui()
        data class SubscriptionOnEvents(
            val streamName: String,
            val streamId: Int,
            val isSubscribed: Boolean,
            val topicName: String?
        ) : Ui()

        data class MessagesLoading(val anchor: String) : Ui()
        data class PostingMessage(val content: String, val topicName: String) : Ui()
        data class EmojiClick(val messageId: Int, val emojiCode: String) : Ui()
        data class DeleteMessage(val messageId: Int) : Ui()
        data class EditMessage(val messageId: Int, val content: String) : Ui()
        data class MoveMessage(val messageId: Int, val topic: String) : Ui()
        data class SendFile(val filePart: MultipartBody.Part, val fileName: String, val topicName: String) : Ui()
        data class DownloadFile(val fileUrl: String) : Ui()
        object ExitClick : Ui()
        object StreamSubscribe : Ui()
        data class TopicClick(val topicName: String) : Ui()
    }

    sealed class Internal : Event() {
        data class ValueLoaded(val messagesList: Pair<String, List<MessageItem>>) : Internal()
        data class TopicsLoaded(val topicsList: List<String>) : Internal()
        data class UpdateMessage(val messageItem: MessageItem) : Internal()
        data class EditContentMessage(val messageId: Int, val content: String) : Internal()
        data class DeleteMessage(val messageId: Int) : Internal()
        data class FileSent(val link: String, val topicName: String) : Internal()
        data class FileDownloaded(val response: Response, val fileName: String, val contentType: String) : Internal()
        data class UpdateReactions(val reactionsItemList: List<EmojiEventItem>) : Internal()
        data class ErrorLoading(val errorMessage: String) : Internal()
        object StreamSubscribed : Internal()
    }
}
sealed class Command {

    data class SendFile(val filePart: MultipartBody.Part, val fileName: String, val topicName: String) : Command()
    data class SubscribeOnEvents(val streamName: String, val topicName: String?) : Command()
    data class SaveCacheMessages(
        val streamName: String,
        val topicName: String?,
        val messages: List<MessageItem>
    ) : Command()

    data class LoadMessages(val streamName: String, val topicName: String?, val anchor: String) : Command()
    data class PostMessage(val streamName: String, val topicName: String, val content: String) : Command()
    data class DeleteMessage(val messageId: Int) : Command()
    data class EditMessage(val messageId: Int, val content: String) : Command()
    data class MoveMessage(val messageId: Int, val topic: String) : Command()
    data class PostEmojiClick(val messageId: Int, val emojiCode: String, val messages: List<MessageItem>) : Command()
    data class LoadTopics(val streamId: Int) : Command()
    data class DownloadFile(val fileUrl: String) : Command()
    data class StreamSubscribe(val streamName: String) : Command()
}

sealed class Effect {
    data class ShowError(val errorMessage: String) : Effect()
    data class ShowInfo(val infoMessage: String) : Effect()
    data class FileDownloadedEffect(val response: Response, val fileName: String, val contentType: String) : Effect()
    object ClickExit : Effect()
    data class NavigateToMessages(val topicName: String, val isSubscribed: Boolean) : Effect()
}

class StoreFactory @Inject constructor(
    private val actor: MessagesActor
) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = State(),
            reducer = MessagesReducer,
            actor = actor
        )
    }

    fun provide() = store
}
