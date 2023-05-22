package com.victoryvalery.tfsproject.presentation.screenProfile

import com.victoryvalery.tfsproject.domain.models.Status
import com.victoryvalery.tfsproject.domain.models.UserItem
import com.victoryvalery.tfsproject.domain.usecases.GetMeUserUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetUserPresenceUseCase
import com.victoryvalery.tfsproject.presentation.screenProfile.Event.Internal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vivid.money.elmslie.coroutines.Actor
import javax.inject.Inject

class ProfileActor @Inject constructor(
    private val getMeUserUseCase: GetMeUserUseCase,
    private val getUserPresenceUseCase: GetUserPresenceUseCase
) : Actor<Command, Event> {
    override fun execute(command: Command): Flow<Event> {
        return when (command) {
            Command.LoadMeUser -> {
                loadMeUser()
            }
            is Command.LoadOtherUser -> {
                loadOtherUser(command.user)
            }
        }
    }

    private fun loadOtherUser(user: UserItem): Flow<Event> {

        return flow {
            kotlin.runCatching {
                val presence = if (user.email.contains("-bot@", true))
                    Status.Active
                else
                    if (user.isActive)
                        getUserPresenceUseCase(userId = user.userId)
                    else
                        Status.Offline
                UserInfo(user, presence)
            }.fold(
                onSuccess = {
                    emit(Internal.ValueLoaded(it))
                },
                onFailure = {
                    emit(Internal.ErrorLoading(it.message.toString()))
                }
            )
        }

    }

    private fun loadMeUser(): Flow<Event> {
        return flow {
            kotlin.runCatching {
                getMeUserUseCase()
            }.fold(
                onSuccess = {
                    emit(Internal.ValueLoaded(UserInfo(it, Status.Active)))
                },
                onFailure = {
                    emit(Internal.ErrorLoading(it.message.toString()))
                }
            )
        }
    }
}
