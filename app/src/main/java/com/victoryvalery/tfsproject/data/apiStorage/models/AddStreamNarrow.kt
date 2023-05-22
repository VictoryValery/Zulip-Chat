package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.Serializable

@Serializable
data class AddStreamNarrow(
    val name: String,
    val description: String
)
