package com.victoryvalery.tfsproject.data.roomStorage.mappers

import com.victoryvalery.tfsproject.data.apiStorage.ME_USER_ID_CONST
import com.victoryvalery.tfsproject.data.apiStorage.mappers.extractUrl
import com.victoryvalery.tfsproject.data.apiStorage.mappers.parseMessageContentString
import com.victoryvalery.tfsproject.data.apiStorage.mappers.toReactionItem
import com.victoryvalery.tfsproject.data.apiStorage.models.Message
import com.victoryvalery.tfsproject.data.apiStorage.models.Stream
import com.victoryvalery.tfsproject.data.roomStorage.models.MessageEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.StreamEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.TopicEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.UserEntity
import com.victoryvalery.tfsproject.domain.models.*
import com.victoryvalery.tfsproject.getDayOfMonthFromTimestamp
import com.victoryvalery.tfsproject.getMonthFromTimestamp

fun StreamEntity.toStreamItem(): StreamItem {
    return StreamItem(
        streamId = streamId,
        description = description,
        name = name
    )
}

fun Stream.toStreamsEntity(isSubscribed: Boolean): StreamEntity {
    return StreamEntity(
        streamId = streamId,
        description = description,
        name = name,
        isSubscribed = isSubscribed
    )
}

fun TopicEntity.toTopicItem(): TopicItem {
    return TopicItem(
        id = id,
        parentStreamId = parentStreamId,
        parentStreamName = parentStreamName,
        maxId = maxId,
        name = name,
        msg = msg
    )
}

fun TopicItem.toTopicEntity(): TopicEntity {
    return TopicEntity(
        id = id,
        parentStreamId = parentStreamId,
        parentStreamName = parentStreamName,
        maxId = maxId,
        name = name,
        msg = msg
    )
}

fun Message.toMessageEntity(streamName: String, topicName: String): MessageEntity {
    return MessageEntity(
        id = id,
        streamName = streamName,
        topicName = topicName,
        senderId = senderId,
        senderFullName = senderFullName,
        avatarUrl = avatarUrl,
        content = content,
        timestamp = timestamp,
        isMeMessage = isMeMessage,
        msgContent = "",
        subject = subject,
        reactions = reactions
    )
}

fun MessageEntity.toMessageItem(): MessageItem {
    return MessageItem(
        id = id,
        userId = senderId,
        name = senderFullName,
        message = parseMessageContentString(content),
        picture = avatarUrl,
        date = timestamp.getDayOfMonthFromTimestamp(),
        month = timestamp.getMonthFromTimestamp(),
        topic = subject,
        timestamp = timestamp,
        isMeMessage = false,
        msgContent = extractUrl(parseMessageContentString(content)),
        myMessage = senderId == ME_USER_ID_CONST,
        listReactions = reactions.map { it.toReactionItem() }.toMutableList()
    )
}

fun MessageItem.toMessageEntity(streamName: String, topicName: String): MessageEntity {
    return MessageEntity(
        id = id,
        streamName = streamName,
        topicName = topicName,
        senderId = userId,
        senderFullName = name,
        avatarUrl = picture,
        content = message,
        timestamp = timestamp,
        isMeMessage = isMeMessage,
        msgContent = msgContent,
        subject = topicName,
        reactions = emptyList()
    )
}

fun UserEntity.toUserItem(): UserItem {
    return UserItem(
        userId = userId,
        fullName = fullName,
        email = email,
        avatarUrl = avatarUrl,
        isMeUser = isMeUser,
        status = Status.Offline,
        isActive = false
    )
}

fun UserItem.toUserEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        fullName = fullName,
        email = email,
        avatarUrl = avatarUrl,
        isMeUser = isMeUser
    )
}