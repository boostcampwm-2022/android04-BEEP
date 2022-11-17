package com.lighthouse.presentation.util.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpaceItemDecoration(
    private val vSpace: Int,
    private val hSpace: Int,
    private val start: Int = 0,
    private val top: Int = 0,
    private val end: Int = 0,
    private val bottom: Int = 0
) : RecyclerView.ItemDecoration() {

    constructor(
        vSpace: Float,
        hSpace: Float,
        start: Float = 0f,
        top: Float = 0f,
        end: Float = 0f,
        bottom: Float = 0f
    ) : this(vSpace.toInt(), hSpace.toInt(), start.toInt(), top.toInt(), end.toInt(), bottom.toInt())

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val manager = parent.layoutManager as? GridLayoutManager ?: return

        val position = parent.getChildAdapterPosition(view)
        val spanCount = manager.spanCount

        outRect.left = if (isFirstCol(position, spanCount)) start else hSpace / 2
        outRect.top = if (isFirstRow(position, spanCount)) top else vSpace / 2
        outRect.right = if (isLastCol(position, spanCount)) end else hSpace / 2
        outRect.bottom = if (isLastRow(position, spanCount, getItemCount(parent))) bottom else vSpace / 2
    }

    private fun isFirstRow(position: Int, spanCount: Int): Boolean {
        return position < spanCount
    }

    private fun isFirstCol(position: Int, spanCount: Int): Boolean {
        return position % spanCount == 0
    }

    private fun isLastRow(position: Int, spanCount: Int, itemCount: Int): Boolean {
        return position >= itemCount - itemCount % spanCount
    }

    private fun isLastCol(position: Int, spanCount: Int): Boolean {
        return isFirstCol(position + 1, spanCount)
    }

    private fun getItemCount(parent: RecyclerView): Int {
        return parent.adapter?.itemCount ?: 0
    }
}
