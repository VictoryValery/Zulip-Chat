package com.victoryvalery.tfsproject.presentation.delegates.topic

import com.victoryvalery.tfsproject.domain.models.TopicItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem

class TopicDelegateItem(
    private val topicItem: TopicItem
) : DelegateItem {
    override fun content(): Any = topicItem

    override fun id(): Int = topicItem.hashCode()

    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as TopicDelegateItem).topicItem.hashCode() == this.topicItem.hashCode()
    }

}
