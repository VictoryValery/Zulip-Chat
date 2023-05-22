package com.victoryvalery.tfsproject.domain.models

data class EmojiEventRootItem(
    val result: String,
    val msg: String,
    val events: List<EmojiEventItem>
)

data class EmojiEventItem(
    val type: String,
    val operation: String,
    val userId: Int,
    val messageId: Int,
    val emojiName: String,
    val emojiCode: String,
    val reactionType: String,
    val id: Int
)
