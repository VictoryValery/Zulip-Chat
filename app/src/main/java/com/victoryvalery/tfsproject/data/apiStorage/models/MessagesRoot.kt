package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessagesRoot(
    val result: String,
    val messages: List<Message>,
)

@Serializable
data class Message(
    @SerialName("id")
    val id: Int,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("sender_full_name")
    val senderFullName: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("content")
    val content: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("is_me_message")
    val isMeMessage: Boolean,
    @SerialName("subject")
    val subject: String,
    @SerialName("reactions")
    val reactions: List<Reaction>,
)
