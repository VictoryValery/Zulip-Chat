package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.models.UserItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(): Flow<List<UserItem>> {
        return repository.getAllUsers()
    }
}
