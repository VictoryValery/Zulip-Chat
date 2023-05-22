package com.victoryvalery.tfsproject.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import com.victoryvalery.tfsproject.R
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

object BottomEmojiDialogScreen : KScreen<BottomEmojiDialogScreen>() {

    override val layoutId: Int = R.layout.bottom_sheet_dialog_layout
    override val viewClass: Class<*> = BottomEmojiDialogScreen::class.java

    val emojiList = KRecyclerView(
        { withId(R.id.emoji_recycler_grid) },
        { itemType(::EmojiItem) }
    )

    class EmojiItem(parent: Matcher<View>) : KRecyclerItem<EmojiItem>(parent) {
        val emoji = KTextView(parent) { withId(R.id.emoji_item_code) }
    }
}
