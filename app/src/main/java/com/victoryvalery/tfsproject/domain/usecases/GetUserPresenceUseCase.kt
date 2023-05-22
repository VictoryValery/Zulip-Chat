package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.models.Status
import com.victoryvalery.tfsproject.domain.models.toStatus
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class GetUserPresenceUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(userId: Int): Status {
        return repository.getUserPresence(userId).toStatus()
    }
}
