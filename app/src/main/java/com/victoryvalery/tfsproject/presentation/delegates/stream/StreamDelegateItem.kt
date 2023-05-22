package com.victoryvalery.tfsproject.presentation.delegates.stream

import com.victoryvalery.tfsproject.domain.models.StreamItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.topic.TopicDelegateItem

class StreamDelegateItem(
    private val streamItem: StreamItem
) : DelegateItem {
    override fun content(): Any = streamItem

    override fun id(): Int = streamItem.streamId

    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as StreamDelegateItem).streamItem == this.streamItem
    }
}

fun List<StreamItem>.toDelegateItemListWithTopics(): List<DelegateItem> {
    return map { streamItem ->
        listOf(StreamDelegateItem(streamItem)) +
                if (streamItem.isClicked) {
                    streamItem.topics.map { TopicDelegateItem(it) }
                } else {
                    emptyList()
                }
    }.flatten()
}
