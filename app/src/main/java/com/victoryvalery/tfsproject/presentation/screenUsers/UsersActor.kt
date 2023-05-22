package com.victoryvalery.tfsproject.presentation.screenUsers

import com.victoryvalery.tfsproject.domain.models.toStatus
import com.victoryvalery.tfsproject.domain.usecases.GetAllUsersPresenceUseCase
import com.victoryvalery.tfsproject.domain.usecases.GetAllUsersUseCase
import com.victoryvalery.tfsproject.domain.usecases.PostMePresenceUseCase
import com.victoryvalery.tfsproject.presentation.screenUsers.Event.Internal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import vivid.money.elmslie.coroutines.Actor
import javax.inject.Inject

class UsersActor @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getAllUsersPresenceUseCase: GetAllUsersPresenceUseCase,
    private val postMePresenceUseCase: PostMePresenceUseCase,
) : Actor<Command, Event> {

    override fun execute(command: Command): Flow<Event> {
        return when (command) {
            Command.LoadUsers -> {
                getAllUsers()
            }
            Command.SetMePresence -> {
                flow { postMePresenceUseCase() }
            }
        }
    }

    private fun getAllUsers(): Flow<Event> {
        return flow {
            try {
                getAllUsersUseCase().zip(getAllUsersPresenceUseCase()) { allUsers, presenceMap ->
                    allUsers
                        .map { user ->
                            user.copy(status = presenceMap[user.userId].toStatus())
                        }
                        .sortedByDescending { it.status }
                }.collect { usersInfo ->
                    emit(Internal.ValueLoaded(usersInfo))
                }
            } catch (throwable: Throwable) {
                emit(Internal.ErrorLoading(throwable.message.toString()))
            }
        }
    }
}
