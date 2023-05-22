package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class GetAllStreamTopicsUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(streamId: Int): List<String> {
        return repository.getAllStreamTopics(streamId)
    }
}
