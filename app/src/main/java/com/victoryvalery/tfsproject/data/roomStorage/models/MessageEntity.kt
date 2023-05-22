package com.victoryvalery.tfsproject.data.roomStorage.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.victoryvalery.tfsproject.data.apiStorage.models.Reaction
import com.victoryvalery.tfsproject.data.roomStorage.mappers.ReactionsJsonConverter

@Entity(tableName = "messages")
@TypeConverters(ReactionsJsonConverter::class)
data class MessageEntity(
    @PrimaryKey
    val id: Int,
    val streamName: String,
    val topicName: String,
    val senderId: Int,
    val senderFullName: String,
    val avatarUrl: String,
    val content: String,
    val timestamp: Long,
    val isMeMessage: Boolean,
    val msgContent: String,
    val subject: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val reactions: List<Reaction>,
)
