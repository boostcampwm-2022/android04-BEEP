package com.lighthouse.presentation.utils.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListSpaceItemDecoration(
    private val space: Int,
    private val start: Int = 0,
    private val top: Int = 0,
    private val end: Int = 0,
    private val bottom: Int = 0
) : RecyclerView.ItemDecoration() {

    constructor(
        space: Float,
        start: Float = 0f,
        top: Float = 0f,
        end: Float = 0f,
        bottom: Float = 0f
    ) : this(space.toInt(), start.toInt(), top.toInt(), end.toInt(), bottom.toInt())

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val manager = parent.layoutManager as? LinearLayoutManager ?: return
        val itemCount = parent.adapter?.itemCount ?: 0
        val position = when (parent.getChildAdapterPosition(view)) {
            0 -> Position.Start
            itemCount - 1 -> Position.End
            else -> Position.Mid
        }

        val isVertical = manager.orientation == LinearLayoutManager.VERTICAL
        if (isVertical) {
            calculateVerticalOffsets(outRect, position)
        } else {
            calculateHorizontalOffsets(outRect, position)
        }
    }

    private fun calculateVerticalOffsets(outRect: Rect, pos: Position) {
        outRect.left = start
        outRect.top = when (pos) {
            Position.Start -> top
            else -> space / 2
        }
        outRect.right = end
        outRect.bottom = when (pos) {
            Position.End -> bottom
            else -> space / 2
        }
    }

    private fun calculateHorizontalOffsets(outRect: Rect, pos: Position) {
        outRect.left = when (pos) {
            Position.Start -> start
            else -> space / 2
        }
        outRect.top = top
        outRect.right = when (pos) {
            Position.End -> end
            else -> space / 2
        }
        outRect.bottom = bottom
    }

    private enum class Position {
        Start, End, Mid
    }
}
