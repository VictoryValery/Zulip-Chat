package com.victoryvalery.tfsproject.data.repository

import com.victoryvalery.tfsproject.data.apiStorage.ME_USER_ID_CONST
import com.victoryvalery.tfsproject.data.apiStorage.ZulipApi
import com.victoryvalery.tfsproject.data.apiStorage.models.ActionsResponse
import com.victoryvalery.tfsproject.data.repository.utill.TestStoreFactory
import com.victoryvalery.tfsproject.data.roomStorage.ZulipDao
import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.domain.models.ReactionItem
import com.victoryvalery.tfsproject.presentation.delegates.message.toDelegateItemListWithDateSeparator
import com.victoryvalery.tfsproject.presentation.screenMessages.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import vivid.money.elmslie.coroutines.ElmStoreCompat

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesStoreTest {
    private lateinit var store: ElmStoreCompat<Event, State, Effect, Command>
    private lateinit var messagesActor: MessagesActor

    private val zulipApi: ZulipApi = mockk()
    private val zulipDao: ZulipDao = mockk()
    private val okHttpClient: OkHttpClient = mockk()

    private val zulipRepositoryImpl = ZulipRepositoryImpl(zulipApi, zulipDao, okHttpClient)

    private val streamName = "general"
    private val topicName = "new streams"
    private val emojiCode = "1f643"
    private val emojiName = "upside_down"
    private val messageId = 123

    private val mockedApiResponse = ActionsResponse(result = ZulipRepositoryImpl.SUCCESS)

    private val mockedMessagesList = listOf(
        MessageItem(
            id = messageId,
            userId = 0,
            name = "",
            message = "",
            picture = "",
            date = 1,
            month = 1,
            timestamp = 0,
            isMeMessage = false,
            myMessage = false,
            msgContent = "",
            topic = topicName,
            listReactions = mutableListOf()
        )
    )

    @Before
    fun setUp() {
        messagesActor = mockkClass(MessagesActor::class)
    }

    @Test
    fun `emoji ADD event`() = runTest {

        val initialState = State(
            isLoading = false,
            isUpperLoaderVisible = false,
            messagesList = mockedMessagesList.toDelegateItemListWithDateSeparator(true),
            updateMessageId = null,
            isError = false,
            streamName = streamName,
            topicName = topicName
        )

        val testStoreFactory = TestStoreFactory(messagesActor, initialState)
        store = testStoreFactory.provide()

        coEvery {
            zulipApi.addReaction(messageId, emojiName)
        } returns mockedApiResponse

        store.accept(Event.Ui.EmojiClick(messageId, emojiCode))

        val resultAdd = zulipRepositoryImpl.addReaction(messageId, emojiName)

        coVerify { messagesActor.execute(Command.PostEmojiClick(messageId, emojiCode, mockedMessagesList)) }

        assertEquals(mockedApiResponse.result.contains(ZulipRepositoryImpl.SUCCESS, true), resultAdd)

    }


    @Test
    fun `emoji REMOVE event`() = runTest {

        mockedMessagesList.first().listReactions.add(
            ReactionItem(
                emoji = emojiCode,
                userId = ME_USER_ID_CONST,
                emojiName = emojiName,
                isClicked = true
            )
        )

        val initialState = State(
            isLoading = false,
            isUpperLoaderVisible = false,
            messagesList = mockedMessagesList.toDelegateItemListWithDateSeparator(true),
            updateMessageId = null,
            isError = false,
            streamName = streamName,
            topicName = topicName
        )

        val testStoreFactory = TestStoreFactory(messagesActor, initialState)
        store = testStoreFactory.provide()

        coEvery {
            zulipApi.removeReaction(messageId, emojiName)
        } returns mockedApiResponse

        store.accept(Event.Ui.EmojiClick(messageId, emojiCode))

        val resultRemove = zulipRepositoryImpl.removeReaction(messageId, emojiName)

        coVerify { messagesActor.execute(Command.PostEmojiClick(messageId, emojiCode, mockedMessagesList)) }

        assertEquals(mockedApiResponse.result.contains(ZulipRepositoryImpl.SUCCESS, true), resultRemove)

    }

}
