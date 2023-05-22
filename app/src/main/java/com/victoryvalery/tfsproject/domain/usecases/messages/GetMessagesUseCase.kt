package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(
        streamName: String,
        topicName: String?,
        anchor: String
    ): Flow<Pair<String, List<MessageItem>>> {
        return repository.getMessages(streamName, topicName, anchor)
    }
}
