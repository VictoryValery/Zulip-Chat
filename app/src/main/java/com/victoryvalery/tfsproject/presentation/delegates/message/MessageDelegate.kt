package com.victoryvalery.tfsproject.presentation.delegates.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.databinding.MessageLayoutBinding
import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.presentation.delegates.AdapterDelegate
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem
import io.noties.markwon.Markwon

class MessageDelegate(
    private val onMessageLongClick: (id: Int, position: Int, myMessage: Boolean, hasContent: Boolean) -> Unit,
    private val onAddEmojiClick: (id: Int, position: Int) -> Unit,
    private val onEmojiClick: (emoji: String, id: Int, position: Int) -> Unit,
    private val markwon: Markwon
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(
            MessageLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onMessageLongClick,
            onAddEmojiClick,
            onEmojiClick,
            markwon
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int) {
        (holder as ViewHolder).bind(item.content() as MessageItem, position)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is MessageDelegateItem
    }

    class ViewHolder(
        private val binding: MessageLayoutBinding,
        private val onMessageLongClick: (id: Int, position: Int, myMessage: Boolean, hasContent: Boolean) -> Unit,
        private val onAddEmojiClick: (id: Int, position: Int) -> Unit,
        private val onEmojiClick: (emoji: String, id: Int, position: Int) -> Unit,
        private val markwon: Markwon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(messageItem: MessageItem, position: Int) {

            binding.message.apply {

                setMyMessage(messageItem.myMessage)
                markwon.setMarkdown(textMessage, messageItem.message)
                textName.text = messageItem.name
                if (messageItem.id == 0)
                    textMessage.alpha = 0.5f
                else
                    textMessage.alpha = 1f
                Glide.with(context)
                    .load(messageItem.picture)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.common_placeholder)
                    .into(avatar)

                flexBox.updateReactions(
                    messageItem,
                    position,
                    onEmojiClick,
                    onAddEmojiClick
                )

                textMessage.setOnLongClickListener {
                    onMessageLongClick(messageItem.id, position, messageItem.myMessage, messageItem.msgContent.isNotEmpty())
                    return@setOnLongClickListener true
                }
            }

        }
    }
}
