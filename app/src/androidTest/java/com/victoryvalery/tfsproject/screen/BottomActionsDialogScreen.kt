package com.victoryvalery.tfsproject.screen

import com.kaspersky.kaspresso.screens.KScreen
import com.victoryvalery.tfsproject.R
import io.github.kakaocup.kakao.text.KButton

object BottomActionsDialogScreen : KScreen<BottomActionsDialogScreen>() {

    override val layoutId: Int = R.layout.bottom_sheet_options_dialog_layout
    override val viewClass: Class<*> = BottomActionsDialogScreen::class.java

    val actionAddReaction = KButton { withId(R.id.dialog_add_reaction) }
}
