package com.lighthouse.presentation.util.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSectionSpaceItemDecoration(
    private val sectionDivider: Int,
    private val itemSpace: Int,
    private val start: Int = 0,
    private val top: Int = 0,
    private val end: Int = 0,
    private val bottom: Int = 0
) : RecyclerView.ItemDecoration() {

    constructor(
        sectionDivider: Float,
        itemSpace: Float,
        start: Float = 0f,
        top: Float = 0f,
        end: Float = 0f,
        bottom: Float = 0f
    ) : this(sectionDivider.toInt(), itemSpace.toInt(), start.toInt(), top.toInt(), end.toInt(), bottom.toInt())

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val manager = parent.layoutManager as? GridLayoutManager ?: return
        val params = view.layoutParams as? GridLayoutManager.LayoutParams ?: return

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        outRect.left = if (isFirstCol(params)) start else itemSpace / 2
        outRect.top = if (isSection(manager, params)) {
            if (isFirstRow(manager, position)) top else sectionDivider - itemSpace / 2
        } else {
            itemSpace / 2
        }
        outRect.right = if (isLastCol(manager, params)) end else itemSpace / 2
        outRect.bottom = if (isLastRow(manager, position, itemCount)) bottom else itemSpace / 2
    }

    private fun isSection(manager: GridLayoutManager, params: GridLayoutManager.LayoutParams): Boolean {
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

    private fun isFirstCol(params: GridLayoutManager.LayoutParams): Boolean {
        return params.spanIndex == 0
    }

    private fun isLastCol(manager: GridLayoutManager, params: GridLayoutManager.LayoutParams): Boolean {
        return params.spanIndex + params.spanSize == manager.spanCount
    }
}
