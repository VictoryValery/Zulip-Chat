package com.victoryvalery.tfsproject.presentation.delegates.stream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.databinding.StreamHolderBinding
import com.victoryvalery.tfsproject.domain.models.StreamItem
import com.victoryvalery.tfsproject.presentation.delegates.AdapterDelegate
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem

class StreamDelegate(
    private val onStreamClick: (id: Int) -> Unit,
    private val openStreamChatClick: (streamName: String, streamId: Int) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return StreamViewHolder(
            StreamHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onStreamClick,
            openStreamChatClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: DelegateItem, position: Int) {
        (holder as StreamViewHolder).bind(item.content() as StreamItem)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is StreamDelegateItem
    }

    inner class StreamViewHolder(
        private val binding: StreamHolderBinding,
        private val onStreamClick: (id: Int) -> Unit,
        private val openStreamChatClick: (streamName: String, streamId: Int) -> Unit
    ) : ViewHolder(binding.root) {

        fun bind(streamItem: StreamItem) {
            binding.streamTitle.text = binding.root.context.getString(R.string.topic_name_text, streamItem.name)
            binding.streamTopics.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    if (streamItem.isClicked) R.drawable.ic_vector_up else R.drawable.ic_vector
                )
            )

            binding.streamTitle.setOnClickListener {
                openStreamChatClick(streamItem.name, streamItem.streamId)
            }

            binding.streamTopics.setOnClickListener {
                onStreamClick(streamItem.streamId)
            }
        }

    }

}
