package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class AddStreamUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(name: String, description: String):Boolean {
        return repository.addStream(name, description)
    }
}
