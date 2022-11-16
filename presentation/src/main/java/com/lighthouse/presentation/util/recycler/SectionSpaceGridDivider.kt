package com.lighthouse.presentation.util.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SectionSpaceGridDivider(
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
        super.getItemOffsets(outRect, view, parent, state)

        val manager = parent.layoutManager as? GridLayoutManager ?: return
        val params = view.layoutParams as? GridLayoutManager.LayoutParams ?: return

        val position = parent.getChildAdapterPosition(view)

        outRect.top = if (isSection(manager, params)) {
            if (isFirstRow(position)) top else sectionDivider - itemSpace / 2
        } else {
            itemSpace / 2
        }

        outRect.bottom = if (isLastRow(position, manager, parent)) bottom else itemSpace / 2

        outRect.left = if (isFirstCol(params)) start else itemSpace / 2

        outRect.right = if (isLastCol(manager, params)) end else itemSpace / 2
    }

    private fun isSection(manager: GridLayoutManager, params: GridLayoutManager.LayoutParams): Boolean {
        return manager.spanCount == params.spanSize
    }

    private fun isFirstRow(position: Int): Boolean {
        return position == 0
    }

    private fun isLastRow(position: Int, manager: GridLayoutManager, recyclerView: RecyclerView): Boolean {
        val itemCount = recyclerView.adapter?.itemCount ?: 0

        return getGroupIndex(position, manager) == getGroupIndex(itemCount - 1, manager)
    }

    private fun getGroupIndex(position: Int, manager: GridLayoutManager): Int {
        return manager.spanSizeLookup.getSpanGroupIndex(position, manager.spanCount)
    }

    private fun isFirstCol(params: GridLayoutManager.LayoutParams): Boolean {
        return params.spanIndex == 0
    }

    private fun isLastCol(manager: GridLayoutManager, params: GridLayoutManager.LayoutParams): Boolean {
        return params.spanIndex + params.spanSize == manager.spanCount
    }
}
