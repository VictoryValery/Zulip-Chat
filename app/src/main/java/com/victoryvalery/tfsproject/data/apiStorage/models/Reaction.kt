package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("reaction_type")
    val reactionType: String,
    @SerialName("user_id")
    val userId: Int,
)
