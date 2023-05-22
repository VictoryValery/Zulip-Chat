package com.victoryvalery.tfsproject.domain.models

data class MessageEventRootItem(
    val result: String,
    val msg: String,
    val events: List<MessageEventItem>
)

data class MessageEventItem(
    val type: MessageEventType,
    val message: MessageItem? = null,
    val id: Int,

//delete_message
    val streamId: Int?,
    val topic: String?,
    val messageId: Int?,

//update_message content
    val editTimestamp: Long?,
    val content: String?,

//update_message moving
    val origSubject: String?,
    val subject: String?,
    val messageIds: List<Int>?,
)

sealed class MessageEventType {
    object Add : MessageEventType()
    object Delete : MessageEventType()
    object Edit : MessageEventType()
    object Move : MessageEventType()
}
