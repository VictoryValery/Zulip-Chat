package com.victoryvalery.tfsproject.domain.usecases

import com.victoryvalery.tfsproject.domain.models.UserItem
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import javax.inject.Inject

class GetMeUserUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(): UserItem {
        return repository.getMeUser()
    }
}
