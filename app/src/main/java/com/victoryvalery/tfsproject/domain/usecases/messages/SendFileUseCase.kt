package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class SendFileUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(filePart: MultipartBody.Part, fileName: String): String {
        return repository.sendFile(filePart, fileName)
    }
}
