package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionsRoot(
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String,
    @SerialName("subscriptions")
    val subscriptions: List<Stream>,
)
