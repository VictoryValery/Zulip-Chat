package com.victoryvalery.tfsproject.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import com.victoryvalery.tfsproject.R
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.progress.KProgressBar
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KSnackbar
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

object MessageScreen : KScreen<MessageScreen>() {
    override val layoutId: Int = R.layout.fragment_messages
    override val viewClass: Class<*> = MessageScreen::class.java

    val messageList = KRecyclerView(
        { withId(R.id.recycle_message) },
        itemTypeBuilder = {
            itemType(::MessageItem)
            itemType(::DateItem)
        }
    )

    class DateItem(parent: Matcher<View>) : KRecyclerItem<DateItem>(parent) {
        val date = KTextView(parent) { withId(R.id.date_separator) }
    }

    class MessageItem(parent: Matcher<View>) : KRecyclerItem<MessageItem>(parent) {
        val messageBody = KTextView(parent) { withId(R.id.message_textView) }
        val reactions = KView(parent) { withId(R.id.custom_flexBoxGroup) }
    }

    val kSnackbar: KSnackbar = KSnackbar()
    val loader: KProgressBar = KProgressBar { withId(R.id.progress_bar) }
}
