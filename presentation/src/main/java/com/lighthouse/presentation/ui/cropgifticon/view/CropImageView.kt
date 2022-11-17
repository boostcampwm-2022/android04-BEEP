package com.lighthouse.presentation.ui.cropgifticon.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.minus
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.dp

class CropImageView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val backgroundPaint by lazy {
        Paint().apply {
            color = context.getColor(R.color.black_a60)
        }
    }
    private val guidelinePaint by lazy {
        Paint().apply {
            color = context.getColor(R.color.gray_400)
            strokeWidth = 1.dp
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }
    private val edgePaint by lazy {
        Paint().apply {
            color = context.getColor(R.color.white)
            strokeWidth = 1.dp
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    private val cornerPaint by lazy {
        Paint().apply {
            color = context.getColor(R.color.primary)
            strokeWidth = CORNER_THICKNESS
            isAntiAlias = true
        }
    }

    private val cropRect = RectF()
    private val viewRect = RectF()

    private var eventType = EventType.None

    private var touchRange: TouchRange? = null
    private val cropBaseRect = RectF()

    private val touchStartPos = PointF()
    private val touchEndPos = PointF()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val wm = width.toFloat() / 10
        val hm = height.toFloat() / 10
        viewRect.set(0f, 0f, width.toFloat(), height.toFloat())
        cropRect.set(wm, hm, width.toFloat() - wm, height.toFloat() - hm)
        cropBaseRect.set(cropRect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawShadow(canvas)
        drawGuidelines(canvas)
        drawEdge(canvas)
        drawCorner(canvas)
    }

    private fun drawShadow(canvas: Canvas) {
        canvas.drawRect(viewRect.left, viewRect.top, viewRect.right, cropRect.top, backgroundPaint)
        canvas.drawRect(viewRect.left, cropRect.top, cropRect.left, cropRect.bottom, backgroundPaint)
        canvas.drawRect(cropRect.right, cropRect.top, viewRect.right, cropRect.bottom, backgroundPaint)
        canvas.drawRect(viewRect.left, cropRect.bottom, viewRect.right, viewRect.bottom, backgroundPaint)
    }

    private fun drawGuidelines(canvas: Canvas) {
        val gapWidth = cropRect.width() / 3
        val gapHeight = cropRect.height() / 3

        val x1 = cropRect.left + gapWidth
        val x2 = cropRect.right - gapWidth
        canvas.drawLine(x1, cropRect.top, x1, cropRect.bottom, guidelinePaint)
        canvas.drawLine(x2, cropRect.top, x2, cropRect.bottom, guidelinePaint)

        val y1 = cropRect.top + gapHeight
        val y2 = cropRect.bottom - gapHeight
        canvas.drawLine(cropRect.left, y1, cropRect.right, y1, guidelinePaint)
        canvas.drawLine(cropRect.left, y2, cropRect.right, y2, guidelinePaint)
    }

    private fun drawEdge(canvas: Canvas) {
        val line = edgePaint.strokeWidth / 2
        canvas.drawRect(
            cropRect.left + line,
            cropRect.top + line,
            cropRect.right - line,
            cropRect.bottom - line,
            edgePaint
        )
    }

    private fun drawCorner(canvas: Canvas) {
        val line = CORNER_THICKNESS / 2
        canvas.drawLine(
            cropRect.left + CORNER_THICKNESS,
            cropRect.top + line,
            cropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            cropRect.top + line,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.left + line,
            cropRect.top,
            cropRect.left + line,
            cropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            cropRect.right - CORNER_THICKNESS,
            cropRect.top + line,
            cropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            cropRect.top + line,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.right - line,
            cropRect.top,
            cropRect.right - line,
            cropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            cropRect.left + CORNER_THICKNESS,
            cropRect.bottom - line,
            cropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            cropRect.bottom - line,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.left + line,
            cropRect.bottom,
            cropRect.left + line,
            cropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            cropRect.right - CORNER_THICKNESS,
            cropRect.bottom - line,
            cropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            cropRect.bottom - line,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.right - line,
            cropRect.bottom,
            cropRect.right - line,
            cropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        setTouchPos(event)
        if (getEventType(event) == EventType.None) {
            return false
        }

        actionEvent()
        cleanEvent(event)
        return true
    }

    private fun setTouchPos(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchStartPos.set(event.x, event.y)
            cropBaseRect.set(cropRect)
        }
        touchEndPos.set(event.x, event.y)
    }

    private fun getEventType(event: MotionEvent): EventType {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchRange = getTouchRange(event.x, event.y)
            eventType = when (touchRange) {
                TouchRange.C -> EventType.Move
                null -> EventType.None
                else -> EventType.Resize
            }
        }
        return eventType
    }

    private fun actionEvent() {
        when (eventType) {
            EventType.Resize -> resizeCrop()
            EventType.Move -> moveCrop()
            else -> {}
        }
    }

    private fun cleanEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                eventType = EventType.None
            }
            else -> {}
        }
    }

    private fun getTouchRange(x: Float, y: Float): TouchRange? {
        return when {
            containL(x, y) -> when {
                containT(x, y) -> TouchRange.LT
                containB(x, y) -> TouchRange.LB
                else -> TouchRange.L
            }
            containR(x, y) -> when {
                containT(x, y) -> TouchRange.RT
                containB(x, y) -> TouchRange.RB
                else -> TouchRange.R
            }
            containT(x, y) -> TouchRange.T
            containB(x, y) -> TouchRange.B
            containC(x, y) -> TouchRange.C
            else -> null
        }
    }

    private fun containL(x: Float, y: Float) =
        x in cropRect.left - EDGE_TOUCH_RANGE..cropRect.left + EDGE_TOUCH_RANGE &&
            y in cropRect.top - EDGE_TOUCH_RANGE..cropRect.bottom + EDGE_TOUCH_RANGE

    private fun containR(x: Float, y: Float) =
        x in cropRect.right - EDGE_TOUCH_RANGE..cropRect.right + EDGE_TOUCH_RANGE &&
            y in cropRect.top - EDGE_TOUCH_RANGE..cropRect.bottom + EDGE_TOUCH_RANGE

    private fun containT(x: Float, y: Float) =
        x in cropRect.left - EDGE_TOUCH_RANGE..cropRect.right + EDGE_TOUCH_RANGE &&
            y in cropRect.top - EDGE_TOUCH_RANGE..cropRect.top + EDGE_TOUCH_RANGE

    private fun containB(x: Float, y: Float) =
        x in cropRect.left - EDGE_TOUCH_RANGE..cropRect.right + EDGE_TOUCH_RANGE &&
            y in cropRect.bottom - EDGE_TOUCH_RANGE..cropRect.bottom + EDGE_TOUCH_RANGE

    private fun containC(x: Float, y: Float) = cropRect.contains(x, y)

    private fun moveCrop() {
        val diff = touchEndPos.minus(touchStartPos)

        val offsetX = when {
            cropBaseRect.left + diff.x < viewRect.left -> -cropBaseRect.left
            cropBaseRect.right + diff.x > viewRect.right -> viewRect.right - cropBaseRect.right
            else -> diff.x
        }
        val offsetY = when {
            cropBaseRect.top + diff.y < viewRect.top -> -cropBaseRect.top
            cropBaseRect.bottom + diff.y > viewRect.bottom -> viewRect.bottom - cropBaseRect.bottom
            else -> diff.y
        }

        cropRect.apply {
            set(cropBaseRect)
            offset(offsetX, offsetY)
        }
        invalidate()
    }

    private fun resizeCrop() {
        val range = touchRange ?: return
        val diff = touchEndPos.minus(touchStartPos)

        when (range) {
            TouchRange.L, TouchRange.LT, TouchRange.LB -> resizeLeft(diff.x)
            TouchRange.R, TouchRange.RT, TouchRange.RB -> resizeRight(diff.x)
            else -> {}
        }

        when (range) {
            TouchRange.T, TouchRange.LT, TouchRange.RT -> resizeTop(diff.y)
            TouchRange.B, TouchRange.LB, TouchRange.RB -> resizeBottom(diff.y)
            else -> {}
        }

        invalidate()
    }

    private fun resizeLeft(diffX: Float) {
        val movedLeft = cropBaseRect.left + diffX
        cropRect.left = when {
            movedLeft < viewRect.left -> viewRect.left
            movedLeft > cropRect.right - MIN_SIZE -> cropBaseRect.right - MIN_SIZE
            else -> movedLeft
        }
    }

    private fun resizeRight(diffX: Float) {
        val movedRight = cropBaseRect.right + diffX
        cropRect.right = when {
            movedRight > viewRect.right -> viewRect.right
            movedRight < cropRect.left + MIN_SIZE -> cropBaseRect.left + MIN_SIZE
            else -> movedRight
        }
    }

    private fun resizeTop(diffY: Float) {
        val movedTop = cropBaseRect.top + diffY
        cropRect.top = when {
            movedTop < viewRect.top -> viewRect.top
            movedTop > cropRect.bottom - MIN_SIZE -> cropBaseRect.bottom - MIN_SIZE
            else -> movedTop
        }
    }

    private fun resizeBottom(diffY: Float) {
        val movedBottom = cropBaseRect.bottom + diffY
        cropRect.bottom = when {
            movedBottom > viewRect.bottom -> viewRect.bottom
            movedBottom < cropRect.top + MIN_SIZE -> cropBaseRect.top + MIN_SIZE
            else -> movedBottom
        }
    }

    enum class TouchRange {
        L, LT, T, RT, R, RB, B, LB, C
    }

    enum class EventType {
        None, Move, Resize
    }

    companion object {
        private val CORNER_THICKNESS = 3.dp
        private val CORNER_LENGTH = 24.dp.toInt()
        private val MIN_SIZE = (CORNER_LENGTH + CORNER_THICKNESS) * 2
        private val EDGE_TOUCH_RANGE = 15.dp.toInt()
    }
}
