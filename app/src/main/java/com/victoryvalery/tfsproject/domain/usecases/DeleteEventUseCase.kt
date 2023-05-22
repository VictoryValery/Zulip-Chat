package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(queueId: String) {
        repository.deleteEvent(queueId)
    }
}
