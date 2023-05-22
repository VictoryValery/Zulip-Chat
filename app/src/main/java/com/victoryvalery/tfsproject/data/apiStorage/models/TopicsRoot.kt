package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicsRoot(
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String,
    @SerialName("topics")
    val topics: List<Topic>,
)

@Serializable
data class Topic(
    @SerialName("max_id")
    val maxId: Int,
    @SerialName("name")
    val name: String,
)
