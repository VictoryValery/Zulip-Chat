package com.victoryvalery.tfsproject.data.roomStorage.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: Int,
    val email: String,
    val fullName: String,
    val avatarUrl: String?,
    val isMeUser: Boolean
)
