package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.models.RegisteredEventItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class RegisterEventUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(eventType: String, streamName: String, topicName: String?): RegisteredEventItem {
        return repository.registerEvent(eventType, streamName, topicName)
    }
}
