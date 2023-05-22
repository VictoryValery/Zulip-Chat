package com.victoryvalery.tfsproject.presentation.delegates.topicSeparator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoryvalery.tfsproject.databinding.TopicSeparatorBinding
import com.victoryvalery.tfsproject.domain.models.TopicSeparatorItem
import com.victoryvalery.tfsproject.presentation.delegates.AdapterDelegate
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem

class TopicSeparatorDelegate(
    private val onTopicClick: (topicName: String) -> Unit
) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(
            TopicSeparatorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onTopicClick
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int) {
        (holder as ViewHolder).bind(item.content() as TopicSeparatorItem)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is TopicSeparatorDelegateItem
    }

    class ViewHolder(
        private val binding: TopicSeparatorBinding,
        private val onTopicClick: (topicName: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentTopic: String? = null

        fun bind(topicSeparatorItem: TopicSeparatorItem) {
            with(binding) {
                topicSeparator.text = topicSeparatorItem.topic
                topicDateSeparator.text = topicSeparatorItem.dateItem.toString()
                root.setOnClickListener {
                    onTopicClick(topicSeparatorItem.topic)
                }
            }
            currentTopic = topicSeparatorItem.topic
        }

        fun getTopicName(): String? = currentTopic
    }
}
