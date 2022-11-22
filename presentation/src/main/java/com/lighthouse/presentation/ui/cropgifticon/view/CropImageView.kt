package com.lighthouse.presentation.ui.cropgifticon.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.graphics.minus
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.getBitmap
import kotlin.math.max
import kotlin.math.min

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

    private var originBitmap: Bitmap? = null

    // 실제 이미지의 크기에 맞는 Rect
    private val realImageRect = RectF()
    private val realCropRect = RectF()

    // 현재 화면에 그려지고 있는 Rect, Matrix
    private val currentImageRect = RectF()
    private val currentCropRect = RectF()

    private val mainMatrix = Matrix()
    private val mainInverseMatrix = Matrix()

    private var zoom = 1f

    private var eventType = EventType.NONE
    private var touchRange: TouchRange? = null

    private val touchCropRect = RectF()
    private val touchStartPos = PointF()
    private val touchBeforePos = PointF()
    private val touchEndPos = PointF()

    private val cropZoomAnimation = object : Animation() {
        private val startCropRect = RectF()
        private val endCropRect = RectF()

        private val startImageRect = RectF()
        private val endImageRect = RectF()

        private val startMatrixPoints = FloatArray(9)
        private val endMatrixPoints = FloatArray(9)

        private val animCropRect = RectF()
        private val animImageRect = RectF()
        private val animMatrixPoints = FloatArray(9)

        init {
            duration = 300
            fillAfter = true
            interpolator = AccelerateDecelerateInterpolator()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            animCropRect.set(
                startCropRect.left + (endCropRect.left - startCropRect.left) * interpolatedTime,
                startCropRect.top + (endCropRect.top - startCropRect.top) * interpolatedTime,
                startCropRect.right + (endCropRect.right - startCropRect.right) * interpolatedTime,
                startCropRect.bottom + (endCropRect.bottom - startCropRect.bottom) * interpolatedTime
            )

            animImageRect.set(
                startImageRect.left + (endImageRect.left - startImageRect.left) * interpolatedTime,
                startImageRect.top + (endImageRect.top - startImageRect.top) * interpolatedTime,
                startImageRect.right + (endImageRect.right - startImageRect.right) * interpolatedTime,
                startImageRect.bottom + (endImageRect.bottom - startImageRect.bottom) * interpolatedTime
            )

            for (i in animMatrixPoints.indices) {
                animMatrixPoints[i] =
                    startMatrixPoints[i] + (endMatrixPoints[i] - startMatrixPoints[i]) * interpolatedTime
            }

            currentCropRect.set(animCropRect)
            currentImageRect.set(animImageRect)
            mainMatrix.setValues(animMatrixPoints)
            invalidate()
        }

        fun setStartState(cropRect: RectF, imageRect: RectF, imageMatrix: Matrix) {
            reset()
            startCropRect.set(cropRect)
            startImageRect.set(imageRect)
            imageMatrix.getValues(startMatrixPoints)
        }

        fun setEndState(cropRect: RectF, imageRectF: RectF, imageMatrix: Matrix) {
            endCropRect.set(cropRect)
            endImageRect.set(imageRectF)
            imageMatrix.getValues(endMatrixPoints)
        }
    }

    fun setOriginUri(uri: Uri) {
        originBitmap = when (uri.scheme) {
            SCHEME_CONTENT -> context.contentResolver.getBitmap(uri)
            SCHEME_FILE -> BitmapFactory.decodeFile(uri.path)
            else -> null
        }

        initRect()
    }

    // 새로운 이미지 등록시, Rect 초기화
    private fun initRect() {
        mainMatrix.reset()

        val bitmap = originBitmap
        if (bitmap != null) {
            realImageRect.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        } else {
            realImageRect.set(RECTF_EMPTY)
        }
        currentImageRect.set(realImageRect)
        realCropRect.set(realImageRect)
        currentCropRect.set(realImageRect)
    }

    private fun applyZoom() {
        val cropWidth = currentCropRect.width()
        val cropHeight = currentCropRect.height()

        var newZoom = zoom
        /*
         * CropWindow 의 가로와 세로가 화면의 50% 이하의 크기가 된다면,
         * CropWindow 은 (1 <= a < 2) 의 값을 곱해도 화면 보다 크지 않기 때문에 scale 이 1 이상이 된다.
         * 1.5 를 곱해 주는 이유는 한번에 너무 많이 커지는 것이 부담 스럽기 때문이다
         */
        if (zoom < MAX_ZOOM && cropWidth < width * 0.5f && cropHeight < height * 0.5f) {
            val scaleW = width / cropWidth * 0.66f * zoom
            val scaleH = height / cropHeight * 0.66f * zoom
            newZoom = minOf(scaleW, scaleH, MAX_ZOOM)
        }
        /*
         * CropWindow 의 가로와 세로가 화면의 66% 보다 커진다면,
         * CropWindow 은 (a > 1.5) 을 곱한다면 scale 이 1 이하가 되게 된다.
         * 2 를 곱하 면서 천천히 Zoom을 감소 시킨다
         */
        if (zoom > MIN_ZOOM && cropWidth > width * 0.66f || cropHeight > height * 0.66f) {
            val scaleW = width / cropWidth * 0.5f * zoom
            val scaleH = height / cropHeight * 0.5f * zoom
            newZoom = max(min(scaleW, scaleH), MIN_ZOOM)
        }

        if (newZoom != zoom) {
            cropZoomAnimation.setStartState(currentCropRect, currentImageRect, mainMatrix)
            zoom = newZoom
            applyMatrix(animate = true)
        }
    }

    private fun applyMatrix(animate: Boolean) {
        val bitmap = originBitmap
        if (width == 0 || height == 0 || bitmap == null) {
            return
        }

        // 1. 역 행렬을 이용 하여 처음 보였던 이미지를 기준으로 변경한다
        mainMatrix.invert(mainInverseMatrix)
        mainInverseMatrix.mapRect(currentImageRect)
        mainInverseMatrix.mapRect(currentCropRect)
        mainMatrix.reset()

        // 2. 이미지를 화면에 맞게 키운다
        val wScale = width / bitmap.width.toFloat()
        val hScale = height / bitmap.height.toFloat()
        val scale = min(wScale, hScale)
        mainMatrix.postScale(scale, scale)
        mapCurrentImageRectByMatrix()

        // 3. 변경된 이미지를 화면의 가운데로 이동
        val offsetX = (width - currentImageRect.width()) / 2
        val offsetY = (height - currentImageRect.height()) / 2
        mainMatrix.postTranslate(offsetX, offsetY)

        // 4. 행렬에 Zoom 연산 추가
        mainMatrix.postScale(zoom, zoom, width / 2f, height / 2f)
        mainMatrix.mapRect(currentCropRect)
        mapCurrentImageRectByMatrix()

        // 6. ZoomOffset 구하기
        val zoomOffsetX = when {
            width > currentImageRect.width() -> 0f
            else -> max(
                min(width / 2 - currentCropRect.centerX(), -currentImageRect.left),
                width - currentImageRect.right
            )
        }
        val zoomOffsetY = when {
            height > currentImageRect.height() -> 0f
            else -> max(
                min(height / 2 - currentCropRect.centerY(), -currentImageRect.top),
                height - currentImageRect.bottom
            )
        }

        mainMatrix.postTranslate(zoomOffsetX, zoomOffsetY)
        currentCropRect.offset(zoomOffsetX, zoomOffsetY)
        mapCurrentImageRectByMatrix()

        if (animate) {
            cropZoomAnimation.setEndState(currentCropRect, currentImageRect, mainMatrix)
            startAnimation(cropZoomAnimation)
        } else {
            invalidate()
        }
    }

    private fun mapCurrentImageRectByMatrix() {
        currentImageRect.set(realImageRect)
        mainMatrix.mapRect(currentImageRect)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        applyMatrix(animate = false)
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = originBitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, mainMatrix, null)
            drawShadow(canvas)
            if (eventType != EventType.NONE) {
                drawGuidelines(canvas)
            }
            drawEdge(canvas)
            drawCorner(canvas)
        }
    }

    private fun drawShadow(canvas: Canvas) {
        canvas.save()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(currentCropRect)
        } else {
            canvas.clipRect(currentCropRect, Region.Op.DIFFERENCE)
        }
        canvas.drawRect(currentImageRect, backgroundPaint)
        canvas.restore()
    }

    private fun drawGuidelines(canvas: Canvas) {
        val gapWidth = currentCropRect.width() / 3
        val gapHeight = currentCropRect.height() / 3

        val x1 = currentCropRect.left + gapWidth
        val x2 = currentCropRect.right - gapWidth
        canvas.drawLine(x1, currentCropRect.top, x1, currentCropRect.bottom, guidelinePaint)
        canvas.drawLine(x2, currentCropRect.top, x2, currentCropRect.bottom, guidelinePaint)

        val y1 = currentCropRect.top + gapHeight
        val y2 = currentCropRect.bottom - gapHeight
        canvas.drawLine(currentCropRect.left, y1, currentCropRect.right, y1, guidelinePaint)
        canvas.drawLine(currentCropRect.left, y2, currentCropRect.right, y2, guidelinePaint)
    }

    private fun drawEdge(canvas: Canvas) {
        val line = edgePaint.strokeWidth / 2
        canvas.drawRect(
            currentCropRect.left + line,
            currentCropRect.top + line,
            currentCropRect.right - line,
            currentCropRect.bottom - line,
            edgePaint
        )
    }

    private fun drawCorner(canvas: Canvas) {
        val line = CORNER_THICKNESS / 2
        canvas.drawLine(
            currentCropRect.left + CORNER_THICKNESS,
            currentCropRect.top + line,
            currentCropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            currentCropRect.top + line,
            cornerPaint
        )
        canvas.drawLine(
            currentCropRect.left + line,
            currentCropRect.top,
            currentCropRect.left + line,
            currentCropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            currentCropRect.right - CORNER_THICKNESS,
            currentCropRect.top + line,
            currentCropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            currentCropRect.top + line,
            cornerPaint
        )
        canvas.drawLine(
            currentCropRect.right - line,
            currentCropRect.top,
            currentCropRect.right - line,
            currentCropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            currentCropRect.left + CORNER_THICKNESS,
            currentCropRect.bottom - line,
            currentCropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            currentCropRect.bottom - line,
            cornerPaint
        )
        canvas.drawLine(
            currentCropRect.left + line,
            currentCropRect.bottom,
            currentCropRect.left + line,
            currentCropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            currentCropRect.right - CORNER_THICKNESS,
            currentCropRect.bottom - line,
            currentCropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            currentCropRect.bottom - line,
            cornerPaint
        )
        canvas.drawLine(
            currentCropRect.right - line,
            currentCropRect.bottom,
            currentCropRect.right - line,
            currentCropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        setTouchPos(event)
        if (getEventType(event) == EventType.NONE) {
            return false
        }

        actionEvent()
        cleanEvent(event)
        return true
    }

    private fun setTouchPos(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchStartPos.set(event.x, event.y)
            touchBeforePos.set(event.x, event.y)

            touchCropRect.set(currentCropRect)
        }
        touchEndPos.set(event.x, event.y)
    }

    private fun getEventType(event: MotionEvent): EventType {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchRange = getTouchRange(event.x, event.y)
            eventType = when (touchRange) {
                TouchRange.CENTER -> EventType.MOVE
                null -> EventType.NONE
                else -> EventType.RESIZE
            }
        }
        return eventType
    }

    private fun actionEvent() {
        when (eventType) {
            EventType.RESIZE -> resizeCrop()
            EventType.MOVE -> moveCrop()
            else -> {}
        }
    }

    private fun cleanEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                if (eventType == EventType.RESIZE) {
                    applyZoom()
                }
                eventType = EventType.NONE
            }
            else -> {
                touchBeforePos.set(touchEndPos)
            }
        }
    }

    private fun getTouchRange(x: Float, y: Float): TouchRange? {
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
            else -> null
        }
    }

    private fun containLeft(x: Float, y: Float) =
        x in currentCropRect.left - EDGE_TOUCH_RANGE..currentCropRect.left + EDGE_TOUCH_RANGE &&
            y in currentCropRect.top - EDGE_TOUCH_RANGE..currentCropRect.bottom + EDGE_TOUCH_RANGE

    private fun containRight(x: Float, y: Float) =
        x in currentCropRect.right - EDGE_TOUCH_RANGE..currentCropRect.right + EDGE_TOUCH_RANGE &&
            y in currentCropRect.top - EDGE_TOUCH_RANGE..currentCropRect.bottom + EDGE_TOUCH_RANGE

    private fun containTop(x: Float, y: Float) =
        x in currentCropRect.left - EDGE_TOUCH_RANGE..currentCropRect.right + EDGE_TOUCH_RANGE &&
            y in currentCropRect.top - EDGE_TOUCH_RANGE..currentCropRect.top + EDGE_TOUCH_RANGE

    private fun containBottom(x: Float, y: Float) =
        x in currentCropRect.left - EDGE_TOUCH_RANGE..currentCropRect.right + EDGE_TOUCH_RANGE &&
            y in currentCropRect.bottom - EDGE_TOUCH_RANGE..currentCropRect.bottom + EDGE_TOUCH_RANGE

    private fun containCenter(x: Float, y: Float) = currentCropRect.contains(x, y)

    private fun moveCrop() {
        val diff = touchEndPos.minus(touchBeforePos)
        val offsetX = when {
            currentCropRect.left + diff.x < currentImageRect.left + SNAP_RADIUS -> currentImageRect.left - currentCropRect.left
            currentCropRect.right + diff.x > currentImageRect.right - SNAP_RADIUS -> currentImageRect.right - currentCropRect.right
            else -> diff.x
        }

        val offsetY = when {
            currentCropRect.top + diff.y < currentImageRect.top + SNAP_RADIUS -> currentImageRect.top - currentCropRect.top
            currentCropRect.bottom + diff.y > currentImageRect.bottom - SNAP_RADIUS -> currentImageRect.bottom - currentCropRect.bottom
            else -> diff.y
        }
        val screenMoveX = when {
            currentCropRect.left + offsetX < 0f -> -(currentCropRect.left + offsetX)
            currentCropRect.right + offsetX > width -> width - (currentCropRect.right + offsetX)
            else -> 0f
        }
        val screenMoveY = when {
            currentCropRect.top + offsetY < 0f -> -(currentCropRect.top + offsetY)
            currentCropRect.bottom + offsetY > height -> height - (currentCropRect.bottom + offsetY)
            else -> 0f
        }

        currentCropRect.offset(offsetX + screenMoveX, offsetY + screenMoveY)
        mainMatrix.postTranslate(screenMoveX * 2, screenMoveY * 2)
        mapCurrentImageRectByMatrix()
        invalidate()
    }

    private fun resizeCrop() {
        val range = touchRange ?: return
        val diff = touchEndPos.minus(touchStartPos)

        when (range) {
            TouchRange.LEFT, TouchRange.LEFT_TOP, TouchRange.LEFT_BOTTOM -> resizeLeft(diff.x)
            TouchRange.RIGHT, TouchRange.RIGHT_TOP, TouchRange.RIGHT_BOTTOM -> resizeRight(diff.x)
            else -> {}
        }

        when (range) {
            TouchRange.TOP, TouchRange.LEFT_TOP, TouchRange.RIGHT_TOP -> resizeTop(diff.y)
            TouchRange.BOTTOM, TouchRange.LEFT_BOTTOM, TouchRange.RIGHT_BOTTOM -> resizeBottom(diff.y)
            else -> {}
        }
        invalidate()
    }

    private fun resizeLeft(diffX: Float) {
        val resizedLeft = touchCropRect.left + diffX
        currentCropRect.left = when {
            resizedLeft < 0f -> 0f
            resizedLeft > currentCropRect.right - MIN_SIZE -> touchCropRect.right - MIN_SIZE
            else -> resizedLeft
        }
    }

    private fun resizeRight(diffX: Float) {
        val resizedRight = touchCropRect.right + diffX
        currentCropRect.right = when {
            resizedRight > width -> width.toFloat()
            resizedRight < currentCropRect.left + MIN_SIZE -> touchCropRect.left + MIN_SIZE
            else -> resizedRight
        }
    }

    private fun resizeTop(diffY: Float) {
        val resizedTop = touchCropRect.top + diffY
        currentCropRect.top = when {
            resizedTop < 0f -> 0f
            resizedTop > currentCropRect.bottom - MIN_SIZE -> touchCropRect.bottom - MIN_SIZE
            else -> resizedTop
        }
    }

    private fun resizeBottom(diffY: Float) {
        val resizedBottom = touchCropRect.bottom + diffY
        currentCropRect.bottom = when {
            resizedBottom > height -> height.toFloat()
            resizedBottom < currentCropRect.top + MIN_SIZE -> touchCropRect.top + MIN_SIZE
            else -> resizedBottom
        }
    }

    enum class TouchRange {
        LEFT, LEFT_TOP, TOP, RIGHT_TOP, RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, CENTER
    }

    enum class EventType {
        NONE, MOVE, RESIZE
    }

    companion object {
        private val RECTF_EMPTY = RectF()

        private const val SCHEME_CONTENT = "content"
        private const val SCHEME_FILE = "file"

        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 4f

        private val CORNER_THICKNESS = 3.dp
        private val CORNER_LENGTH = 24.dp.toInt()
        private val MIN_SIZE = (CORNER_LENGTH + CORNER_THICKNESS) * 2
        private val EDGE_TOUCH_RANGE = 24.dp.toInt()
        private val SNAP_RADIUS = 3.dp
    }
}
