package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.models.StreamItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscribedStreamsUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(): Flow<List<StreamItem>> {
        return repository.getSubscribedStreams()
    }
}
