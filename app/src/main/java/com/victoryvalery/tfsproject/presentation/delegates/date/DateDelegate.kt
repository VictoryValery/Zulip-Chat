package com.victoryvalery.tfsproject.presentation.delegates.date

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoryvalery.tfsproject.databinding.DateSeparatorBinding
import com.victoryvalery.tfsproject.domain.models.DateItem
import com.victoryvalery.tfsproject.presentation.delegates.AdapterDelegate
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem

class DateDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(
            DateSeparatorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int) {
        (holder as ViewHolder).bind(item.content() as DateItem)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is DateDelegateItem
    }

    class ViewHolder(private val binding: DateSeparatorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dateItem: DateItem) {
            binding.dateSeparator.text = dateItem.toString()
        }
    }

}
