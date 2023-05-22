package com.victoryvalery.tfsproject.presentation.screenMessages

import android.app.*
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.terrakok.cicerone.Router
import com.google.android.material.snackbar.Snackbar
import com.victoryvalery.tfsproject.App
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.closeKeyboard
import com.victoryvalery.tfsproject.data.apiStorage.API_DELAY
import com.victoryvalery.tfsproject.data.repository.ZulipRepositoryImpl.Companion.ANCHOR_NEWEST
import com.victoryvalery.tfsproject.databinding.FragmentMessagesBinding
import com.victoryvalery.tfsproject.databinding.MessageEditDialogBinding
import com.victoryvalery.tfsproject.databinding.TopicChoiceDialogBinding
import com.victoryvalery.tfsproject.di.messagesComponent.DaggerMessagesDependentComponent
import com.victoryvalery.tfsproject.domain.models.MessageItem
import com.victoryvalery.tfsproject.presentation.adapters.TopicsAdapter
import com.victoryvalery.tfsproject.presentation.cicerone.Screens
import com.victoryvalery.tfsproject.presentation.delegates.MessagesAdapter
import com.victoryvalery.tfsproject.presentation.delegates.StickyHeaderDecoration
import com.victoryvalery.tfsproject.presentation.delegates.date.DateDelegate
import com.victoryvalery.tfsproject.presentation.delegates.message.MessageDelegate
import com.victoryvalery.tfsproject.presentation.delegates.message.MessageDelegateItem
import com.victoryvalery.tfsproject.presentation.delegates.topicSeparator.TopicSeparatorDelegate
import com.victoryvalery.tfsproject.presentation.screenMainActivity.MainActivity
import io.noties.markwon.Markwon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.storepersisting.retainStoreHolder
import java.io.InputStream
import javax.inject.Inject

class MessagesFragment : ElmFragment<Event, Effect, State>() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var storeFactory: StoreFactory

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var markwon: Markwon

    private lateinit var messagesAdapter: MessagesAdapter
    private val anchorFlow = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var topicAlertDialog: AlertDialog
    private var showBadge = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val appComponent = App.INSTANCE.appComponent
        val messagesComponent = DaggerMessagesDependentComponent.builder().appComponent(appComponent).build()
        messagesComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        setFragmentResultListener(EMOJI_REQUEST) { _, bundle ->
            bundle.getString(EMOJI_NAME)?.let { selectedEmoji ->
                onEmojiCLick(selectedEmoji, bundle.getInt(MESSAGE_ID), bundle.getInt(POSITION))
            }
        }
        setFragmentResultListener(ACTION_REQUEST) { _, bundle ->
            val selectedAction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                bundle.getParcelable(ACTION_NAME, MessagesActions::class.java)
            else
                bundle.getParcelable(ACTION_NAME)
            selectedAction?.let { action ->
                onActionClick(action, bundle.getInt(MESSAGE_ID), bundle.getInt(POSITION))
            }
        }
        return binding.root
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            store.accept(Event.Ui.ExitClick)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun onActionClick(action: MessagesActions, messageId: Int, position: Int) {
        val messageItem = messagesAdapter.currentList.filterIsInstance<MessageDelegateItem>()
            .map { it.content() as MessageItem }
            .find { it.id == messageId }
        if (messageItem != null) {
            when (action) {
                MessagesActions.AddReaction -> openBottomSheetEmojiDialog(messageId, position)
                MessagesActions.CopyToClipboard -> {
                    val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val myClip: ClipData = ClipData.newPlainText(getString(R.string.label), messageItem.message)
                    clipboard.setPrimaryClip(myClip)
                    Toast.makeText(context, getString(R.string.message_copied), Toast.LENGTH_SHORT).show()
                }
                MessagesActions.DeleteMessage -> store.accept(Event.Ui.DeleteMessage(messageId))
                MessagesActions.EditMessage -> openEditMessageWindow(messageId, messageItem.message)
                MessagesActions.MoveMessage -> {
                    val topics = store.currentState.topicsList.filter { it != messageItem.topic }
                    showTopicChoiceDialog(topics, messageId)
                }
                MessagesActions.DownloadMessageContent -> store.accept(Event.Ui.DownloadFile(messageItem.msgContent))
            }
        }
    }

    private fun openEditMessageWindow(messageId: Int, message: String) {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        val dialogBinding = MessageEditDialogBinding.inflate(layoutInflater)
        with(builder) {
            setTitle(getString(R.string.edit_message_window_title))
            setView(dialogBinding.root)
            dialogBinding.dialogEditMessage.setText(message)
            setPositiveButton(getString(R.string.save_positive_button_text)) { _, _ ->
                store.accept(Event.Ui.EditMessage(messageId, dialogBinding.dialogEditMessage.text.toString()))
                closeKeyboard(requireContext(), binding.sendLayout.messageEditText.windowToken)
            }
            setNegativeButton(getString(R.string.cancel_negative_button_text)) { _, _ -> }
        }

        val alertDialog = builder.create()
        alertDialog.show()
        dialogBinding.dialogEditMessage.postDelayed({
            dialogBinding.dialogEditMessage.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(dialogBinding.dialogEditMessage, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    override val initEvent: Event = Event.Ui.Initial

    private val retainStore = retainStoreHolder {
        storeFactory.provide()
    }

    override val storeHolder: StoreHolder<Event, Effect, State>
        get() = retainStore.value

    override fun render(state: State) {

        binding.progressBar.isVisible = state.isLoading
        binding.pageProgressBar.isVisible = state.isUpperLoaderVisible
        binding.recycleMessage.isVisible = state.messagesList.isNotEmpty()

        if (state.messagesList.isNotEmpty()) {
            showBadge = (state.messagesList.size - messagesAdapter.itemCount in 1..LAST_VISIBLE_ITEM)
                    && binding.fabContainer.isVisible
            binding.badge.isVisible = showBadge
            messagesAdapter.submitList(state.messagesList)
            if (layoutManager.findLastVisibleItemPosition() == messagesAdapter.itemCount - 1)
                binding.recycleMessage.post {
                    binding.recycleMessage.smoothScrollToPosition(messagesAdapter.itemCount + LAST_VISIBLE_ITEM)
                }
        }

        binding.sendLayout.messageTopicEditText.isClickable = state.isSubscribed
        binding.sendLayout.sendTopicFrame.isVisible = state.isSubscribed && state.topicName == null
        binding.messagesButtonSubscribe.isVisible = !state.isSubscribed

        if (state.updateMessageId != null) {
            val position = messagesAdapter.getPositionById(state.updateMessageId)
            messagesAdapter.notifyItemChanged(position)
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            Effect.ClickExit -> router.exit()
            is Effect.ShowError -> {
                if (effect.errorMessage.contains(getString(R.string.Internet_error), true))
                    Snackbar.make(binding.root, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                else
                    Snackbar.make(binding.root, effect.errorMessage, Toast.LENGTH_SHORT).show()
            }
            is Effect.ShowInfo -> {
                Toast.makeText(requireContext(), effect.infoMessage, Toast.LENGTH_SHORT).show()
            }
            is Effect.NavigateToMessages -> {
                router.navigateTo(
                    Screens.Messages(
                        store.currentState.streamName,
                        store.currentState.streamId,
                        effect.isSubscribed,
                        effect.topicName
                    )
                )
            }
            is Effect.FileDownloadedEffect -> {
                effect.response.use { responseBody ->
                    val resolver = requireContext().contentResolver

                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, effect.fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, effect.contentType)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    } else {
                        null
                    }
                    uri?.let {
                        resolver.openOutputStream(it)?.use { outputStream ->
                            val inputStream = responseBody.body?.byteStream()
                            inputStream?.copyTo(outputStream)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPageLoaderDebounce()
        (requireActivity() as MainActivity).apply {
            setStatusBarColor(R.color.green_main)
            setNavigationViewVisibility(false)
        }
        val streamName = arguments?.getString(STREAM_NAME)!!
        val streamId = arguments?.getInt(STREAM_ID)!!
        val isSubscribed = arguments?.getBoolean(SUBSCRIBED)!!
        val topicName = arguments?.getString(TOPIC_NAME)
        setMessagesAdapter()
        layoutManager = binding.recycleMessage.layoutManager as LinearLayoutManager
        if (savedInstanceState == null && store.currentState.messagesList.isEmpty()) {
            store.accept(Event.Ui.SubscriptionOnEvents(streamName, streamId, isSubscribed, topicName))
            store.accept(Event.Ui.MessagesLoading(ANCHOR_NEWEST))
        }

        binding.titleToolbar.text = getString(R.string.topic_name_text, streamName)

        setSendLayout()
        if (topicName != null) {
            binding.textToolbar.text = getString(R.string.topic_name_text, topicName)
            binding.sendLayout.messageTopicEditText.setText(topicName)
            binding.sendLayout.sendTopicFrame.isVisible = false
        } else {
            binding.sendLayout.messageTopicEditText.setText(getString(R.string.default_Topic))
            binding.textToolbar.isVisible = false
        }

        binding.backArrowToolbar.setOnClickListener {
            store.accept(Event.Ui.ExitClick)
        }

        binding.messagesButtonSubscribe.setOnClickListener {
            store.accept(Event.Ui.StreamSubscribe)
        }

        binding.fab.setOnClickListener {
            binding.recycleMessage.smoothScrollToPosition(messagesAdapter.itemCount)
            showBadge = false
            binding.badge.isVisible = showBadge
            binding.fabContainer.isVisible = false
        }

        val layoutManager = binding.recycleMessage.layoutManager as LinearLayoutManager
        binding.recycleMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    val messages = messagesAdapter.currentList.filterIsInstance<MessageDelegateItem>()
                        .map { it.content() as MessageItem }
                    if ((layoutManager.findFirstVisibleItemPosition() <= LAST_VISIBLE_ITEM)
                        && messages.size >= PAGE_SIZE
                    ) {
                        val anchor = messages.first().id
                        viewLifecycleOwner.lifecycleScope.launch {
                            anchorFlow.emit(anchor)
                        }
                    }
                }
                binding.fabContainer.isVisible =
                    layoutManager.findLastVisibleItemPosition() < messagesAdapter.itemCount - LAST_VISIBLE_ITEM
            }
        })
    }

    @OptIn(FlowPreview::class)
    private fun setupPageLoaderDebounce() {
        viewLifecycleOwner.lifecycleScope.launch {
            anchorFlow
                .debounce(PAGE_DEBOUNCE)
                .collect { anchor -> store.accept(Event.Ui.MessagesLoading(anchor.toString())) }
        }
    }

    private fun setMessagesAdapter() {
        messagesAdapter = MessagesAdapter()
        messagesAdapter.apply {
            addDelegate(
                MessageDelegate(
                    { messageId, messagePosition, myMessage, hasContent ->
                        openBottomSheetActionsDialog(messageId, messagePosition, myMessage, hasContent)
                    },
                    { messageId, messagePosition -> openBottomSheetEmojiDialog(messageId, messagePosition) },
                    { emoji, id, position -> onEmojiCLick(emoji, id, position) },
                    markwon
                )
            )
            addDelegate(DateDelegate())
            addDelegate(
                TopicSeparatorDelegate { topicName -> onTopicSeparatorClick(topicName) }
            )
        }
        binding.recycleMessage.adapter = messagesAdapter
        val stickyHeaderDecoration = StickyHeaderDecoration()
        binding.recycleMessage.addItemDecoration(stickyHeaderDecoration)
        binding.recycleMessage.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    if (stickyHeaderDecoration.isPointInHeader(e.x, e.y)) {
                        val topicSeparatorName = stickyHeaderDecoration.currentTopic
                        onTopicSeparatorClick(topicSeparatorName ?: getString(R.string.base_topic_name))
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun onTopicSeparatorClick(topicName: String) {
        store.accept(Event.Ui.TopicClick(topicName))
    }

    private fun onEmojiCLick(emoji: String, messageId: Int, position: Int) {
        store.accept(Event.Ui.EmojiClick(messageId = messageId, emojiCode = emoji))
        messagesAdapter.notifyItemChanged(position)
    }

    private fun openBottomSheetActionsDialog(messageId: Int, position: Int, myMessage: Boolean, hasContent: Boolean) {
        MessagesActionsDialogFragment.getNewInstance(messageId, position, myMessage, hasContent, store.currentState.isSubscribed)
            .show(parentFragmentManager, BOTTOM_FRAGMENT_ACTION_TAG)
    }

    private fun openBottomSheetEmojiDialog(messageId: Int, position: Int) {
        EmojiBottomSheetDialogFragment.getNewInstance(messageId, position)
            .show(parentFragmentManager, BOTTOM_FRAGMENT_EMOJI_TAG)
    }

    private val pickFileResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val contentResolver = requireContext().contentResolver
                    val mimeType = contentResolver.getType(uri)
                    val inputStream: InputStream =
                        contentResolver.openInputStream(uri)
                            ?: throw Exception(getString(R.string.exception_open_file_input_stream))
                    val fileRequestBody = inputStream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())
                    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                    val fileName = "${uri.lastPathSegment ?: getString(R.string.unknown_file_name)}.$extension"
                    val filePart = MultipartBody.Part.createFormData(getString(R.string.file_name), fileName, fileRequestBody)
                    viewLifecycleOwner.lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            inputStream.close()
                        }
                    }
                    store.accept(
                        Event.Ui.SendFile(
                            filePart, fileName,
                            binding.sendLayout.messageTopicEditText.text.toString().takeIf { it.isNotEmpty() }
                                ?: getString(R.string.default_Topic)
                        )
                    )
                }
            }
        }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        pickFileResultLauncher.launch(intent)
    }

    private fun setSendLayout() {
        binding.sendLayout.apply {
            messageTopicEditText.setOnLongClickListener {
                showTopicChoiceDialog()
                return@setOnLongClickListener true
            }
            messageTopicEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty() && ::topicAlertDialog.isInitialized && topicAlertDialog.isShowing) {
                        topicAlertDialog.dismiss()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
            sendButton.also {
                messageEditText.doOnTextChanged { _, _, _, _ ->
                    if (messageEditText.text.toString().isEmpty()) {
                        it.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_plus))
                    } else {
                        it.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_send))
                    }
                }
            }
            sendButton.setOnClickListener {
                if (messageEditText.text.toString().isNotEmpty()) {
                    store.accept(
                        Event.Ui.PostingMessage(
                            messageEditText.text.toString(),
                            binding.sendLayout.messageTopicEditText.text.toString().takeIf { it.isNotEmpty() }
                                ?: getString(R.string.default_Topic)
                        )
                    )
                    binding.sendLayout.messageEditText.clearFocus()
                    binding.sendLayout.messageTopicEditText.clearFocus()
                } else {
                    openFilePicker()
                }
                messageEditText.text.clear()
                closeKeyboard(requireContext(), binding.sendLayout.messageEditText.windowToken)
                binding.recycleMessage.postDelayed({
                    binding.fab.performClick()
                }, API_DELAY)
            }
        }
    }

    private fun showTopicChoiceDialog(topicsList: List<String>? = null, messageId: Int? = null) {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        val dialogBinding = TopicChoiceDialogBinding.inflate(layoutInflater)

        with(builder) {
            setTitle(getString(R.string.messages_fragment_choose_topic))
            setView(dialogBinding.root)
            if (topicsList != null) {
                dialogBinding.topicChoiceDialogText.isVisible = true
                setPositiveButton(getString(R.string.messages_fragment_move_message)) { _, _ ->
                    if (messageId != null)
                        store.accept(Event.Ui.MoveMessage(messageId, dialogBinding.topicChoiceDialogText.text.toString()))
                    closeKeyboard(requireContext(), binding.sendLayout.messageEditText.windowToken)
                }
            }
            setNegativeButton(getString(R.string.cancel_negative_button_text)) { _, _ -> }
        }
        topicAlertDialog = builder.create()
        val topicAdapter = TopicsAdapter { onTopicClick(it, messageId) }
        dialogBinding.topicChoiceDialogRecycler.adapter = topicAdapter
        topicAdapter.submitList(topicsList ?: store.currentState.topicsList)
        topicAlertDialog.show()
    }

    private fun onTopicClick(topicName: String, messageId: Int?) {
        if (messageId != null)
            store.accept(Event.Ui.MoveMessage(messageId, topicName))
        else
            binding.sendLayout.messageTopicEditText.setText(topicName)
        topicAlertDialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val STREAM_NAME = "STREAM_NAME"
        private const val STREAM_ID = "STREAM_ID"
        private const val TOPIC_NAME = "TOPIC_NAME"
        const val SUBSCRIBED = "SUBSCRIBED"
        private const val BOTTOM_FRAGMENT_EMOJI_TAG = "EmojiBottomSheetDialogFragment_tag"
        private const val BOTTOM_FRAGMENT_ACTION_TAG = "ActionBottomSheetDialogFragment_tag"
        const val EMOJI_NAME = "EMOJI_NAME"
        const val EMOJI_REQUEST = "EMOJI_REQUEST"
        const val ACTION_NAME = "ACTION_NAME"
        const val ACTION_REQUEST = "ACTION_REQUEST"
        const val POSITION = "POSITION"
        const val MESSAGE_ID = "MESSAGE_ID"
        const val MY_MESSAGE = "MY_MESSAGE"
        const val HAS_CONTENT = "HAS_CONTENT"
        private const val PAGE_DEBOUNCE = 500L
        private const val PAGE_SIZE = 20
        private const val LAST_VISIBLE_ITEM = 5

        fun getNewInstance(streamName: String, streamId: Int, subscribed: Boolean, topicName: String?): MessagesFragment {
            return MessagesFragment().apply {
                arguments = Bundle().apply {
                    putString(STREAM_NAME, streamName)
                    putInt(STREAM_ID, streamId)
                    putBoolean(SUBSCRIBED, subscribed)
                    putString(TOPIC_NAME, topicName)
                }
            }
        }
    }
}

