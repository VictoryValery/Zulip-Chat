package com.victoryvalery.tfsproject.presentation.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.*
import com.google.android.material.imageview.ShapeableImageView
import com.victoryvalery.tfsproject.R

class MessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    val textName: TextView
    val avatar: ShapeableImageView
    var textMessage: TextView
    var flexBox: CustomFlexBoxGroup

    private var myMessage = false

    fun setMyMessage(value: Boolean) {
        myMessage = value
        setLayoutVisibility()
        flexBox.setMyMessage(value)
        invalidate()
        requestLayout()
    }

    private fun setLayoutVisibility() {
        if (myMessage) {
            textMessage.background = ContextCompat.getDrawable(context, R.drawable.bg_gradient)
            avatar.isVisible = false
            textName.isVisible = false
        } else {
            textName.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg)
            textMessage.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg)
            avatar.isVisible = true
            textName.isVisible = true
        }
    }

    init {
        inflate(context, R.layout.message_viewgroup, this)
        textMessage = findViewById(R.id.message_textView)
        flexBox = findViewById(R.id.custom_flexBoxGroup)
        avatar = findViewById(R.id.message_avatar_image)
        textName = findViewById(R.id.message_name_textView)

        textMessage.isLongClickable = true

        setLayoutVisibility()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val basicIndent = context.resources.getDimension(R.dimen.basic_indent).toInt()
        val totalWidth: Int
        val totalHeight: Int

        if (myMessage) {
            setPadding(basicIndent, basicIndent, basicIndent, basicIndent)

            measureChildWithMargins(
                textMessage,
                widthMeasureSpec,
                0,
                heightMeasureSpec,
                0
            )

            measureChildWithMargins(
                flexBox,
                widthMeasureSpec,
                0,
                heightMeasureSpec,
                0
            )

            totalWidth = paddingLeft + paddingRight + textMessage.marginLeft + textMessage.marginRight +
                    maxOf(textMessage.measuredWidth, flexBox.measuredWidth)

            totalHeight = paddingTop + paddingTop + textMessage.paddingBottom + textMessage.paddingTop +
                    textMessage.measuredHeight + flexBox.measuredHeight + paddingBottom
        } else {
            setPadding(0, basicIndent, 0, basicIndent)

            measureChildWithMargins(avatar, widthMeasureSpec, 0, heightMeasureSpec, 0)

            measureChildWithMargins(
                textName,
                widthMeasureSpec,
                avatar.measuredWidth + avatar.marginLeft + avatar.marginRight,
                heightMeasureSpec,
                avatar.measuredHeight + avatar.marginTop + avatar.marginBottom
            )

            measureChildWithMargins(
                textMessage,
                widthMeasureSpec,
                avatar.measuredWidth + avatar.marginLeft + avatar.marginRight,
                heightMeasureSpec,
                avatar.measuredHeight + avatar.marginTop + avatar.marginBottom
            )

            measureChildWithMargins(
                flexBox,
                widthMeasureSpec,
                avatar.measuredWidth + avatar.marginLeft + avatar.marginRight,
                heightMeasureSpec,
                avatar.measuredHeight + avatar.marginTop + avatar.marginBottom
            )

            totalWidth = paddingLeft + paddingRight + avatar.measuredWidth + avatar.marginLeft + avatar.marginRight +
                    textMessage.marginLeft + textMessage.marginRight +
                    maxOf(textMessage.measuredWidth, textName.measuredWidth, flexBox.measuredWidth)

            totalHeight = paddingTop + paddingBottom + textName.paddingBottom + textName.paddingTop +
                    textName.measuredHeight - textMessage.paddingBottom - textMessage.paddingBottom -
                    textMessage.paddingTop + textMessage.measuredHeight + flexBox.measuredHeight
        }

        setMeasuredDimension(totalWidth, totalHeight)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (myMessage) {
            onLayoutMyMessage()
        } else {
            onLayoutMessage()
        }
    }

    private fun onLayoutMyMessage() {
        var offsetY = paddingTop

        textMessage.layout(
            width - textMessage.paddingRight - textMessage.measuredWidth,
            offsetY + textMessage.marginTop,
            width - paddingRight,
            offsetY + textMessage.marginTop + textMessage.measuredHeight + paddingTop / 2 + paddingTop
        )

        offsetY += paddingTop

        flexBox.layout(
            width - paddingRight - flexBox.measuredWidth,
            offsetY + flexBox.marginTop + textMessage.marginTop + textMessage.measuredHeight + paddingTop / 2,
            width - paddingRight,
            offsetY + textMessage.marginTop + textMessage.measuredHeight + flexBox.measuredHeight + paddingTop +
                    textMessage.paddingBottom + textMessage.paddingTop + paddingBottom + paddingTop / 2
        )
    }

    private fun onLayoutMessage() {
        var offsetX = paddingLeft + avatar.marginLeft
        val offsetY = paddingTop

        avatar.layout(
            offsetX,
            offsetY + avatar.marginTop,
            offsetX + avatar.measuredWidth,
            offsetY + avatar.measuredHeight + avatar.marginTop
        )

        offsetX += avatar.measuredWidth + avatar.marginRight

        textName.layout(
            offsetX,
            offsetY + textName.marginTop,
            offsetX + maxOf(textMessage.measuredWidth, textName.measuredWidth),
            offsetY + textName.marginTop + textName.measuredHeight
        )

        textMessage.layout(
            offsetX,
            offsetY + textMessage.marginTop + textName.measuredHeight - textName.paddingBottom,
            offsetX + maxOf(textMessage.measuredWidth, textName.measuredWidth),
            offsetY + textMessage.marginTop + textMessage.measuredHeight + textName.measuredHeight
        )

        flexBox.layout(
            offsetX,
            offsetY + flexBox.marginTop + textMessage.marginTop + textName.measuredHeight + textMessage.measuredHeight,
            width,
            offsetY + textMessage.marginTop + textMessage.measuredHeight +
                    textName.measuredHeight + flexBox.measuredHeight
                    + textName.paddingBottom + textName.paddingTop + textMessage.paddingBottom + textMessage.paddingTop
        )
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

}
