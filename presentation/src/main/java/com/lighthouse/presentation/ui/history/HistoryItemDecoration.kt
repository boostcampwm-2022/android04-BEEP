package com.lighthouse.presentation.ui.history

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.ui.history.adapter.gifticondetail.HistoryAdapter.Companion.HEADER_TYPE
import com.lighthouse.presentation.ui.history.adapter.gifticondetail.HistoryAdapter.Companion.ITEM_TYPE

class HistoryItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val dayDividerHeight = 30f
    private val itemDividerHeight = 2f
    private val itemDividerPaddingHorizontal = 24.dp

    private val dayDividerPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.light_gray)
        strokeWidth = dayDividerHeight
    }
    private val itemDividerPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.light_gray)
        strokeWidth = itemDividerHeight
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)

        if (parent.adapter?.getItemViewType(position) == HEADER_TYPE) {
            outRect.top = dayDividerHeight.toInt()
        } else if (parent.adapter?.getItemViewType(position) == ITEM_TYPE) {
            outRect.top = itemDividerHeight.toInt()
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 1 until parent.childCount) {
            val item = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(item)

            if (parent.adapter?.getItemViewType(position) == HEADER_TYPE) {
                val top = item.top - dayDividerHeight
                val bottom = item.top - dayDividerHeight
                c.drawLine(0f, top, parent.width.toFloat(), bottom, dayDividerPaint)
            } else if (
                parent.adapter?.getItemViewType(position) == ITEM_TYPE &&
                parent.adapter?.getItemViewType(position - 1) != HEADER_TYPE
            ) {
                val top = item.top - itemDividerHeight
                val bottom = item.top
                c.drawLine(
                    left.toFloat() + itemDividerPaddingHorizontal,
                    top,
                    right.toFloat() - itemDividerPaddingHorizontal,
                    bottom.toFloat(),
                    itemDividerPaint,
                )
            }
        }
    }
}
