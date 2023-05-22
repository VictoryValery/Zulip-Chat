package com.victoryvalery.tfsproject.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import com.victoryvalery.tfsproject.R
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.searchview.KSearchView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

object StreamsScreen : KScreen<StreamsScreen>() {
    override val layoutId: Int = R.layout.fragment_streams
    override val viewClass: Class<*> = StreamsScreen::class.java

    val streamsList = KRecyclerView(
        { withId(R.id.recycle_streams) },
        itemTypeBuilder = {
            itemType(::StreamItem)
            itemType(::TopicItem)
        }
    )

    class TopicItem(parent: Matcher<View>) : KRecyclerItem<TopicItem>(parent) {
        val topicTitle = KTextView(parent) { withId(R.id.topic_title) }
    }

    class StreamItem(parent: Matcher<View>) : KRecyclerItem<StreamItem>(parent) {
        val streamTitle = KTextView(parent) { withId(R.id.stream_title) }
        val streamButton = KButton(parent) { withId(R.id.stream_topics) }

    }

    val streamSearch = KSearchView { withId(R.id.search_view) }
}
