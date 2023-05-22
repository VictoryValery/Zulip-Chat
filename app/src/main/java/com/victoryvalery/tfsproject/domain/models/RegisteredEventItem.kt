package com.victoryvalery.tfsproject.domain.models

data class RegisteredEventItem(
    val result: String,
    val msg: String,
    val queueId: String,
    val maxMessageId: Int? = null,
    val lastEventId: Int,
)
