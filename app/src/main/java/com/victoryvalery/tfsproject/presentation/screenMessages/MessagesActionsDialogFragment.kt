package com.victoryvalery.tfsproject.presentation.screenMessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.victoryvalery.tfsproject.databinding.BottomSheetOptionsDialogLayoutBinding
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesActions.*

class MessagesActionsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetOptionsDialogLayoutBinding? = null
    private val binding get() = _binding!!

    private var position: Int? = null
    private var messageId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetOptionsDialogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        position = arguments?.getInt(MessagesFragment.POSITION)
        messageId = arguments?.getInt(MessagesFragment.MESSAGE_ID)
        val myMessage = arguments?.getBoolean(MessagesFragment.MY_MESSAGE) ?: false
        val hasContent = arguments?.getBoolean(MessagesFragment.HAS_CONTENT) ?: false
        val isSubscribed = arguments?.getBoolean(MessagesFragment.SUBSCRIBED) ?: false

        binding.dialogCopyMessage.setOnClickListener {
            setFragmentDialogResult(CopyToClipboard)
        }

        binding.dialogEditMessage.isVisible = myMessage
        binding.dialogEditMessage.setOnClickListener {
            setFragmentDialogResult(EditMessage)
        }

        binding.dialogDeleteMessage.isVisible = isSubscribed
        binding.dialogDeleteMessage.setOnClickListener {
            setFragmentDialogResult(DeleteMessage)
        }

        binding.dialogChangeMessageTopic.isVisible = isSubscribed
        binding.dialogChangeMessageTopic.setOnClickListener {
            setFragmentDialogResult(MoveMessage)
        }

        binding.dialogAddReaction.setOnClickListener {
            setFragmentDialogResult(AddReaction)
        }

        binding.dialogDownloadContent.isVisible = hasContent
        binding.dialogDownloadContent.setOnClickListener {
            setFragmentDialogResult(DownloadMessageContent)
        }
    }

    private fun setFragmentDialogResult(action: MessagesActions) {
        setFragmentResult(
            MessagesFragment.ACTION_REQUEST,
            bundleOf(
                MessagesFragment.ACTION_NAME to action,
                MessagesFragment.MESSAGE_ID to messageId,
                MessagesFragment.POSITION to position
            )
        )
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getNewInstance(
            messageId: Int,
            position: Int,
            otherUserMessage: Boolean,
            hasContent: Boolean,
            subscribed: Boolean
        ): MessagesActionsDialogFragment {
            return MessagesActionsDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(MessagesFragment.MY_MESSAGE, otherUserMessage)
                    putBoolean(MessagesFragment.HAS_CONTENT, hasContent)
                    putInt(MessagesFragment.MESSAGE_ID, messageId)
                    putInt(MessagesFragment.POSITION, position)
                    putBoolean(MessagesFragment.SUBSCRIBED, subscribed)
                }
            }
        }
    }
}