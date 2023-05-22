package com.victoryvalery.tfsproject.data.apiStorage.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersRoot(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val msg: String,
    @SerialName("members")
    val members: List<Member>,
)

@Serializable
data class Member(
    val email: String,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("avatar_url")
    val avatarUrl: String?
)
