package com.victoryvalery.tfsproject.presentation.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.victoryvalery.tfsproject.R
import androidx.appcompat.widget.AppCompatImageView as AddImageView

class AddReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0
) : AddImageView(context, attrs, defStyleAtr) {

    private val backgroundRect = Rect()
    private val rectF = RectF(backgroundRect)

    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.background_dark_gray)
    }

    init {
        this.setImageResource(R.drawable.ic_add)
        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val addWidth = context.resources.getDimension(R.dimen.add_view_width).toInt()
        val addHeight = context.resources.getDimension(R.dimen.add_view_height).toInt()

        val measuredWidth = resolveSize(addWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(addHeight, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val childrenPadding = context.resources.getDimension(R.dimen.basic_indent)
        backgroundRect.set(0, 0, width, height)
        rectF.set(backgroundRect)
        canvas.drawRoundRect(rectF, childrenPadding, childrenPadding, backgroundPaint)

        val centerX = backgroundRect.centerX().toFloat()
        val centerY = backgroundRect.centerY().toFloat()
        val halfWidth = drawable.intrinsicWidth / 2
        val halfHeight = drawable.intrinsicHeight / 2
        drawable.setBounds(
            (centerX - halfWidth).toInt(),
            (centerY - halfHeight).toInt(),
            (centerX + halfWidth).toInt(),
            (centerY + halfHeight).toInt()
        )
        drawable.draw(canvas)
    }

}
