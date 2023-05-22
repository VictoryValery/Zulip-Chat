package com.victoryvalery.tfsproject.domain.models

data class MessageItem(
    val id: Int,
    val userId: Int,
    val name: String,
    val message: String,
    val picture: String,
    val date: Int,
    val month: Int,
    val topic: String,
    val timestamp: Long,
    val isMeMessage: Boolean,
    val msgContent: String,
    val myMessage: Boolean,
    val listReactions: MutableList<ReactionItem>
)
