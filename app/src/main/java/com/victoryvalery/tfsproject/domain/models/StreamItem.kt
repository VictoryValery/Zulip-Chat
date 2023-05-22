package com.victoryvalery.tfsproject.domain.models

data class StreamItem(
    val streamId: Int,
    val description: String,
    val name: String,
    val topics : List<TopicItem> = emptyList(),
    val isClicked: Boolean = false
)
