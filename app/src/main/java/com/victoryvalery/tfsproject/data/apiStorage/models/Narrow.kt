package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.Serializable

@Serializable
data class Narrow(
    val operator: String,
    val operand: String
)

