package com.victoryvalery.tfsproject.domain.models

data class TopicItem(
    val id: Int,
    val parentStreamId: Int,
    val parentStreamName: String,
    val maxId: Int,
    val name: String,
    var msg: Int = 0
)
