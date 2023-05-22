package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.models.MessageEventItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class GetMessagesEventUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(queueId: String, lastEventId: Int): List<MessageEventItem> {
        return repository.getMessageEvent(queueId, lastEventId)
    }
}
