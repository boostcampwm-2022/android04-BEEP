package com.lighthouse.presentation.ui.cropgifticon.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.view.MotionEvent
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.dp
import kotlin.math.max
import kotlin.math.min

class CropImageWindow(
    private val view: CropImageView,
    private val listener: OnCropImageWindowListener,
) {

    companion object {
        private val RECT_F_EMPTY = RectF()

        private const val DEFAULT_MARGIN_PERCENT = 0.1f

        private val CORNER_THICKNESS = 3f.dp
        private val CORNER_LENGTH = 12f.dp
        private val MIN_SIZE = (CORNER_LENGTH + CORNER_THICKNESS) * 2

        private val EDGE_TOUCH_RANGE = 24.dp
    }

    private var eventType = EventType.NONE
    private var touchRange: TouchRange = TouchRange.NONE

    private val backgroundPaint by lazy {
        Paint().apply {
            color = view.context.getColor(R.color.black_60)
        }
    }
    private val guidelinePaint by lazy {
        Paint().apply {
            color = view.context.getColor(R.color.gray_400)
            strokeWidth = 1f.dp
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }
    private val edgePaint by lazy {
        Paint().apply {
            color = view.context.getColor(R.color.white)
            strokeWidth = 1f.dp
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }
    private val cornerPaint by lazy {
        Paint().apply {
            color = view.context.getColor(R.color.primary)
            strokeWidth = CORNER_THICKNESS
            isAntiAlias = true
        }
    }

    val curCropRect = RectF()

    private var cropInitHorizontalMarginPercent = DEFAULT_MARGIN_PERCENT
    private var cropInitVerticalMarginPercent = DEFAULT_MARGIN_PERCENT

    fun initRect(
        bitmap: Bitmap? = null,
        croppedRect: RectF? = null,
    ) {
        if (bitmap != null) {
            if (croppedRect != null && croppedRect != RECT_F_EMPTY) {
                curCropRect.set(croppedRect)
            } else {
                if (view.enableAspectRatio) {
                    val aspectWidth =
                        min(bitmap.width.toFloat(), bitmap.height * view.aspectRatio)
                    val aspectHeight =
                        min(bitmap.width / view.aspectRatio, bitmap.height.toFloat())
                    val aspectOffsetX = (bitmap.width - aspectWidth) / 2
                    val aspectOffsetY = (bitmap.height - aspectHeight) / 2

                    curCropRect.set(
                        aspectOffsetX,
                        aspectOffsetY,
                        aspectOffsetX + aspectWidth,
                        aspectOffsetY + aspectHeight,
                    )
                } else {
                    curCropRect.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
                }
                val horizontalMargin = curCropRect.width() * cropInitHorizontalMarginPercent
                val verticalMargin = curCropRect.height() * cropInitVerticalMarginPercent
                curCropRect.inset(horizontalMargin, verticalMargin)
            }
        } else {
            curCropRect.set(RECT_F_EMPTY)
        }
    }

    private val touchBeforePos = PointF()
    private val touchDiff = PointF()

    private val calculateMinCropWidth
        get() = if (view.enableAspectRatio) {
            if (view.aspectRatio > 1f) MIN_SIZE * view.aspectRatio else MIN_SIZE
        } else {
            MIN_SIZE
        }

    private val calculateMinCropHeight
        get() = if (view.enableAspectRatio) {
            if (view.aspectRatio > 1f) MIN_SIZE else MIN_SIZE / view.aspectRatio
        } else {
            MIN_SIZE
        }

    fun onDraw(canvas: Canvas) {
        drawShadow(canvas)
        if (eventType != EventType.NONE) {
            drawGuidelines(canvas)
        }
        drawEdge(canvas)
        drawCorner(canvas)
    }

    private fun drawShadow(canvas: Canvas) {
        canvas.save()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(curCropRect)
        } else {
            canvas.clipRect(curCropRect, Region.Op.DIFFERENCE)
        }
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
        canvas.restore()
    }

    private fun drawGuidelines(canvas: Canvas) {
        val gapWidth = curCropRect.width() / 3
        val gapHeight = curCropRect.height() / 3

        val x1 = curCropRect.left + gapWidth
        val x2 = curCropRect.right - gapWidth
        canvas.drawLine(x1, curCropRect.top, x1, curCropRect.bottom, guidelinePaint)
        canvas.drawLine(x2, curCropRect.top, x2, curCropRect.bottom, guidelinePaint)

        val y1 = curCropRect.top + gapHeight
        val y2 = curCropRect.bottom - gapHeight
        canvas.drawLine(curCropRect.left, y1, curCropRect.right, y1, guidelinePaint)
        canvas.drawLine(curCropRect.left, y2, curCropRect.right, y2, guidelinePaint)
    }

    private fun drawEdge(canvas: Canvas) {
        val line = edgePaint.strokeWidth / 2
        canvas.drawRect(
            curCropRect.left + line,
            curCropRect.top + line,
            curCropRect.right - line,
            curCropRect.bottom - line,
            edgePaint,
        )
    }

    private fun drawCorner(canvas: Canvas) {
        val line = CORNER_THICKNESS / 2
        canvas.drawLine(
            curCropRect.left + CORNER_THICKNESS,
            curCropRect.top + line,
            curCropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            curCropRect.top + line,
            cornerPaint,
        )
        canvas.drawLine(
            curCropRect.left + line,
            curCropRect.top,
            curCropRect.left + line,
            curCropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint,
        )

        canvas.drawLine(
            curCropRect.right - CORNER_THICKNESS,
            curCropRect.top + line,
            curCropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            curCropRect.top + line,
            cornerPaint,
        )
        canvas.drawLine(
            curCropRect.right - line,
            curCropRect.top,
            curCropRect.right - line,
            curCropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint,
        )

        canvas.drawLine(
            curCropRect.left + CORNER_THICKNESS,
            curCropRect.bottom - line,
            curCropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            curCropRect.bottom - line,
            cornerPaint,
        )
        canvas.drawLine(
            curCropRect.left + line,
            curCropRect.bottom,
            curCropRect.left + line,
            curCropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint,
        )

        canvas.drawLine(
            curCropRect.right - CORNER_THICKNESS,
            curCropRect.bottom - line,
            curCropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            curCropRect.bottom - line,
            cornerPaint,
        )
        canvas.drawLine(
            curCropRect.right - line,
            curCropRect.bottom,
            curCropRect.right - line,
            curCropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint,
        )
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (isActivePointer(event).not()) {
            return false
        }

        setTouchPos(event)
        if (getEventType(event) == EventType.NONE) {
            return false
        }

        actionEvent()
        cleanEvent(event)
        return true
    }

    private var activePointerId: Int? = null

    private fun isActivePointer(event: MotionEvent): Boolean {
        val curPointerId = event.getPointerId(event.actionIndex)
        if (activePointerId == null) {
            activePointerId = curPointerId
        }
        return activePointerId == curPointerId
    }

    private fun setTouchPos(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchBeforePos.set(event.x, event.y)
        }
        touchDiff.set(event.x - touchBeforePos.x, event.y - touchBeforePos.y)
        touchBeforePos.set(event.x, event.y)
    }

    private fun getEventType(event: MotionEvent): EventType {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchRange = getTouchRange(event.x, event.y)
            eventType = when (touchRange) {
                TouchRange.CENTER -> EventType.MOVE
                TouchRange.NONE -> EventType.NONE
                else -> EventType.RESIZE
            }
        }
        return eventType
    }

    private fun getTouchRange(x: Float, y: Float): TouchRange {
        return when {
            containLeft(x, y) -> when {
                containTop(x, y) -> TouchRange.LEFT_TOP
                containBottom(x, y) -> TouchRange.LEFT_BOTTOM
                else -> TouchRange.LEFT
            }

            containRight(x, y) -> when {
                containTop(x, y) -> TouchRange.RIGHT_TOP
                containBottom(x, y) -> TouchRange.RIGHT_BOTTOM
                else -> TouchRange.RIGHT
            }

            containTop(x, y) -> TouchRange.TOP
            containBottom(x, y) -> TouchRange.BOTTOM
            containCenter(x, y) -> TouchRange.CENTER
            else -> TouchRange.NONE
        }
    }

    private fun containLeft(x: Float, y: Float): Boolean {
        val innerRange =
            min(max(curCropRect.width().toInt() - EDGE_TOUCH_RANGE * 2, 0), EDGE_TOUCH_RANGE)
        return x in curCropRect.left - EDGE_TOUCH_RANGE..curCropRect.left + innerRange &&
            y in curCropRect.top - EDGE_TOUCH_RANGE..curCropRect.bottom + EDGE_TOUCH_RANGE
    }

    private fun containRight(x: Float, y: Float): Boolean {
        val innerRange =
            min(max((curCropRect.width().toInt() - EDGE_TOUCH_RANGE * 2), 0), EDGE_TOUCH_RANGE)
        return x in curCropRect.right - innerRange..curCropRect.right + EDGE_TOUCH_RANGE &&
            y in curCropRect.top - EDGE_TOUCH_RANGE..curCropRect.bottom + EDGE_TOUCH_RANGE
    }

    private fun containTop(x: Float, y: Float): Boolean {
        val innerRange =
            min(max((curCropRect.height().toInt() - EDGE_TOUCH_RANGE * 2), 0), EDGE_TOUCH_RANGE)
        return x in curCropRect.left - EDGE_TOUCH_RANGE..curCropRect.right + EDGE_TOUCH_RANGE &&
            y in curCropRect.top - EDGE_TOUCH_RANGE..curCropRect.top + innerRange
    }

    private fun containBottom(x: Float, y: Float): Boolean {
        val innerRange =
            min(max((curCropRect.height().toInt() - EDGE_TOUCH_RANGE * 2), 0), EDGE_TOUCH_RANGE)
        return x in curCropRect.left - EDGE_TOUCH_RANGE..curCropRect.right + EDGE_TOUCH_RANGE &&
            y in curCropRect.bottom - innerRange..curCropRect.bottom + EDGE_TOUCH_RANGE
    }

    private fun containCenter(x: Float, y: Float) = curCropRect.contains(x, y)

    private fun actionEvent() {
        when (eventType) {
            EventType.RESIZE -> if (view.enableAspectRatio && view.aspectRatio > 0f) {
                resizeCropWithFixedAspectRatio()
            } else {
                resizeCropWithFreeAspectRatio()
            }

            EventType.MOVE -> moveCrop()
            else -> {}
        }
    }

    private fun resizeCropWithFixedAspectRatio() {
        when (touchRange) {
            TouchRange.LEFT_TOP -> resizeLeftTopWithAspectRatio(touchDiff)
            TouchRange.TOP -> resizeTopWithAspectRatio(touchDiff.y)
            TouchRange.RIGHT_TOP -> resizeRightTopWithAspectRatio(touchDiff)
            TouchRange.RIGHT -> resizeRightWithAspectRatio(touchDiff.x)
            TouchRange.RIGHT_BOTTOM -> resizeRightBottomWithAspectRatio(touchDiff)
            TouchRange.BOTTOM -> resizeBottomWithAspectRatio(touchDiff.y)
            TouchRange.LEFT_BOTTOM -> resizeLeftBottomWithAspectRatio(touchDiff)
            TouchRange.LEFT -> resizeLeftWithAspectRatio(touchDiff.x)
            else -> {}
        }
        view.invalidate()
    }

    private fun resizeLeftTopWithAspectRatio(diff: PointF) {
        if (calculateAspectRatio(
                min(curCropRect.left + diff.x, curCropRect.right - calculateMinCropWidth),
                min(curCropRect.top + diff.y, curCropRect.bottom - calculateMinCropHeight),
                curCropRect.right,
                curCropRect.bottom,
            ) < view.aspectRatio
        ) {
            resizeTop(diff.y, ResizeAddDir.LEFT)
            resizeLeftByAspectRatio()
        } else {
            resizeLeft(diff.x, ResizeAddDir.TOP)
            resizeTopByAspectRatio()
        }
    }

    private fun resizeTopWithAspectRatio(diffY: Float) {
        resizeTop(diffY, ResizeAddDir.HORIZONTAL)
        resizeHorizontalByAspectRatio()
    }

    private fun resizeRightTopWithAspectRatio(diff: PointF) {
        if (calculateAspectRatio(
                curCropRect.left,
                min(curCropRect.top + diff.y, curCropRect.bottom - calculateMinCropHeight),
                max(curCropRect.right + diff.x, curCropRect.left + calculateMinCropWidth),
                curCropRect.bottom,
            ) < view.aspectRatio
        ) {
            resizeTop(diff.y, ResizeAddDir.RIGHT)
            resizeRightByAspectRatio()
        } else {
            resizeRight(diff.x, ResizeAddDir.TOP)
            resizeTopByAspectRatio()
        }
    }

    private fun resizeRightWithAspectRatio(diffX: Float) {
        resizeRight(diffX, ResizeAddDir.VERTICAL)
        resizeVerticalByAspectRatio()
    }

    private fun resizeRightBottomWithAspectRatio(diff: PointF) {
        if (calculateAspectRatio(
                curCropRect.left,
                curCropRect.top,
                max(curCropRect.right + diff.x, curCropRect.left + calculateMinCropWidth),
                max(curCropRect.bottom + diff.y, curCropRect.top + calculateMinCropHeight),
            ) < view.aspectRatio
        ) {
            resizeBottom(diff.y, ResizeAddDir.RIGHT)
            resizeRightByAspectRatio()
        } else {
            resizeRight(diff.x, ResizeAddDir.BOTTOM)
            resizeBottomByAspectRatio()
        }
    }

    private fun resizeBottomWithAspectRatio(diffY: Float) {
        resizeBottom(diffY, ResizeAddDir.HORIZONTAL)
        resizeHorizontalByAspectRatio()
    }

    private fun resizeLeftBottomWithAspectRatio(diff: PointF) {
        if (calculateAspectRatio(
                min(curCropRect.left + diff.x, curCropRect.right - calculateMinCropWidth),
                curCropRect.top,
                curCropRect.right,
                max(curCropRect.bottom + diff.y, curCropRect.top + calculateMinCropHeight),
            ) < view.aspectRatio
        ) {
            resizeBottom(diff.y, ResizeAddDir.LEFT)
            resizeLeftByAspectRatio()
        } else {
            resizeLeft(diff.x, ResizeAddDir.BOTTOM)
            resizeBottomByAspectRatio()
        }
    }

    private fun resizeLeftWithAspectRatio(diffX: Float) {
        resizeLeft(diffX, ResizeAddDir.VERTICAL)
        resizeVerticalByAspectRatio()
    }

    private fun resizeLeft(diffX: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedLeft = curCropRect.left + diffX
        val boundRect = view.boundRect
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedLeft < boundRect.left) {
            resizedLeft = boundRect.left
        }
        if (resizedLeft > curCropRect.right - minCropWidth) {
            resizedLeft = curCropRect.right - minCropWidth
        }

        if (dir != ResizeAddDir.NONE) {
            var newHeight = (curCropRect.right - resizedLeft) / view.aspectRatio
            if (newHeight < minCropHeight) {
                resizedLeft =
                    max(boundRect.left, curCropRect.right - minCropHeight * view.aspectRatio)
                newHeight = (curCropRect.right - resizedLeft) / view.aspectRatio
            }

            when (dir) {
                ResizeAddDir.TOP -> {
                    if (newHeight > curCropRect.bottom - boundRect.top) {
                        resizedLeft = max(
                            boundRect.left,
                            curCropRect.right - (curCropRect.bottom - boundRect.top) * view.aspectRatio,
                        )
                    }
                }

                ResizeAddDir.BOTTOM -> {
                    if (newHeight > boundRect.bottom - curCropRect.top) {
                        resizedLeft =
                            maxOf(
                                resizedLeft,
                                boundRect.left,
                                curCropRect.right - (boundRect.bottom - curCropRect.top) * view.aspectRatio,
                            )
                    }
                }

                ResizeAddDir.VERTICAL -> {
                    resizedLeft =
                        maxOf(
                            resizedLeft,
                            boundRect.left,
                            curCropRect.right - (boundRect.bottom - boundRect.top) * view.aspectRatio,
                        )
                }

                else -> {}
            }
        }
        curCropRect.left = resizedLeft
    }

    private fun resizeRight(diffX: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedRight = curCropRect.right + diffX
        val boundRect = view.boundRect
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedRight > boundRect.right) {
            resizedRight = boundRect.right
        }
        if (resizedRight < curCropRect.left + minCropWidth) {
            resizedRight = curCropRect.left + minCropWidth
        }
        if (dir != ResizeAddDir.NONE) {
            var newHeight = (resizedRight - curCropRect.left) / view.aspectRatio
            if (newHeight < minCropHeight) {
                resizedRight =
                    min(boundRect.right, curCropRect.left + minCropHeight * view.aspectRatio)
                newHeight = (resizedRight - curCropRect.left) / view.aspectRatio
            }

            when (dir) {
                ResizeAddDir.TOP -> {
                    if (newHeight > curCropRect.bottom - boundRect.top) {
                        resizedRight = min(
                            boundRect.right,
                            curCropRect.left + (curCropRect.bottom - boundRect.top) * view.aspectRatio,
                        )
                    }
                }

                ResizeAddDir.BOTTOM -> {
                    if (newHeight > boundRect.bottom - curCropRect.top) {
                        resizedRight =
                            minOf(
                                resizedRight,
                                boundRect.right,
                                curCropRect.left + (boundRect.bottom - curCropRect.top) * view.aspectRatio,
                            )
                    }
                }

                ResizeAddDir.VERTICAL -> {
                    resizedRight =
                        minOf(
                            resizedRight,
                            boundRect.right,
                            curCropRect.left + (boundRect.bottom - boundRect.top) * view.aspectRatio,
                        )
                }

                else -> {}
            }
        }

        curCropRect.right = resizedRight
    }

    private fun resizeTop(diffY: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedTop = curCropRect.top + diffY
        val boundRect = view.boundRect
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedTop < boundRect.top) {
            resizedTop = boundRect.top
        }
        if (resizedTop > curCropRect.bottom - minCropHeight) {
            resizedTop = curCropRect.bottom - minCropHeight
        }
        if (dir != ResizeAddDir.NONE) {
            var newWidth = (curCropRect.bottom - resizedTop) * view.aspectRatio
            if (newWidth < minCropWidth) {
                resizedTop =
                    max(boundRect.top, curCropRect.bottom - minCropWidth / view.aspectRatio)
                newWidth = (curCropRect.bottom - resizedTop) * view.aspectRatio
            }

            when (dir) {
                ResizeAddDir.LEFT -> {
                    if (newWidth > curCropRect.right - boundRect.left) {
                        resizedTop = max(
                            boundRect.top,
                            curCropRect.bottom - (curCropRect.right - boundRect.left) / view.aspectRatio,
                        )
                    }
                }

                ResizeAddDir.RIGHT -> {
                    if (newWidth > boundRect.right - curCropRect.left) {
                        resizedTop =
                            maxOf(
                                resizedTop,
                                boundRect.top,
                                curCropRect.bottom - (boundRect.right - curCropRect.left) / view.aspectRatio,
                            )
                    }
                }

                ResizeAddDir.HORIZONTAL -> {
                    resizedTop =
                        maxOf(
                            resizedTop,
                            boundRect.top,
                            curCropRect.bottom - (boundRect.right - boundRect.left) / view.aspectRatio,
                        )
                }

                else -> {}
            }
        }
        curCropRect.top = resizedTop
    }

    private fun resizeBottom(diffY: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedBottom = curCropRect.bottom + diffY
        val boundRect = view.boundRect
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedBottom > boundRect.bottom) {
            resizedBottom = boundRect.bottom
        }
        if (resizedBottom < curCropRect.top + minCropHeight) {
            resizedBottom = curCropRect.top + minCropHeight
        }
        if (dir != ResizeAddDir.NONE) {
            var newWidth = (resizedBottom - curCropRect.top) * view.aspectRatio
            if (newWidth < minCropWidth) {
                resizedBottom =
                    min(boundRect.bottom, curCropRect.top + minCropWidth / view.aspectRatio)
                newWidth = (resizedBottom - curCropRect.top) * view.aspectRatio
            }

            when (dir) {
                ResizeAddDir.LEFT -> {
                    if (newWidth > curCropRect.right - boundRect.left) {
                        resizedBottom =
                            min(
                                boundRect.bottom,
                                curCropRect.top + (curCropRect.right - boundRect.left) / view.aspectRatio,
                            )
                    }
                }

                ResizeAddDir.RIGHT -> {
                    if (newWidth > boundRect.right - curCropRect.left) {
                        resizedBottom =
                            minOf(
                                resizedBottom,
                                boundRect.bottom,
                                curCropRect.top + (boundRect.right - curCropRect.left) / view.aspectRatio,
                            )
                    }
                }

                ResizeAddDir.HORIZONTAL -> {
                    resizedBottom =
                        minOf(
                            resizedBottom,
                            boundRect.bottom,
                            curCropRect.top + (boundRect.right - boundRect.left) / view.aspectRatio,
                        )
                }

                else -> {}
            }
        }
        curCropRect.bottom = resizedBottom
    }

    private fun calculateAspectRatio(left: Float, top: Float, right: Float, bottom: Float): Float {
        return (right - left) / (bottom - top)
    }

    private fun resizeLeftByAspectRatio() {
        curCropRect.left = curCropRect.right - curCropRect.height() * view.aspectRatio
    }

    private fun resizeTopByAspectRatio() {
        curCropRect.top = curCropRect.bottom - curCropRect.width() / view.aspectRatio
    }

    private fun resizeRightByAspectRatio() {
        curCropRect.right = curCropRect.left + curCropRect.height() * view.aspectRatio
    }

    private fun resizeBottomByAspectRatio() {
        curCropRect.bottom = curCropRect.top + curCropRect.width() / view.aspectRatio
    }

    private fun resizeHorizontalByAspectRatio() {
        curCropRect.inset((curCropRect.width() - curCropRect.height() * view.aspectRatio) / 2, 0f)
        val boundLeft = view.boundLeft
        val boundRight = view.boundRight
        if (curCropRect.left < boundLeft) {
            curCropRect.offset(boundLeft - curCropRect.left, 0f)
        }
        if (curCropRect.right > boundRight) {
            curCropRect.offset(boundRight - curCropRect.right, 0f)
        }
    }

    private fun resizeVerticalByAspectRatio() {
        curCropRect.inset(0f, (curCropRect.height() - curCropRect.width() / view.aspectRatio) / 2)
        val boundTop = view.boundTop
        val boundBottom = view.boundBottom
        if (curCropRect.top < boundTop) {
            curCropRect.offset(0f, boundTop - curCropRect.top)
        }
        if (curCropRect.bottom > boundBottom) {
            curCropRect.offset(0f, boundBottom - curCropRect.bottom)
        }
    }

    private fun resizeCropWithFreeAspectRatio() {
        val range = touchRange
        when (range) {
            TouchRange.LEFT,
            TouchRange.LEFT_TOP,
            TouchRange.LEFT_BOTTOM,
            -> resizeLeft(touchDiff.x)

            TouchRange.RIGHT,
            TouchRange.RIGHT_TOP,
            TouchRange.RIGHT_BOTTOM,
            -> resizeRight(touchDiff.x)

            else -> {}
        }

        when (range) {
            TouchRange.TOP,
            TouchRange.LEFT_TOP,
            TouchRange.RIGHT_TOP,
            -> resizeTop(touchDiff.y)

            TouchRange.BOTTOM,
            TouchRange.LEFT_BOTTOM,
            TouchRange.RIGHT_BOTTOM,
            -> resizeBottom(touchDiff.y)

            else -> {}
        }
        view.invalidate()
    }

    private fun moveCrop() {
        val boundRect = view.boundRect

        val consumedX = when {
            curCropRect.left + touchDiff.x < boundRect.left -> boundRect.left - curCropRect.left
            curCropRect.right + touchDiff.x > boundRect.right -> boundRect.right - curCropRect.right
            else -> touchDiff.x
        }
        val consumedY = when {
            curCropRect.top + touchDiff.y < boundRect.top -> boundRect.top - curCropRect.top
            curCropRect.bottom + touchDiff.y > boundRect.bottom -> boundRect.bottom - curCropRect.bottom
            else -> touchDiff.y
        }
        val unconsumedX = touchDiff.x - consumedX
        val unconsumedY = touchDiff.y - consumedY

        curCropRect.offset(consumedX, consumedY)
        listener.onWindowMove(unconsumedX, unconsumedY, boundRect)
        view.invalidate()
    }

    private fun cleanEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP,
            -> {
                if (eventType == EventType.RESIZE) {
                    listener.onWindowResized()
                }

                eventType = EventType.NONE
                activePointerId = null

                listener.onWindowTouchComplete(curCropRect)
            }
        }
    }

    enum class TouchRange {
        NONE, LEFT, LEFT_TOP, TOP, RIGHT_TOP, RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, CENTER
    }

    enum class EventType {
        NONE, MOVE, RESIZE
    }

    enum class ResizeAddDir {
        NONE, LEFT, TOP, RIGHT, BOTTOM, VERTICAL, HORIZONTAL
    }
}
