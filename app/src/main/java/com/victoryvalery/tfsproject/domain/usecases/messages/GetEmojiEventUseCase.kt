package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.models.EmojiEventRootItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class GetEmojiEventUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(queueId: String, lastEventId: Int): EmojiEventRootItem {
        return repository.getEmojiEvent(queueId, lastEventId)
    }
}
