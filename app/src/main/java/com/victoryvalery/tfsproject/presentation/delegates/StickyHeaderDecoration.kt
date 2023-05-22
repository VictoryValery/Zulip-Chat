package com.victoryvalery.tfsproject.presentation.delegates

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoryvalery.tfsproject.presentation.delegates.topicSeparator.TopicSeparatorDelegate

class StickyHeaderDecoration : RecyclerView.ItemDecoration() {

    private var currentHeader: View? = null
    var currentTopic: String? = null

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        val header = parent.createHeaderView(topChildPosition)
            ?: parent.createPreviousHeaderView(topChildPosition)
            ?: return
        currentHeader = header
        parent.makeViewFullLayout(header)
        val nextHeader = parent.findNextHeaderView()

        val dx = if (nextHeader != null) {
            val differ = nextHeader.top.toFloat() - header.height / 2
            if (differ <= 0) differ else 0f
        } else {
            0f
        }
        canvas.run {
            save()
            translate(0f, dx)
            header.draw(this)
            restore()
        }
    }

    fun isPointInHeader(x: Float, y: Float): Boolean {
        return currentHeader?.let { x <= it.right && y <= it.bottom } ?: false
    }

    private fun RecyclerView.createHeaderView(position: Int): View? {
        val adapter = adapter ?: return null
        if (position == RecyclerView.NO_POSITION || position >= adapter.itemCount) {
            return null
        }
        return (adapter.onCreateViewHolder(this, adapter.getItemViewType(position)) as? TopicSeparatorDelegate.ViewHolder)
            ?.apply {
                adapter.onBindViewHolder(this, position)
                currentTopic = getTopicName()
            }
            ?.itemView
    }

    private fun RecyclerView.createPreviousHeaderView(position: Int): View? {
        if (position <= 0) return null

        return (position downTo 0).asSequence()
            .mapNotNull { createHeaderView(it) }
            .firstOrNull()
    }

    private fun RecyclerView.findNextHeaderView(): View? {
        var header: View? = null
        val itemCount = adapter?.itemCount ?: 0

        if (itemCount > 1) {
            val visibleCount = layoutManager?.childCount ?: 0
            for (i in 1..visibleCount) {
                val currentHeader = getChildAt(i)
                if (currentHeader != null && getChildViewHolder(currentHeader) as? TopicSeparatorDelegate.ViewHolder != null) {
                    header = currentHeader
                    break
                }
            }
        }
        return header
    }

    private fun RecyclerView.makeViewFullLayout(view: View) {
        val parentWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val parentHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        val childLayoutParam = view.layoutParams ?: return

        val childWidth = ViewGroup.getChildMeasureSpec(parentWidth, 0, childLayoutParam.width)
        val childHeight = ViewGroup.getChildMeasureSpec(parentHeight, 0, childLayoutParam.height)

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
