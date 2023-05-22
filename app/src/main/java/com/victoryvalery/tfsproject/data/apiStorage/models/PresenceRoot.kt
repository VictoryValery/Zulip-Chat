package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresenceRoot(
    @SerialName("msg")
    val msg: String,
    @SerialName("presence")
    val presence: Presence,
    @SerialName("result")
    val result: String,
)

@Serializable
data class Presence(
    @SerialName("aggregated")
    val aggregated: Aggregated
)

@Serializable
data class Aggregated(
    @SerialName("status")
    val status: String,
    @SerialName("timestamp")
    val timestamp: Int = 0,
)
