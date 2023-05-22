package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUsersPresenceUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(): Flow<Map<Int, String>> {
        return repository.getAllUsersPresence()
    }
}
