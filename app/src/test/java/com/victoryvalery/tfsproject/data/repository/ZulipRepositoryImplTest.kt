package com.victoryvalery.tfsproject.data.repository

import com.victoryvalery.tfsproject.data.apiStorage.ZulipApi
import com.victoryvalery.tfsproject.data.apiStorage.mappers.toMessageItem
import com.victoryvalery.tfsproject.data.apiStorage.models.Message
import com.victoryvalery.tfsproject.data.apiStorage.models.MessagesRoot
import com.victoryvalery.tfsproject.data.roomStorage.ZulipDao
import com.victoryvalery.tfsproject.data.roomStorage.mappers.toMessageItem
import com.victoryvalery.tfsproject.data.roomStorage.models.MessageEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ZulipRepositoryImplTest {

    private val zulipApi: ZulipApi = mockk()
    private val zulipDao: ZulipDao = mockk()
    private val okHttpClient: OkHttpClient = mockk()

    private val zulipRepositoryImpl = ZulipRepositoryImpl(zulipApi, zulipDao, okHttpClient)
    private val streamName = "general"
    private val topicName = "new streams"
    private val anchor = ZulipRepositoryImpl.ANCHOR_NEWEST

    @Test
    fun `getMessages when DB is empty`() = runTest {
        val mockedDaoMessages = emptyList<MessageEntity>()

        coEvery {
            zulipDao.getLastChatMessages(streamName, topicName)
        } returns mockedDaoMessages

        val result = zulipRepositoryImpl.getMessages(streamName, topicName, anchor).first()

        coVerify { zulipDao.getLastChatMessages(streamName, topicName) }

        assertEquals(anchor to mockedDaoMessages.map { it.toMessageItem() }, result)
    }

    @Test
    fun `getMessages when DB is NOT empty`() = runTest {
        val mockedDaoMessages = listOf(
            MessageEntity(
                id = 0,
                streamName = streamName,
                topicName = topicName,
                senderId = 0,
                senderFullName = "",
                avatarUrl = "",
                content = "",
                timestamp = 0L,
                isMeMessage = false,
                msgContent = "",
                subject = topicName,
                reactions = emptyList()
            ),
            MessageEntity(
                id = 2,
                streamName = streamName,
                topicName = topicName,
                senderId = 0,
                senderFullName = "",
                avatarUrl = "",
                content = "",
                timestamp = 0L,
                isMeMessage = false,
                msgContent = "",
                subject = topicName,
                reactions = emptyList()
            ),
        )

        coEvery {
            zulipDao.getLastChatMessages(streamName, topicName)
        } returns mockedDaoMessages

        val result = zulipRepositoryImpl.getMessages(streamName, topicName, anchor).first()

        coVerify { zulipDao.getLastChatMessages(streamName, topicName) }

        assertEquals(anchor to mockedDaoMessages.map { it.toMessageItem() }, result)
    }

    @Test
    fun `getMessages with Error loading data from the server`() = runTest {
        val streamName = "gene"
        val topicName = "new strea"
        val anchor = "77"

        val mockedApiResponse = MessagesRoot(
            result = "error",
            messages = emptyList()
        )
        val queryMap = getQueryMapMessages(streamName, topicName, anchor)
        coEvery {
            zulipApi.getMessages(queryMap)
        } returns mockedApiResponse

        val result = zulipRepositoryImpl.getMessages(streamName, topicName, anchor).first()

        coVerify { zulipApi.getMessages(queryMap) }

        assertEquals(anchor to emptyList<Message>().map { it.toMessageItem() }, result)
    }

}
