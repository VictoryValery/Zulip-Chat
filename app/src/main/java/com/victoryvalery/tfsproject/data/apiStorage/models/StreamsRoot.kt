package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamsRoot(
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String,
    @SerialName("streams")
    val streams: List<Stream>,
)

@Serializable
data class Stream(
    @SerialName("stream_id")
    val streamId: Int,
    @SerialName("description")
    val description: String,
    @SerialName("name")
    val name: String,
)
