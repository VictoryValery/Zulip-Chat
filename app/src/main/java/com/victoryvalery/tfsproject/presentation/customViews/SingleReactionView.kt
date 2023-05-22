package com.victoryvalery.tfsproject.presentation.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.dp
import com.victoryvalery.tfsproject.sp

class SingleReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAtr, defStyleRes) {

    private val textBounds = Rect()
    private val backgroundRect = Rect()
    private val rectF = RectF(backgroundRect)
    private val childrenPadding = context.resources.getDimension(R.dimen.basic_indent).toInt()

    private val minimumReactionWidth = 40f.dp(context).toInt()
    private val minimumReactionHeight = 32f.dp(context).toInt()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14f.sp(context)
        color = ContextCompat.getColor(context, R.color.text_dark_gray)
    }

    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        background = ContextCompat.getDrawable(context, R.drawable.bg_reaction_view)
    }

    private var textToDraw = ""
    private var emoji = ""
    var count = 0
        private set

    init {
        isSelected = false
    }

    fun setAttributes(reaction: String, count: Int, isClicked: Boolean) {
        emoji = reaction
        this.count = count
        isSelected = isClicked
        textToDraw = "$reaction ${optimizeNumbers(count)}"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(textToDraw, 0, textToDraw.length, textBounds)
        val textWidth = textBounds.width()
        val textHeight = textBounds.height()

        val measuredWidth = resolveSize(textWidth + childrenPadding + childrenPadding, widthMeasureSpec)
        val measuredHeight = resolveSize(textHeight + childrenPadding + childrenPadding, heightMeasureSpec)
        setMeasuredDimension(
            maxOf(measuredWidth, minimumReactionWidth),
            maxOf(measuredHeight, minimumReactionHeight)
        )
    }

    override fun onDraw(canvas: Canvas) {
        val centerY = height / 2 - textBounds.exactCenterY()
        val centerX = width / 2 - textBounds.exactCenterX()

        canvas.drawRoundRect(rectF, paddingLeft.toFloat(), centerY, backgroundPaint)
        canvas.drawText(textToDraw, centerX, centerY, textPaint)
    }

    private fun optimizeNumbers(number: Int): String {
        return when (number) {
            in 1..999 -> number.toString()
            in 1000..999999 -> "%.1f".format(number.toDouble() / 1000) + "k"
            in 1000000..1099999 -> "1.0M"
            else -> "%.1f".format(number.toDouble() / 1000000) + "M"
        }
    }

}
