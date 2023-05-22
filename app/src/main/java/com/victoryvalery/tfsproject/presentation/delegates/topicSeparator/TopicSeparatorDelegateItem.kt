package com.victoryvalery.tfsproject.presentation.delegates.topicSeparator

import com.victoryvalery.tfsproject.domain.models.TopicSeparatorItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem

data class TopicSeparatorDelegateItem(
    val value: TopicSeparatorItem
) : DelegateItem {
    override fun content(): Any = value
    override fun id(): Int = value.hashCode()
    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as TopicSeparatorDelegateItem).value == content()
    }
}
