package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class PostMessageUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(streamName: String, topicName: String, content: String): Boolean {
        return repository.postMessage(streamName, topicName, content)
    }
}
