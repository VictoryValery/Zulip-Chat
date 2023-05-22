package com.victoryvalery.tfsproject.presentation.screenMessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.victoryvalery.tfsproject.data.apiStorage.mappers.emojiSetNCS
import com.victoryvalery.tfsproject.data.apiStorage.mappers.toReactionItem
import com.victoryvalery.tfsproject.databinding.BottomSheetDialogLayoutBinding
import com.victoryvalery.tfsproject.domain.models.ReactionItem
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesFragment.Companion.EMOJI_NAME
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesFragment.Companion.EMOJI_REQUEST
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesFragment.Companion.MESSAGE_ID
import com.victoryvalery.tfsproject.presentation.screenMessages.MessagesFragment.Companion.POSITION
import com.victoryvalery.tfsproject.presentation.adapters.EmojiAdapter

class EmojiBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDialogLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var emojiAdapter: EmojiAdapter
    private var position: Int? = null
    private var messageId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDialogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        position = arguments?.getInt(POSITION)
        messageId = arguments?.getInt(MESSAGE_ID)
        setEmojiAdapter()
        emojiAdapter.submitList(emojiSetNCS.map { it.toReactionItem() }.toList())
    }

    private fun setEmojiAdapter() {
        emojiAdapter = EmojiAdapter { onEmojiClick(it) }
        binding.emojiRecyclerGrid.adapter = emojiAdapter
    }

    private fun onEmojiClick(reaction: ReactionItem) {
        if (messageId != null && position != null)
            setFragmentResult(
                EMOJI_REQUEST,
                bundleOf(
                    EMOJI_NAME to reaction.emoji,
                    MESSAGE_ID to messageId,
                    POSITION to position
                )
            )
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getNewInstance(messageId: Int, position: Int): EmojiBottomSheetDialogFragment {
            return EmojiBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(MESSAGE_ID, messageId)
                    putInt(POSITION, position)
                }
            }
        }
    }
}
