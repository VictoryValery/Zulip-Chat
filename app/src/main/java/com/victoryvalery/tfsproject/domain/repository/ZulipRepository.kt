package com.victoryvalery.tfsproject.domain.repository

import com.victoryvalery.tfsproject.domain.models.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.Response

interface ZulipRepository {

    suspend fun addStream(name: String, description: String): Boolean

    suspend fun sendFile(filePart: MultipartBody.Part, fileName: String): String

    suspend fun downloadFile(fileUrl: String): Response

    suspend fun registerEvent(eventType: String, streamName: String, topicName: String?): RegisteredEventItem

    suspend fun getMessageEvent(queueId: String, lastEventId: Int): List<MessageEventItem>

    suspend fun getEmojiEvent(queueId: String, lastEventId: Int): EmojiEventRootItem

    suspend fun deleteEvent(queueId: String)

    suspend fun getMeUser(): UserItem

    suspend fun getAllUsers(): Flow<List<UserItem>>

    suspend fun getUserPresence(userId: Int): String

    suspend fun getAllUsersPresence(): Flow<Map<Int, String>>

    suspend fun postMePresence()

    suspend fun getAllStreams(): Flow<List<StreamItem>>

    suspend fun getAllStreamTopics(streamId: Int): List<String>

    suspend fun getSubscribedStreams(): Flow<List<StreamItem>>

    suspend fun getMessages(
        streamName: String,
        topicName: String?,
        anchor: String
    ): Flow<Pair<String, List<MessageItem>>>

    suspend fun getSingleMessage(messageId: Int): MessageItem

    suspend fun deleteSingleMessage(messageId: Int): Boolean

    suspend fun editSingleMessage(messageId: Int, content: String): Result<Boolean>

    suspend fun getCountedTopics(streamName: String, streamId: Int): Flow<Pair<String, List<TopicItem>>>

    suspend fun postMessage(streamName: String, topicName: String, content: String): Boolean

    suspend fun addReaction(messageId: Int, emojiName: String): Boolean

    suspend fun removeReaction(messageId: Int, emojiName: String): Boolean

    suspend fun rewriteMessages(streamName: String, topicName: String?, messages: List<MessageItem>)

    suspend fun moveMessage(messageId: Int, topicName: String): Result<Boolean>

}
