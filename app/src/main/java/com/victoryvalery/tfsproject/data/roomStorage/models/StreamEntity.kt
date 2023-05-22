package com.victoryvalery.tfsproject.data.roomStorage.models

import androidx.room.Entity

@Entity(
    tableName = "streams",
    primaryKeys = ["streamId", "isSubscribed"]
)
data class StreamEntity(
    val streamId: Int,
    var isSubscribed: Boolean,
    val description: String,
    val name: String,
)
