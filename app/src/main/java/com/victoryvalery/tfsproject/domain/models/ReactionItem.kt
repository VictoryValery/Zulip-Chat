package com.victoryvalery.tfsproject.domain.models

data class ReactionItem(
    val userId: Int,
    val emoji: String,
    val emojiName: String,
    var isClicked: Boolean,
)
