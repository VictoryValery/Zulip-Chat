package com.victoryvalery.tfsproject.presentation.screenUsers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.github.terrakok.cicerone.Router
import com.victoryvalery.tfsproject.App
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.closeKeyboard
import com.victoryvalery.tfsproject.databinding.FragmentUsersBinding
import com.victoryvalery.tfsproject.di.usersComponent.DaggerUsersDependentComponent
import com.victoryvalery.tfsproject.domain.models.UserItem
import com.victoryvalery.tfsproject.presentation.adapters.UsersAdapter
import com.victoryvalery.tfsproject.presentation.cicerone.Screens.AnyUser
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.storepersisting.retainStoreHolder
import javax.inject.Inject

class UsersFragment : ElmFragment<Event, Effect, State>() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val usersAdapter = UsersAdapter { onUserClick(it) }

    @Inject
    lateinit var storeFactory: StoreFactory

    @Inject
    lateinit var router: Router

    override val initEvent: Event = Event.Ui.GetUsers

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val appComponent = App.INSTANCE.appComponent
        val usersComponent = DaggerUsersDependentComponent.builder().appComponent(appComponent).build()
        usersComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerUsers.adapter = usersAdapter

        binding.searchIc.setOnClickListener {
            if (store.currentState.isSearching) {
                store.accept(Event.Ui.SearchTextChange(EMPTY_STRING))
            }
            store.accept(Event.Ui.SearchClick(!store.currentState.isSearching))
        }

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus && store.currentState.searchText == null && !store.currentState.isSearching) {
                store.accept(Event.Ui.SearchClick(!store.currentState.isSearching))
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                store.accept(Event.Ui.SearchTextChange(query))
                usersAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    store.accept(Event.Ui.SearchTextChange(query))
                    usersAdapter.filter.filter(query)
                }
                return true
            }
        })
    }

    private val retainStore = retainStoreHolder {
        storeFactory.provide()
    }

    override val storeHolder: StoreHolder<Event, Effect, State>
        get() = retainStore.value

    override fun render(state: State) {
        with(binding) {
            usersShimmer.isVisible = state.isLoading && !state.isError
            recyclerUsers.isVisible = !state.isLoading && !state.isError
            usersAdapter.submitList(state.usersInfo)
            recyclerUsers.post {
                recyclerUsers.smoothScrollToPosition(0)
            }
        }

        binding.searchIc.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                if (state.isSearching) R.drawable.ic_search_clear else R.drawable.ic_search
            )
        )

        if (!state.isSearching) {
            binding.searchView.setQuery(EMPTY_STRING, false)
            binding.searchView.clearFocus()
            if (binding.searchView.windowToken != null)
                closeKeyboard(requireContext(), binding.searchView.windowToken)
        } else {
            binding.searchView.isIconified = !state.isSearching
            binding.searchView.setQuery(state.searchText, false)
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.ShowError -> {
                if (effect.errorMessage.contains(getString(R.string.Internet_error), true))
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.check_internet_connection), Toast.LENGTH_SHORT
                    ).show()
                else
                    Toast.makeText(requireContext(), effect.errorMessage, Toast.LENGTH_SHORT).show()
            }
            is Effect.NavigateToUser -> {
                router.navigateTo(AnyUser(effect.user))
            }
        }
    }

    private fun onUserClick(user: UserItem) {
        closeKeyboard(requireContext(), binding.searchView.windowToken)
        binding.searchView.setQuery(EMPTY_STRING, false)
        store.accept(Event.Ui.OnUserClick(user))
    }

    override fun onDestroyView() {
        binding.recyclerUsers.adapter = null
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EMPTY_STRING = ""
    }
}
