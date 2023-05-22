package com.victoryvalery.tfsproject.presentation.delegates.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.databinding.TopicHolderBinding
import com.victoryvalery.tfsproject.domain.models.TopicItem
import com.victoryvalery.tfsproject.presentation.delegates.AdapterDelegate
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem

class TopicDelegate(
    private val onTopicClick: (parentStreamName: String, parentStreamId: Int, name: String) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return TopicViewHolder(
            TopicHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onTopicClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: DelegateItem, position: Int) {
        (holder as TopicViewHolder).bind(item.content() as TopicItem)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is TopicDelegateItem
    }

    inner class TopicViewHolder(
        private val binding: TopicHolderBinding,
        private val onTopicClick: (parentStreamName: String, parentStreamId: Int, name: String) -> Unit
    ) : ViewHolder(binding.root) {

        fun bind(topicItem: TopicItem) {
            binding.root.setBackgroundColor(
                when (topicItem.id % 2) {
                    0 -> ContextCompat.getColor(binding.root.context, R.color.green_main)
                    else -> ContextCompat.getColor(binding.root.context, R.color.mustard_main)
                }
            )
            binding.topicTitle.text = topicItem.name
            binding.topicMsgAmount.text = binding.root.context.getString(R.string.topic_msg, topicItem.msg.toString())
            binding.root.setOnClickListener {
                onTopicClick(topicItem.parentStreamName, topicItem.parentStreamId, topicItem.name)
            }
        }
    }
}
