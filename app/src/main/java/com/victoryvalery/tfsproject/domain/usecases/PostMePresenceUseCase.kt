package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class PostMePresenceUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke() {
        repository.postMePresence()
    }
}
