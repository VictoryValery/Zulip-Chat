package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmojiEventRoot(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val msg: String,
    @SerialName("events")
    val events: List<EmojiEvent>
)

@Serializable
data class EmojiEvent(
    @SerialName("type")
    val type: String,
    @SerialName("op")
    val operation: String,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("message_id")
    val messageId: Int,
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("reaction_type")
    val reactionType: String,
    @SerialName("id")
    val id: Int,
)
