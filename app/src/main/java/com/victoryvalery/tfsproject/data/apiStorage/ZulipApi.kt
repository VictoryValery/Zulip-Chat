package com.victoryvalery.tfsproject.data.apiStorage

import com.victoryvalery.tfsproject.data.apiStorage.models.*
import kotlinx.serialization.json.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ZulipApi {

    @POST(GET_SUBSCRIBED_STREAMS)
    suspend fun addStream(@QueryMap subscriptions: Map<String, String>): ActionsResponse

    @Multipart
    @POST(SEND_FILE)
    suspend fun sendFile(@Part file: MultipartBody.Part): ActionsResponse

    @POST(REGISTER_EVENT)
    suspend fun registerEvent(@QueryMap queryMap: Map<String, String>): RegisteredEvent

    @GET(EVENT)
    suspend fun getMessageEvent(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastEventId: Int = -1
    ): MessageEventRoot

    @GET(EVENT)
    suspend fun getEmojiEvent(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastEventId: Int = -1
    ): EmojiEventRoot

    @DELETE(EVENT)
    suspend fun deleteEvent(@Query("queue_id") queueId: String)

    @GET(GET_ME_USER)
    suspend fun getMeUser(): MeUser

    @GET(GET_ALL_USERS)
    suspend fun getAllUsers(): UsersRoot

    @GET(GET_USER_PRESENCE)
    suspend fun getUserPresence(@Path("user_id") userId: Int): PresenceRoot

    @POST(POST_ME_PRESENCE)
    suspend fun postMePresence(
        @Query("status") status: String = "active",
        @Query("client") client: String = "mobile client",
        @Query("new_user_input_timestamp") timestamp: Long
    ): SetMePresenceResponse

    @GET(GET_ALL_USERS_PRESENCE)
    suspend fun getALLUsersPresence(): JsonObject

    @GET(GET_STREAMS)
    suspend fun getAllStreams(): StreamsRoot

    @GET(GET_SUBSCRIBED_STREAMS)
    suspend fun getSubscribedStreams(): SubscriptionsRoot

    @GET(GET_STREAM_TOPICS)
    suspend fun getStreamTopics(
        @Path("stream_id") streamId: Int
    ): TopicsRoot

    @GET(GET_MESSAGES)
    suspend fun getMessages(@QueryMap queryMap: Map<String, String>): MessagesRoot

    @GET(SINGLE_MESSAGE)
    suspend fun getSingleMessage(
        @Path("message_id") messageId: Int,
        @Query("apply_markdown") markdown: String = "false"
    ): SingleMessage

    @DELETE(SINGLE_MESSAGE)
    suspend fun deleteSingleMessage(
        @Path("message_id") messageId: Int
    ): ActionsResponse

    @PATCH(SINGLE_MESSAGE)
    suspend fun editSingleMessage(
        @Path("message_id") messageId: Int,
        @Query("content") content: String,
    ): Response<ActionsResponse>

    @PATCH(SINGLE_MESSAGE)
    suspend fun moveMessage(
        @Path("message_id") messageId: Int,
        @Query("topic") content: String,
    ): Response<ActionsResponse>

    @POST(POST_MESSAGES)
    suspend fun postMessage(
        @Query("type") type: String = "stream",
        @Query("to") streamName: String,
        @Query("topic") topicName: String,
        @Query("content") content: String,
    ): ActionsResponse

    @POST(PATH_REACTIONS)
    suspend fun addReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): ActionsResponse

    @DELETE(PATH_REACTIONS)
    suspend fun removeReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): ActionsResponse

}
