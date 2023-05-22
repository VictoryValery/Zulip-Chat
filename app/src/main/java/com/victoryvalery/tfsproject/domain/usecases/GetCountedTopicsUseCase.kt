package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.models.TopicItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCountedTopicsUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(streamName: String, streamId: Int): Flow<Pair<String, List<TopicItem>>> {
        return repository.getCountedTopics(streamName, streamId)
    }
}
