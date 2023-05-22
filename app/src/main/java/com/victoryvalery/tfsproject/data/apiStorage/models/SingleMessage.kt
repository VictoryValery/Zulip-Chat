package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleMessage(
    @SerialName("message")
    val message: Message
)
