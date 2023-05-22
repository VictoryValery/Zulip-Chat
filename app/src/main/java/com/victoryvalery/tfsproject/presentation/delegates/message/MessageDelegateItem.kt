package com.victoryvalery.tfsproject.presentation.delegates.message

import com.victoryvalery.tfsproject.domain.models.DateItem
import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.domain.models.TopicSeparatorItem
import com.victoryvalery.tfsproject.getMonthName
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.date.DateDelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.topicSeparator.TopicSeparatorDelegateItem

class MessageDelegateItem(
    private val messageItem: MessageItem
) : DelegateItem {
    override fun content(): Any = messageItem
    override fun id(): Int = messageItem.id
    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as MessageDelegateItem).messageItem == this.messageItem
    }
}

fun List<MessageItem>.toDelegateItemListWithDateSeparator(topicChat: Boolean): List<DelegateItem> {
    var previousTopic: String? = null
    return this
        .sortedWith(compareBy({ it.month }, { it.date }))
        .groupBy { Pair(it.month, it.date) }
        .flatMap { (monthDatePair, messages) ->
            val dateItem = DateItem(monthDatePair.second.toString(), getMonthName(monthDatePair.first))
            val delegateItems = mutableListOf<DelegateItem>(DateDelegateItem(dateItem))
            for (message in messages) {
                if (!topicChat && previousTopic != message.topic) {
                    val topicSeparatorItem = TopicSeparatorItem(message.topic, dateItem)
                    delegateItems.add(TopicSeparatorDelegateItem(topicSeparatorItem))
                    previousTopic = message.topic
                }
                delegateItems.add(MessageDelegateItem(message))
            }
            delegateItems
        }
}
