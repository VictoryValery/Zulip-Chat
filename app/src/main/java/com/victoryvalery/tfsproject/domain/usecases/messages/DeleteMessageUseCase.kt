package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(messageId: Int): Boolean {
        return repository.deleteSingleMessage(messageId)
    }
}
