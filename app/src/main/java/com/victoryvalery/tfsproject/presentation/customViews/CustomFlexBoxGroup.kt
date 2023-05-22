package com.victoryvalery.tfsproject.presentation.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.domain.models.MessageItem

class CustomFlexBoxGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val children = mutableListOf<View>()
    private val childrenPadding = context.resources.getDimension(R.dimen.basic_indent).toInt()
    private var rowList = mutableListOf<MutableList<View>>()
    private var rowHeights = mutableListOf<Int>()

    private var myMessage = false

    private fun addChild(child: View) {
        children.add(0, child)
        addView(child)
        requestLayout()
    }

    private fun cleanChildren() {
        removeAllViews()
        children.clear()
        requestLayout()
    }

    fun setMyMessage(value: Boolean) {
        myMessage = value
        requestLayout()
    }

    fun updateReactions(
        messageItem: MessageItem,
        position: Int,
        onEmojiClick: (String, Int, Int) -> Unit,
        onAddEmojiClick: (Int, Int) -> Unit
    ) {
        cleanChildren()
        isVisible = messageItem.listReactions.isNotEmpty()
        if (messageItem.listReactions.isNotEmpty()) {
            val add = AddReactionView(context)
            add.setOnClickListener {
                onAddEmojiClick(messageItem.id, position)
            }
            addChild(add)
        }

        messageItem.listReactions
            .groupBy { it.emoji }
            .forEach { mapEntry ->
                val react = SingleReactionView(context)
                val isClicked = mapEntry.value.any { reactionItem -> reactionItem.isClicked }
                react.setAttributes(mapEntry.key, mapEntry.value.size, isClicked)
                react.setOnClickListener {
                    onEmojiClick(mapEntry.key, messageItem.id, position)
                }
                addChild(react)
            }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)

        var maxHeight = 0
        var currentWidth = 0
        var currentRow = mutableListOf<View>()

        rowList.clear()
        rowHeights.clear()

        children.forEach { child ->
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (currentWidth + child.measuredWidth > parentWidth) {
                rowList.add(currentRow)
                rowHeights.add(maxHeight)
                currentRow = mutableListOf()
                currentWidth = 0
                maxHeight = 0
            }

            currentRow.add(child)
            currentWidth += child.measuredWidth + childrenPadding
            maxHeight = maxOf(maxHeight, child.measuredHeight)
        }

        if (currentRow.isNotEmpty()) {
            rowList.add(currentRow)
            rowHeights.add(maxHeight)
        }

        var totalHeight = paddingTop + paddingBottom
        for (height in rowHeights) {
            totalHeight += height + childrenPadding
        }
        setMeasuredDimension(parentWidth, minOf(totalHeight, parentHeight))
    }

    override fun onLayout(p0: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var currentTop = paddingTop
        var currentLeft = if (myMessage) right - marginLeft - childrenPadding else paddingLeft
        var rowWidthMax = 0

        for (i in rowList.indices) {
            val currentRow = rowList[i]
            val rowHeight = rowHeights[i]
            val rowWidth = currentRow.sumOf { it.measuredWidth } + (currentRow.size - 1) * childrenPadding

            if (rowWidth > rowWidthMax)
                rowWidthMax = rowWidth

            if (myMessage) {
                if (right - marginLeft - childrenPadding - rowWidthMax < currentLeft) {
                    currentLeft = right - marginLeft - childrenPadding - rowWidthMax
                }
            }

            for (j in currentRow.indices) {
                val child = currentRow[j]
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight
                val childLeft = currentLeft
                val childTop = currentTop
                val childRight = currentLeft + childWidth
                val childBottom = currentTop + childHeight
                child.layout(childLeft, childTop, childRight, childBottom)
                currentLeft += childWidth + childrenPadding
            }
            currentTop += rowHeight + childrenPadding
            currentLeft = if (myMessage) right - marginLeft - childrenPadding else paddingLeft
        }
    }
}
