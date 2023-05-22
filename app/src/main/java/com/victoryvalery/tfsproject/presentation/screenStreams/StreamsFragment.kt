package com.victoryvalery.tfsproject.presentation.screenStreams

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.github.terrakok.cicerone.Router
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.victoryvalery.tfsproject.App
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.closeKeyboard
import com.victoryvalery.tfsproject.databinding.AddStreamDialogBinding
import com.victoryvalery.tfsproject.databinding.FragmentStreamsBinding
import com.victoryvalery.tfsproject.di.streamsComponent.DaggerStreamsDependentComponent
import com.victoryvalery.tfsproject.presentation.cicerone.Screens.Messages
import com.victoryvalery.tfsproject.presentation.delegates.MessagesAdapter
import com.victoryvalery.tfsproject.presentation.delegates.stream.StreamDelegate
import com.victoryvalery.tfsproject.presentation.delegates.topic.TopicDelegate
import com.victoryvalery.tfsproject.presentation.screenMainActivity.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.storepersisting.retainStoreHolder
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StreamsFragment : ElmFragment<Event, Effect, State>() {

    private var _binding: FragmentStreamsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var storeFactory: StoreFactory

    private lateinit var streamsAdapter: MessagesAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val appComponent = App.INSTANCE.appComponent
        val streamsComponent = DaggerStreamsDependentComponent.builder().appComponent(appComponent).build()
        streamsComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStreamsAdapter()
        setTabs()

        (requireActivity() as MainActivity).setNavigationViewVisibility(true)

        binding.searchIc.setOnClickListener {
            if (store.currentState.isSearching) {
                store.accept(Event.Ui.Search(EMPTY_STRING))
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
                store.accept(Event.Ui.Search(query.toString()))
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                store.accept(Event.Ui.Search(query.toString()))
                return true
            }
        })

        binding.streamsFabAdd.setOnClickListener {
            showStreamAddDialog()
        }
    }

    private fun showStreamAddDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        val dialogBinding = AddStreamDialogBinding.inflate(layoutInflater)

        with(builder) {
            setTitle(getString(R.string.add_new_stream_dialog_title))
            setPositiveButton(getString(R.string.add_positive_button_text), null)
            setNegativeButton(getString(R.string.cancel_negative_button_text)) { _, _ -> }
            setView(dialogBinding.root)
        }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            val addButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            addButton.setOnClickListener {
                val newStreamName = dialogBinding.dialogStreamName.text.toString()
                if (newStreamName.isNotEmpty()) {
                    store.accept(
                        Event.Ui.AddStream(
                            newStreamName,
                            dialogBinding.dialogStreamDescription.text.toString()
                        )
                    )
                    alertDialog.dismiss()
                } else {
                    Snackbar.make(dialogBinding.root, getString(R.string.input_stream_name_snackbar_text), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        alertDialog.show()
    }

    override val initEvent: Event = Event.Ui.Initial

    private val retainStore = retainStoreHolder {
        storeFactory.provide()
    }

    override val storeHolder: StoreHolder<Event, Effect, State>
        get() = retainStore.value

    override fun render(state: State) {
        with(binding) {
            progressBar.isVisible = state.isLoading
            streamsShimmer.isVisible = state.isInitializing
            recycleStreams.isVisible = !state.isInitializing
            if (state.isError) {
                progressBar.isVisible = false
                streamsShimmer.isVisible = false
                recycleStreams.isVisible = false
            }
        }

        state.streamsInfo.apply {
            val streamsLoaded = allStreamsDelegate.isNotEmpty() && subscribedStreamsDelegate.isNotEmpty()
            binding.streamsShimmer.isVisible = !streamsLoaded
            binding.recycleStreams.isVisible = streamsLoaded
            if (streamsLoaded) {
                binding.streamsTabLayout.selectTab(binding.streamsTabLayout.getTabAt(activeTabIndex))
                val delegates =
                    if (state.searchResult != null && state.searchText != EMPTY_STRING)
                        state.searchResult
                    else
                        if (activeTabIndex == TAB_ALL_INDEX)
                            allStreamsDelegate
                        else
                            subscribedStreamsDelegate
                streamsAdapter.submitList(delegates)
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
            is Effect.NavigateToMessages -> {
                router.navigateTo(Messages(effect.streamName, effect.streamId, effect.isSubscribed, effect.topicName))
            }
            is Effect.NavigateToGeneralMessages -> {
                router.navigateTo(Messages(effect.streamName, effect.streamId, effect.isSubscribed))
            }
        }
    }

    private fun setStreamsAdapter() {
        streamsAdapter = MessagesAdapter()
        streamsAdapter.apply {
            addDelegate(
                StreamDelegate(
                    { streamId -> onStreamClick(streamId) },
                    { streamName, streamId -> openStreamChatClick(streamName, streamId) })
            )
            addDelegate(TopicDelegate
            { parentStreamName, parentStreamId, topicName -> onTopicClick(parentStreamName, parentStreamId, topicName) }
            )
        }
        binding.recycleStreams.adapter = streamsAdapter
    }

    private fun onStreamClick(streamId: Int) {
        store.accept(Event.Ui.GetStreamTopics(streamId))
    }

    private fun openStreamChatClick(streamName: String, streamId: Int) {
        closeKeyboard(requireContext(), binding.searchView.windowToken)
        store.accept(Event.Ui.OpenGeneralChat(streamName, streamId))
    }

    private fun onTopicClick(parentStreamName: String, parentStreamId: Int, topicName: String) {
        closeKeyboard(requireContext(), binding.searchView.windowToken)
        store.accept(Event.Ui.TopicClick(parentStreamName, parentStreamId, topicName))
    }

    private fun setTabs() {

        binding.streamsTabLayout.addTab(binding.streamsTabLayout.newTab().setText(getString(R.string.subscribed_tab)))
        binding.streamsTabLayout.addTab(binding.streamsTabLayout.newTab().setText(getString(R.string.all_streams_tab)))

        binding.streamsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                when (tab?.position) {
                    TAB_SUBSCRIBED_INDEX -> changeTab(TAB_SUBSCRIBED_INDEX)
                    TAB_ALL_INDEX -> changeTab(TAB_ALL_INDEX)
                    else -> return
                }
            }

            override fun onTabUnselected(tab: Tab?) = Unit
            override fun onTabReselected(tab: Tab?) = Unit
        })
    }

    private fun changeTab(tabIndex: Int) {
        if (store.currentState.searchText != null)
            store.accept(Event.Ui.Search(EMPTY_STRING))
        if (store.currentState.isSearching)
            store.accept(Event.Ui.SearchClick(false))
        store.accept(Event.Ui.SetActiveTab(tabIndex))
        store.accept(Event.Ui.UpdateStreamsTopics)
    }

    override fun onDestroyView() {
        binding.recycleStreams.adapter = null
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setStatusBarColor(R.color.background_toolbar_grey)
        store.accept(Event.Ui.UpdateStreamsTopics)
        if (store.currentState.streamsInfo.activeTabIndex == TAB_ALL_INDEX)
            store.accept(Event.Ui.UpdateSubscribedStreams)
    }

    companion object {
        const val TAB_ALL_INDEX = 1
        const val TAB_SUBSCRIBED_INDEX = 0
        const val EMPTY_STRING = ""
    }
}
