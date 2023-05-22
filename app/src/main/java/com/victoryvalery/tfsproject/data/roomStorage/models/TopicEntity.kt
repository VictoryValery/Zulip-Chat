package com.victoryvalery.tfsproject.data.roomStorage.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey
    val id: Int,
    val parentStreamId: Int,
    val parentStreamName: String,
    val maxId: Int,
    val name: String,
    var msg: Int
)
