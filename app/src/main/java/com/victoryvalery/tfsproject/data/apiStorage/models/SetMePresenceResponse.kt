package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetMePresenceResponse(
    @SerialName("result")
    val result: String
)
