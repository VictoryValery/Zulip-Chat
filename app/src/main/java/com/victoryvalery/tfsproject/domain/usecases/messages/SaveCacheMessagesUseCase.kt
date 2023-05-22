package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class SaveCacheMessagesUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(streamName: String, topicName: String?, messages: List<MessageItem>) {
        repository.rewriteMessages(streamName, topicName, messages)
    }
}
