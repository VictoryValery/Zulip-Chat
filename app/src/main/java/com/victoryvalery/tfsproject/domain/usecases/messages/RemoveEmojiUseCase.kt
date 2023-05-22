package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class RemoveEmojiUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(messageId: Int, emojiName: String): Boolean {
        return repository.removeReaction(messageId, emojiName)
    }
}
