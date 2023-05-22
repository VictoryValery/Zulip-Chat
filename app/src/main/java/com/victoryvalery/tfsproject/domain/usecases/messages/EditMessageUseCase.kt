package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class EditMessageUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(messageId: Int, content: String): Result<Boolean> {
        return repository.editSingleMessage(messageId, content)
    }
}
