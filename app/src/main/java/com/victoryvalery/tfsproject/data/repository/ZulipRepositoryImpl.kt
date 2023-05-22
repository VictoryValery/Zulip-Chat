package com.victoryvalery.tfsproject.data.repository

import com.victoryvalery.tfsproject.data.apiStorage.API_DELAY
import com.victoryvalery.tfsproject.data.apiStorage.API_SECONDS
import com.victoryvalery.tfsproject.data.apiStorage.ZulipApi
import com.victoryvalery.tfsproject.data.apiStorage.mappers.*
import com.victoryvalery.tfsproject.data.apiStorage.models.ActionsResponse
import com.victoryvalery.tfsproject.data.apiStorage.models.AddStreamNarrow
import com.victoryvalery.tfsproject.data.roomStorage.ZulipDao
import com.victoryvalery.tfsproject.data.roomStorage.mappers.*
import com.victoryvalery.tfsproject.domain.models.*
import com.victoryvalery.tfsproject.domain.repository.ZulipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class ZulipRepositoryImpl @Inject constructor(
    private val zulipApiRepository: ZulipApi,
    private val zulipDaoRepository: ZulipDao,
    private val okHttpClient: OkHttpClient
) : ZulipRepository {

    override suspend fun addStream(name: String, description: String): Boolean {
        val newStream = zulipApiRepository.addStream(
            mapOf("subscriptions" to Json.encodeToString(listOf(AddStreamNarrow(name, description))))
        )
        return newStream.result.contains(SUCCESS, true)
    }

    override suspend fun getAllStreams(): Flow<List<StreamItem>> {
        return flow {
            val currentData = zulipDaoRepository.getAllStreams().map { it.toStreamItem() }
            emit(currentData)
            val apiData = zulipApiRepository.getAllStreams().streams
            emit(apiData.map { it.toStreamItem() })
            withContext(Dispatchers.IO) {
                zulipDaoRepository.deleteAllStreams()
                val streamEntities = apiData.map { it.toStreamsEntity(isSubscribed = false) }
                zulipDaoRepository.insertStreamsList(streamEntities)
            }
        }
    }

    override suspend fun getAllStreamTopics(streamId: Int): List<String> {
        return zulipApiRepository.getStreamTopics(streamId).topics.map { it.name }
    }

    override suspend fun getSubscribedStreams(): Flow<List<StreamItem>> {
        return flow {
            val currentData = zulipDaoRepository.getSubscribedStreams().map { it.toStreamItem() }
            emit(currentData)
            val apiData = zulipApiRepository.getSubscribedStreams().subscriptions
            emit(apiData.map { it.toStreamItem() })
            withContext(Dispatchers.IO) {
                zulipDaoRepository.deleteSubscribedStreams()
                val streamEntities = apiData.map { it.toStreamsEntity(isSubscribed = true) }
                zulipDaoRepository.insertStreamsList(streamEntities)
            }
        }
    }

    override suspend fun getMessages(
        streamName: String,
        topicName: String?,
        anchor: String
    ): Flow<Pair<String, List<MessageItem>>> {
        return flow {
            if (anchor == ANCHOR_NEWEST) {
                val currentData = if (topicName != null)
                    zulipDaoRepository.getLastChatMessages(streamName, topicName)
                else
                    zulipDaoRepository.getLastChatMessages(streamName)

                emit(anchor to currentData.map { it.toMessageItem() })
            }
            val queryMap = getQueryMapMessages(streamName, topicName, anchor)
            val lastMessages = zulipApiRepository.getMessages(queryMap)
                .messages
                .map {
                    it.toMessageItem()
                }
            emit(anchor to lastMessages)
        }
    }

    override suspend fun getMessageEvent(queueId: String, lastEventId: Int): List<MessageEventItem> {
        return zulipApiRepository.getMessageEvent(queueId, lastEventId).toMessageEventRootItem().events
    }

    override suspend fun sendFile(filePart: MultipartBody.Part, fileName: String): String {
        kotlin.runCatching {
            zulipApiRepository.sendFile(filePart)
        }.fold(
            onSuccess = { response ->
                return if (response.result == SUCCESS && response.uri != null) {
                    "[${fileName}](${response.uri})"
                } else {
                    response.msg.toString()
                }
            },
            onFailure = {
                it.message.toString()
            }
        )
        return EMPTY_RETURN_MESSAGE
    }

    override suspend fun downloadFile(fileUrl: String): Response {
        val request = Request.Builder()
            .url(fileUrl)
            .build()

        return okHttpClient.newCall(request).execute()
    }

    override suspend fun registerEvent(eventType: String, streamName: String, topicName: String?): RegisteredEventItem {
        val queryMap = getEventQueryMap(eventType, streamName, topicName)
        return zulipApiRepository.registerEvent(queryMap).toRegisteredEventItem()
    }

    override suspend fun getCountedTopics(streamName: String, streamId: Int): Flow<Pair<String, List<TopicItem>>> {
        return flow {
            val currentData = zulipDaoRepository.getStreamTopics(streamId).map { it.toTopicItem() }
            emit(ROOM_SOURCE to currentData)
            val queryMap = getQueryMapTopics(streamName)
            val apiData = zulipApiRepository.getMessages(queryMap).messages
            val group = apiData.groupBy { it.subject }
            val topics = group.map { it.key }
            val topicItems = group.map {
                mapToTopicItem(it, streamId, streamName, topics.indexOf(it.key))
            }
            emit(API_SOURCE to topicItems)

            val mesList = group.flatMap { topicPair ->
                topicPair.value.takeLast(MESSAGES_CACHE_SIZE).map {
                    it.toMessageEntity(streamName, topicPair.key)
                }
            }

            withContext(Dispatchers.IO) {
                zulipDaoRepository.deleteStreamTopics(streamId)
                val topicEntityList = topicItems.map { it.toTopicEntity() }
                zulipDaoRepository.insertTopicList(topicEntityList)

                zulipDaoRepository.deleteLastChatMessages(streamName)
                zulipDaoRepository.insertMessageList(mesList)
            }
        }
    }

    override suspend fun rewriteMessages(streamName: String, topicName: String?, messages: List<MessageItem>) {
        withContext(Dispatchers.IO) {
            if (topicName != null)
                zulipDaoRepository.insertMessageList(messages.map { it.toMessageEntity(streamName, topicName) })
            else
                zulipDaoRepository.insertMessageList(messages.map { it.toMessageEntity(streamName, it.topic) })
        }
    }

    override suspend fun getEmojiEvent(queueId: String, lastEventId: Int): EmojiEventRootItem {
        return zulipApiRepository.getEmojiEvent(queueId, lastEventId).toEmojiEventRootItem()
    }

    override suspend fun deleteEvent(queueId: String) {
        zulipApiRepository.deleteEvent(queueId)
    }

    override suspend fun getMeUser(): UserItem {
        return zulipApiRepository.getMeUser().toUserItem()
    }

    override suspend fun getAllUsers(): Flow<List<UserItem>> {
        return flow {
            val currentData = zulipDaoRepository.getAllUsers().map { it.toUserItem() }
            if (currentData.isNotEmpty())
                emit(currentData)
            val apiData = zulipApiRepository.getAllUsers().members.map { it.toUserItem() }
            emit(apiData)
            withContext(Dispatchers.IO) {
                zulipDaoRepository.deleteAllUsers()
                val usersEntities = apiData.map { it.toUserEntity() }
                zulipDaoRepository.insertUsersList(usersEntities)
            }
        }
    }

    override suspend fun getUserPresence(userId: Int): String {
        return zulipApiRepository.getUserPresence(userId).presence.aggregated.status
    }

    override suspend fun getAllUsersPresence(): Flow<Map<Int, String>> {
        return flow {
            emit(parsePresenceJsonToMap(zulipApiRepository.getALLUsersPresence()))
        }
    }

    override suspend fun postMePresence() {
        val timestamp = (System.currentTimeMillis() + API_DELAY) / API_SECONDS
        zulipApiRepository.postMePresence(timestamp = timestamp)
    }

    override suspend fun getSingleMessage(messageId: Int): MessageItem {
        return zulipApiRepository.getSingleMessage(messageId).message.toMessageItem()
    }

    override suspend fun deleteSingleMessage(messageId: Int): Boolean {
        return zulipApiRepository.deleteSingleMessage(messageId).result.contains(SUCCESS, true)
    }

    override suspend fun editSingleMessage(messageId: Int, content: String): Result<Boolean> {
        return try {
            val response = zulipApiRepository.editSingleMessage(messageId, content)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                if (result.result.contains(SUCCESS, true)) {
                    Result.success(true)
                } else {
                    Result.failure(Throwable(result.msg ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val json = Json { ignoreUnknownKeys = true }
                val errorResponse = json.decodeFromString<ActionsResponse>(errorBody ?: "")
                Result.failure(Throwable(errorResponse.msg ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moveMessage(messageId: Int, topicName: String): Result<Boolean> {
        return try {
            val response = zulipApiRepository.moveMessage(messageId, topicName)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                if (result.result.contains(SUCCESS, true)) {
                    Result.success(true)
                } else {
                    Result.failure(Throwable(result.msg ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val json = Json { ignoreUnknownKeys = true }
                val errorResponse = json.decodeFromString<ActionsResponse>(errorBody ?: "")
                Result.failure(Throwable(errorResponse.msg ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun postMessage(streamName: String, topicName: String, content: String): Boolean {
        val res = zulipApiRepository.postMessage(streamName = streamName, topicName = topicName, content = content)
        return res.result.contains(SUCCESS, true)
    }

    override suspend fun addReaction(messageId: Int, emojiName: String): Boolean {
        return zulipApiRepository.addReaction(messageId, emojiName).result.contains(SUCCESS, true)
    }

    override suspend fun removeReaction(messageId: Int, emojiName: String): Boolean {
        return zulipApiRepository.removeReaction(messageId, emojiName).result.contains(SUCCESS, true)
    }

    companion object {
        const val SUCCESS = "success"
        const val MESSAGES_CACHE_SIZE = 50
        const val PAGE_SIZE = "20"
        const val ANCHOR_NEWEST = "newest"
        private const val ROOM_SOURCE = "room"
        private const val API_SOURCE = "api"
        private const val EMPTY_RETURN_MESSAGE = "empty return"
    }

}
