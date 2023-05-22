package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisteredEvent(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val msg: String,
    @SerialName("queue_id")
    val queueId: String,
    @SerialName("max_message_id")
    val maxMessageId: Int? = null,
    @SerialName("last_event_id")
    val lastEventId: Int,
)
