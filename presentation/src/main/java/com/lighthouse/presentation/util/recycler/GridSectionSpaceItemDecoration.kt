package com.lighthouse.presentation.util.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.LayoutParams
import androidx.recyclerview.widget.RecyclerView

class GridSectionSpaceItemDecoration(
    private val sectionDivider: Int,
    private val itemSpace: Int,
    private val scrollStart: Int = 0,
    private val scrollEnd: Int = 0
) : RecyclerView.ItemDecoration() {

    constructor(
        sectionDivider: Float,
        itemSpace: Float,
        scrollStart: Float = 0f,
        scrollEnd: Float = 0f
    ) : this(sectionDivider.toInt(), itemSpace.toInt(), scrollStart.toInt(), scrollEnd.toInt())

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val manager = parent.layoutManager as? GridLayoutManager ?: return
        val params = view.layoutParams as? LayoutParams ?: return

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        val isVertical = manager.orientation == GridLayoutManager.VERTICAL
        if (isVertical) {
            calculateVerticalOffsets(outRect, manager, params, position, itemCount)
        } else {
            calculateHorizontalOffsets(outRect, manager, params, position, itemCount)
        }
    }

    private fun calculateVerticalOffsets(
        outRect: Rect,
        manager: GridLayoutManager,
        params: LayoutParams,
        position: Int,
        itemCount: Int
    ) {
        outRect.left = itemSpace / 2
        outRect.top = if (isSection(manager, params)) {
            if (isFirstRow(manager, position)) scrollStart else sectionDivider - itemSpace / 2
        } else {
            itemSpace / 2
        }
        outRect.right = itemSpace / 2
        outRect.bottom = if (isLastRow(manager, position, itemCount)) scrollEnd else itemSpace / 2
    }

    private fun calculateHorizontalOffsets(
        outRect: Rect,
        manager: GridLayoutManager,
        params: LayoutParams,
        position: Int,
        itemCount: Int
    ) {
        outRect.left = if (isSection(manager, params)) {
            if (isFirstRow(manager, position)) scrollStart else sectionDivider - itemSpace / 2
        } else {
            itemSpace / 2
        }
        outRect.top = itemSpace / 2
        outRect.right = if (isLastRow(manager, position, itemCount)) scrollEnd else itemSpace / 2
        outRect.bottom = itemSpace / 2
    }

    private fun isSection(manager: GridLayoutManager, params: LayoutParams): Boolean {
        return manager.spanCount == params.spanSize
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
