package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionsResponse(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("msg")
    val msg: String? = null,
    @SerialName("result")
    val result: String,
    @SerialName("uri")
    val uri: String? = null
)
