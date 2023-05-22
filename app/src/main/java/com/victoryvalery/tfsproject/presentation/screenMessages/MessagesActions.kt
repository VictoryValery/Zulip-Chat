package com.victoryvalery.tfsproject.presentation.screenMessages

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class MessagesActions : Parcelable {
    @Parcelize
    object AddReaction : MessagesActions()

    @Parcelize
    object CopyToClipboard : MessagesActions()

    @Parcelize
    object DeleteMessage : MessagesActions()

    @Parcelize
    object EditMessage : MessagesActions()

    @Parcelize
    object MoveMessage : MessagesActions()

    @Parcelize
    object DownloadMessageContent : MessagesActions()
}
