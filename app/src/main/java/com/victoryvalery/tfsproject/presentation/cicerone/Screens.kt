package com.victoryvalery.tfsproject.presentation.cicerone

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.victoryvalery.tfsproject.domain.models.UserItem
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesFragment
import com.victoryvalery.tfsproject.presentation.screenProfile.ProfileFragment
import com.victoryvalery.tfsproject.presentation.screenStreams.StreamsFragment
import com.victoryvalery.tfsproject.presentation.screenUsers.UsersFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
object Screens {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun Streams() = FragmentScreen { StreamsFragment() }

    fun Messages(streamName: String, streamId: Int, subscribed: Boolean, topicName: String? = null) = FragmentScreen {
        MessagesFragment.getNewInstance(streamName, streamId, subscribed, topicName)
    }

    fun People() = FragmentScreen { UsersFragment() }

    fun MeUser() = FragmentScreen { ProfileFragment() }

    fun AnyUser(user: UserItem) = FragmentScreen { ProfileFragment.getNewInstance(user) }

}
