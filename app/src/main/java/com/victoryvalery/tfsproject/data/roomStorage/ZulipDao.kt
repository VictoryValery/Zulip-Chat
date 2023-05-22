package com.victoryvalery.tfsproject.data.roomStorage

import androidx.room.*
import com.victoryvalery.tfsproject.data.roomStorage.models.MessageEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.StreamEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.TopicEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.UserEntity

@Dao
interface ZulipDao {

    //Messages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageList(messageEntities: List<MessageEntity>)

    @Query("SELECT * FROM messages WHERE streamName = :streamName AND topicName = :topicName")
    suspend fun getLastChatMessages(streamName: String, topicName: String): List<MessageEntity>
    @Query("SELECT * FROM messages WHERE streamName = :streamName")
    suspend fun getLastChatMessages(streamName: String): List<MessageEntity>

    @Query("DELETE FROM messages WHERE streamName = :streamName")
    suspend fun deleteLastChatMessages(streamName: String)

    @Query("DELETE FROM messages WHERE streamName = :streamName AND topicName = :topicName")
    suspend fun deleteLastChatMessages(streamName: String, topicName: String)

    //Topic
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicList(topicEntities: List<TopicEntity>)

    @Query("DELETE FROM topics WHERE parentStreamId = :parentStreamId")
    suspend fun deleteStreamTopics(parentStreamId: Int)

    @Query("SELECT * FROM topics WHERE parentStreamId = :parentStreamId")
    suspend fun getStreamTopics(parentStreamId: Int): List<TopicEntity>

    //Streams
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreamsList(streamEntities: List<StreamEntity>)

    @Query("SELECT * FROM streams WHERE isSubscribed = 0")
    suspend fun getAllStreams(): List<StreamEntity>

    @Query("SELECT * FROM streams WHERE isSubscribed = 1")
    suspend fun getSubscribedStreams(): List<StreamEntity>

    @Query("DELETE FROM streams WHERE isSubscribed = 1")
    suspend fun deleteSubscribedStreams()

    @Query("DELETE FROM streams WHERE isSubscribed = 0")
    suspend fun deleteAllStreams()

    //Users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsersList(usersEntities: List<UserEntity>)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

}
