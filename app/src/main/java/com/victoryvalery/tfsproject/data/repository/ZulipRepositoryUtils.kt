package com.victoryvalery.tfsproject.data.repository

import com.victoryvalery.tfsproject.data.apiStorage.API_SECONDS
import com.victoryvalery.tfsproject.data.apiStorage.models.Narrow
import com.victoryvalery.tfsproject.data.repository.ZulipRepositoryImpl.Companion.ANCHOR_NEWEST
import com.victoryvalery.tfsproject.data.repository.ZulipRepositoryImpl.Companion.PAGE_SIZE
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun getEventQueryMap(eventType: String, streamName: String, topicName: String?): Map<String, String> {

    val narrow = if (topicName != null)
        listOf(
            arrayOf("stream", streamName),
            arrayOf("topic", topicName)
        ) else
        listOf(
            arrayOf("stream", streamName)
        )

    val eventTypeList = eventType.split(",")
    return mapOf(
        "narrow" to Json.encodeToString(narrow),
        "event_types" to Json.encodeToString(eventTypeList)
    )
}

fun getQueryMapTopics(streamName: String): Map<String, String> {

    val narrow = listOf(
        Narrow("stream", streamName)
    )

    return mapOf(
        "anchor" to "newest",
        "num_before" to "2000",
        "num_after" to "0",
        "narrow" to Json.encodeToString(narrow),
        "apply_markdown" to "false"
    )
}

fun getQueryMapMessages(streamName: String, topicName: String?, anchor: String): Map<String, String> {

    val narrow = if (topicName != null)
        listOf(
            Narrow("stream", streamName),
            Narrow("topic", topicName),
        ) else
        listOf(
            Narrow("stream", streamName)
        )

    return mapOf(
        "include_anchor" to "false",
        "anchor" to anchor.ifEmpty { ANCHOR_NEWEST },
        "num_before" to PAGE_SIZE,
        "num_after" to "0",
        "narrow" to Json.encodeToString(narrow),
        "apply_markdown" to "false"
    )
}

fun parsePresenceJsonToMap(jsonObject: JsonObject): Map<Int, String> {
    val presencesJson = jsonObject["presences"] as JsonObject

    return presencesJson.entries.associate { (fullName, listActivities) ->
        val userId = fullName.substringAfter("user").substringBefore("@").toInt()
        val aggregated = listActivities.jsonObject["aggregated"]?.jsonObject
        val timestamp = aggregated?.get("timestamp")?.jsonPrimitive?.content?.toLong()

        val status = when (val webStatus = aggregated?.get("status")?.jsonPrimitive?.content ?: "offline") {
            "idle", "active" -> calculateStatus(webStatus, timestamp)
            else -> "offline"
        }

        userId to status
    }
}

fun calculateStatus(webStatus: String, timestamp: Long?): String {
    val expirationTime = if (webStatus == "idle") 600 else 180
    return if (timestamp != null && ((System.currentTimeMillis() / API_SECONDS) - timestamp <= expirationTime)) {
        webStatus
    } else {
        "offline"
    }
}

