package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class GetSingleMessageUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(messageId: Int): MessageItem {
        return repository.getSingleMessage(messageId)
    }
}
