package com.victoryvalery.tfsproject.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.victoryvalery.tfsproject.databinding.EmojiItemHolderBinding
import com.victoryvalery.tfsproject.domain.models.ReactionItem

class EmojiAdapter(
    private val onEmojiClick: (reaction: ReactionItem) -> Unit
) : ListAdapter<ReactionItem, EmojiAdapter.EmojiViewHolder>(AsyncDifferConfig.Builder(OperationDiffUtil()).build()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        return EmojiViewHolder(
            EmojiItemHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onEmojiClick
        )
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        val emoji = getItem(position)
        holder.bind(emoji)
    }

    class EmojiViewHolder(
        val binding: EmojiItemHolderBinding,
        val onEmojiClick: (reaction: ReactionItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reaction: ReactionItem) {
            binding.emojiItemCode.text = reaction.emoji
            binding.root.setOnClickListener {
                onEmojiClick(reaction)
            }
        }
    }

    class OperationDiffUtil : DiffUtil.ItemCallback<ReactionItem>() {
        override fun areItemsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean {
            return oldItem.emoji == newItem.emoji
        }

        override fun areContentsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean {
            return oldItem == newItem
        }
    }

}
