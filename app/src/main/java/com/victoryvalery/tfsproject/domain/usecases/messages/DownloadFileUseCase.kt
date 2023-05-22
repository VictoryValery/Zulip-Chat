package com.victoryvalery.tfsproject.domain.usecases.messages

import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import okhttp3.Response
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(
    private val repository: ZulipRepository
) {
    suspend operator fun invoke(fileUrl: String): Response {
        return repository.downloadFile(fileUrl)
    }
}
