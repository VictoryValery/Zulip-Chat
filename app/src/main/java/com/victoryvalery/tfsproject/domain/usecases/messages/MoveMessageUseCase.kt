package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class MoveMessageUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(messageId: Int, topicName: String): Result<Boolean> {
        return repository.moveMessage(messageId, topicName)
    }
}
