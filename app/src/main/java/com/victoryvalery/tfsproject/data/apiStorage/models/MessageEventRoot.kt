package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageEventRoot(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val msg: String,
    @SerialName("events")
    val events: List<MessageEvent>
)

@Serializable
data class MessageEvent(
    @SerialName("type")
    val type: String,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("id")
    val id: Int,

//delete_message
    @SerialName("streamId")
    val streamId: Int? = null,
    @SerialName("topic")
    val topic: String? = null,
    @SerialName("message_id")
    val messageId: Int? = null,

//update_message content
    @SerialName("editTimestamp")
    val editTimestamp: Long? = null,
    @SerialName("content")
    val content: String? = null,

//update_message moving
    @SerialName("orig_subject")
    val origSubject: String? = null,
    @SerialName("subject")
    val subject: String? = null,
    @SerialName("message_ids")
    val messageIds: List<Int>? = null,
)
