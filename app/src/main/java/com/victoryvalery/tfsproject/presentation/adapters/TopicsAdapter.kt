package com.victoryvalery.tfsproject.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.victoryvalery.tfsproject.databinding.TopicChoiceHolderBinding

class TopicsAdapter(
    private val onTopicClick: (topicName: String) -> Unit
) : ListAdapter<String, TopicsAdapter.TopicViewHolder>(AsyncDifferConfig.Builder(OperationDiffUtil()).build()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(
            TopicChoiceHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onTopicClick
        )
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = getItem(position)
        holder.bind(topic)
    }

    class TopicViewHolder(
        val binding: TopicChoiceHolderBinding,
        val onTopicClick: (topicName: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topicName: String) {
            binding.topicItem.text = topicName
            binding.root.setOnClickListener {
                onTopicClick(topicName)
            }
        }
    }

    class OperationDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
