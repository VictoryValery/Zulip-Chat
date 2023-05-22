package com.victoryvalery.tfsproject.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.databinding.UserHolderBinding
import com.victoryvalery.tfsproject.domain.models.Status
import com.victoryvalery.tfsproject.domain.models.UserItem

class UsersAdapter(
    private val onUserClick: (user: UserItem) -> Unit
) : ListAdapter<UserItem, UsersAdapter.UsersViewHolder>(
    AsyncDifferConfig.Builder(UsersOperationDiffUtil()).build()
), Filterable {

    private var generalList: List<UserItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            UserHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onUserClick
        )
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class UsersViewHolder(
        private val binding: UserHolderBinding,
        private val onUserClick: (user: UserItem) -> Unit
    ) : ViewHolder(binding.root) {

        fun bind(user: UserItem) {
            binding.apply {
                userEmail.text = user.email
                userName.text = user.fullName
                userWebstatus.apply {
                    isVisible = user.status !is Status.Offline
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.root.context,
                            when (user.status) {
                                is Status.Idle -> R.drawable.ic_circle_webstatus_idle
                                is Status.Active -> R.drawable.ic_circle_webstatus
                                else -> R.drawable.ic_circle_webstatus_offlne
                            }
                        )
                    )
                }
                Glide.with(binding.root)
                    .load(user.avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.prototype)
                    .into(userAvatarImage)
            }

            binding.root.setOnClickListener {
                onUserClick(user)
            }
        }
    }

    private class UsersOperationDiffUtil : DiffUtil.ItemCallback<UserItem>() {
        override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint.toString()
                val filterResults = FilterResults()
                val filteredUserList =
                    if (queryString.isEmpty()) {
                        generalList.toMutableList()
                    } else {
                        val filteredList = mutableListOf<UserItem>()
                        generalList.forEach { user ->
                            if (user.fullName.contains(queryString, ignoreCase = true) ||
                                user.email.contains(queryString, ignoreCase = true)
                            ) {
                                filteredList.add(user)
                            }
                        }
                        filteredList
                    }

                filterResults.values = filteredUserList.toList()
                filterResults.count = filteredUserList.size
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                val fullList = generalList
                val filteredList = (results.values as? List<UserItem>) ?: emptyList()
                submitList(filteredList)
                generalList = fullList
            }
        }
    }

    override fun submitList(list: List<UserItem>?) {
        super.submitList(list)
        generalList = list ?: listOf()
    }

}
