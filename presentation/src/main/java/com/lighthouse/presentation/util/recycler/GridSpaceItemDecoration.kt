package com.lighthouse.presentation.util.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpaceItemDecoration(
    private val vSpace: Int,
    private val hSpace: Int,
    private val scrollStart: Int = 0,
    private val scrollEnd: Int = 0
) : RecyclerView.ItemDecoration() {

    constructor(
        vSpace: Float,
        hSpace: Float,
        scrollStart: Float = 0f,
        scrollEnd: Float = 0f
    ) : this(vSpace.toInt(), hSpace.toInt(), scrollStart.toInt(), scrollEnd.toInt())

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val manager = parent.layoutManager as? GridLayoutManager ?: return

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        val isVertical = manager.orientation == GridLayoutManager.VERTICAL
        if (isVertical) {
            calculateVerticalOffsets(outRect, manager, position, itemCount)
        } else {
            calculateHorizontalOffsets(outRect, manager, position, itemCount)
        }
    }

    private fun calculateVerticalOffsets(
        outRect: Rect,
        manager: GridLayoutManager,
        position: Int,
        itemCount: Int
    ) {
        outRect.left = hSpace / 2
        outRect.top = if (isFirstRow(manager, position)) scrollStart else vSpace / 2
        outRect.right = hSpace / 2
        outRect.bottom = if (isLastRow(manager, position, itemCount)) scrollEnd else vSpace / 2
    }

    private fun calculateHorizontalOffsets(
        outRect: Rect,
        manager: GridLayoutManager,
        position: Int,
        itemCount: Int
    ) {
        outRect.left = if (isFirstRow(manager, position)) scrollStart else hSpace / 2
        outRect.top = vSpace / 2
        outRect.right = if (isLastRow(manager, position, itemCount)) scrollEnd else hSpace / 2
        outRect.bottom = vSpace / 2
    }

    private fun isFirstRow(manager: GridLayoutManager, position: Int): Boolean {
        return getGroupIndex(manager, position) == getGroupIndex(manager, 0)
    }

    private fun isLastRow(manager: GridLayoutManager, position: Int, itemCount: Int): Boolean {
        return getGroupIndex(manager, position) == getGroupIndex(manager, itemCount - 1)
    }

    private fun getGroupIndex(manager: GridLayoutManager, position: Int): Int {
        return manager.spanSizeLookup.getSpanGroupIndex(position, manager.spanCount)
    }
}
