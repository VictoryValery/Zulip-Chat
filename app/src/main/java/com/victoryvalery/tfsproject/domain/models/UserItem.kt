package com.victoryvalery.tfsproject.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserItem(
    val email: String,
    val userId: Int,
    val fullName: String,
    val isActive: Boolean,
    var status: Status,
    val avatarUrl: String?,
    val isMeUser: Boolean = false
) : Parcelable

@Parcelize
sealed class Status(val order: Int) : Parcelable, Comparable<Status> {
    object Active : Status(1)
    object Idle : Status(0)
    object Offline : Status(-1)

    override fun compareTo(other: Status): Int = order - other.order
}


fun String?.toStatus() : Status {
    return when (this) {
        "active" -> Status.Active
        "idle" -> Status.Idle
        else -> Status.Offline
    }
}
