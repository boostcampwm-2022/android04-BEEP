package com.lighthouse.presentation.ui.cropgifticon.view

import android.annotation.SuppressLint
import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver.SCHEME_FILE
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class CropImageView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val backgroundPaint by lazy {
        Paint().apply {
            color = context.getColor(R.color.black_60)
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

    // ?????? ???????????? ????????? ?????? Rect
    private val realImageRect = RectF()

    // ?????? ????????? ???????????? ?????? Rect, Matrix
    private val curImageRect = RectF()
    private val curCropRect = RectF()

    private val mainMatrix = Matrix()
    private val mainInverseMatrix = Matrix()

    private var zoom = 1f

    // AspectRatio = Width / Height
    var enableAspectRatio = true
    var aspectRatio = 1f

    private var eventType = EventType.NONE
    private var touchRange: TouchRange? = null

    private val touchMatrix = Matrix()
    private val touchImageRect = RectF()
    private val touchCropRect = RectF()
    private val touchStartPos = PointF()
    private val touchEndPos = PointF()

    // CropRect ??? ????????? ??? ?????? ?????? ??????
    private val boundLeft: Float
        get() = max(curImageRect.left, 0f)

    private val boundTop: Float
        get() = max(curImageRect.top, 0f)

    private val boundRight: Float
        get() = min(curImageRect.right, width.toFloat())

    private val boundBottom: Float
        get() = min(curImageRect.bottom, height.toFloat())

    private var cropInitHorizontalMarginPercent = DEFAULT_MARGIN_PERCENT
    private var cropInitVerticalMarginPercent = DEFAULT_MARGIN_PERCENT

    // CropRect ??? ?????? ??? ?????? ?????? ??????
    private val calculateMinCropWidth
        get() = if (enableAspectRatio) {
            if (aspectRatio > 1f) MIN_SIZE * aspectRatio else MIN_SIZE
        } else {
            MIN_SIZE
        }

    private val calculateMinCropHeight
        get() = if (enableAspectRatio) {
            if (aspectRatio > 1f) MIN_SIZE else MIN_SIZE / aspectRatio
        } else {
            MIN_SIZE
        }

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

            curCropRect.set(animCropRect)
            curImageRect.set(animImageRect)
            mainMatrix.setValues(animMatrixPoints)
            invalidate()
        }

        fun setStartState(cropRect: RectF, imageRect: RectF, imageMatrix: Matrix) {
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

    private var onCropImageListener: OnCropImageListener? = null
    fun setOnCropImageListener(onCropImageListener: OnCropImageListener?) {
        this.onCropImageListener = onCropImageListener
    }

    private var onChangeCropRectListener: OnChangeCropRectListener? = null
    fun setOnChangeCropRectListener(onChangeCropRectListener: OnChangeCropRectListener?) {
        this.onChangeCropRectListener = onChangeCropRectListener
    }

    fun setCropInfo(info: CropImageInfo) {
        coroutineScope.launch {
            originBitmap = withContext(Dispatchers.IO) {
                when (info.uri?.scheme) {
                    SCHEME_CONTENT -> context.contentResolver.getBitmap(info.uri)
                    SCHEME_FILE -> BitmapFactory.decodeFile(info.uri.path)
                    else -> null
                }
            }
            initRect(info.croppedRect)
            applyMatrix(false)
        }
    }

    fun cropImage() {
        val bitmap = originBitmap
        if (bitmap != null) {
            val croppedRect = RectF(curCropRect)
            mainMatrix.invert(mainInverseMatrix)
            mainInverseMatrix.mapRect(croppedRect)

            val croppedBitmap = Bitmap.createBitmap(
                bitmap,
                croppedRect.left.toInt(),
                croppedRect.top.toInt(),
                (croppedRect.right - croppedRect.left).toInt(),
                (croppedRect.bottom - croppedRect.top).toInt()
            )
            onCropImageListener?.onCrop(croppedBitmap, croppedRect)
        } else {
            onCropImageListener?.onCrop(null, null)
        }
    }

    // ????????? ????????? ?????????, Rect ?????????
    private fun initRect(croppedRect: RectF? = null) {
        mainMatrix.reset()

        val bitmap = originBitmap
        if (bitmap != null) {
            realImageRect.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
            curImageRect.set(realImageRect)

            if (croppedRect != null && croppedRect != RECT_F_EMPTY) {
                curCropRect.set(croppedRect)
                applyZoom(bitmap.width, bitmap.height)
            } else {
                if (enableAspectRatio) {
                    // AspectRatio ??? ????????? CropRect ??? ?????? ??????
                    val aspectWidth = min(realImageRect.width(), realImageRect.height() * aspectRatio)
                    val aspectHeight = min(realImageRect.width() / aspectRatio, realImageRect.height())

                    val aspectOffsetX = (realImageRect.width() - aspectWidth) / 2
                    val aspectOffsetY = (realImageRect.height() - aspectHeight) / 2

                    curCropRect.set(
                        aspectOffsetX,
                        aspectOffsetY,
                        aspectOffsetX + aspectWidth,
                        aspectOffsetY + aspectHeight
                    )
                } else {
                    curCropRect.set(realImageRect)
                }

                val widthMargin = curCropRect.width() * cropInitHorizontalMarginPercent
                val heightMargin = curCropRect.height() * cropInitVerticalMarginPercent

                curCropRect.inset(widthMargin, heightMargin)
            }
        } else {
            realImageRect.set(RECT_F_EMPTY)
            curImageRect.set(RECT_F_EMPTY)
            curCropRect.set(RECT_F_EMPTY)
        }
    }

    private fun applyZoom(width: Int, height: Int) {
        if (width == 0 || height == 0 || originBitmap == null) {
            return
        }

        val cropWidth = curCropRect.width()
        val cropHeight = curCropRect.height()

        var newZoom = zoom
        /*
         * CropWindow ??? ????????? ????????? ????????? 50% ????????? ????????? ?????????,
         * CropWindow ??? (1 <= a < 2) ??? ?????? ????????? ?????? ?????? ?????? ?????? ????????? scale ??? 1 ????????? ??????.
         * 1.5 ??? ?????? ?????? ????????? ????????? ?????? ?????? ????????? ?????? ?????? ????????? ????????????
         */
        if (zoom < MAX_ZOOM && cropWidth < width * 0.5f && cropHeight < height * 0.5f) {
            val scaleW = width / cropWidth * 0.66f * zoom
            val scaleH = height / cropHeight * 0.66f * zoom
            newZoom = minOf(scaleW, scaleH, MAX_ZOOM)
        }
        /*
         * CropWindow ??? ????????? ????????? ????????? 66% ?????? ????????????,
         * CropWindow ??? (a > 1.5) ??? ???????????? scale ??? 1 ????????? ?????? ??????.
         * 2 ??? ?????? ?????? ????????? Zoom??? ?????? ?????????
         */
        else if (zoom > MIN_ZOOM && cropWidth > width * 0.66f || cropHeight > height * 0.66f) {
            val scaleW = width / cropWidth * 0.5f * zoom
            val scaleH = height / cropHeight * 0.5f * zoom
            newZoom = max(min(scaleW, scaleH), MIN_ZOOM)
        }

        if (newZoom != zoom) {
            cropZoomAnimation.setStartState(curCropRect, curImageRect, mainMatrix)
            zoom = newZoom
            applyMatrix(animate = true)
        }
    }

    /**
     * ????????? ????????? ?????? ????????? ???????????? ???????????? ????????? ???????????? ?????? ??????
     * ex) onLayout, applyZoom
     */
    private fun applyMatrix(animate: Boolean) {
        val bitmap = originBitmap
        if (width == 0 || height == 0 || bitmap == null) {
            return
        }

        // 1. ??? ????????? ?????? ?????? ?????? ????????? ???????????? ???????????? ????????????
        curImageRect.set(realImageRect)
        mainMatrix.invert(mainInverseMatrix)
        mainInverseMatrix.mapRect(curCropRect)
        mainMatrix.reset()

        // 2. ???????????? ????????? ?????? ?????????
        val wScale = width / bitmap.width.toFloat()
        val hScale = height / bitmap.height.toFloat()
        val scale = min(wScale, hScale) * zoom
        mainMatrix.postScale(scale, scale)
        mapCurrentImageRectByMatrix()

        // 3. ????????? ???????????? ????????? ???????????? ??????
        val offsetX = (width - curImageRect.width()) / 2
        val offsetY = (height - curImageRect.height()) / 2
        mainMatrix.postTranslate(offsetX, offsetY)
        mainMatrix.mapRect(curCropRect)
        mapCurrentImageRectByMatrix()

        // 4. ZoomOffset ?????????
        val zoomOffsetX = when {
            width > curImageRect.width() -> 0f
            else -> max(
                min(width / 2 - curCropRect.centerX(), -curImageRect.left),
                width - curImageRect.right
            )
        }
        val zoomOffsetY = when {
            height > curImageRect.height() -> 0f
            else -> max(
                min(height / 2 - curCropRect.centerY(), -curImageRect.top),
                height - curImageRect.bottom
            )
        }

        mainMatrix.postTranslate(zoomOffsetX, zoomOffsetY)
        curCropRect.offset(zoomOffsetX, zoomOffsetY)
        mapCurrentImageRectByMatrix()

        if (animate) {
            cropZoomAnimation.setEndState(curCropRect, curImageRect, mainMatrix)
            startAnimation(cropZoomAnimation)
        } else {
            invalidate()
        }
    }

    // ?????? ????????? Matrix ??? ?????? ?????? curImageRect ??? ?????? ?????????
    private fun mapCurrentImageRectByMatrix() {
        curImageRect.set(realImageRect)
        mainMatrix.mapRect(curImageRect)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        applyZoom(right - left, bottom - top)
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
            canvas.clipOutRect(curCropRect)
        } else {
            canvas.clipRect(curCropRect, Region.Op.DIFFERENCE)
        }
        canvas.drawRect(curImageRect, backgroundPaint)
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
            edgePaint
        )
    }

    private fun drawCorner(canvas: Canvas) {
        val line = CORNER_THICKNESS / 2
        canvas.drawLine(
            curCropRect.left + CORNER_THICKNESS,
            curCropRect.top + line,
            curCropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            curCropRect.top + line,
            cornerPaint
        )
        canvas.drawLine(
            curCropRect.left + line,
            curCropRect.top,
            curCropRect.left + line,
            curCropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            curCropRect.right - CORNER_THICKNESS,
            curCropRect.top + line,
            curCropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            curCropRect.top + line,
            cornerPaint
        )
        canvas.drawLine(
            curCropRect.right - line,
            curCropRect.top,
            curCropRect.right - line,
            curCropRect.top + CORNER_LENGTH + CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            curCropRect.left + CORNER_THICKNESS,
            curCropRect.bottom - line,
            curCropRect.left + CORNER_LENGTH + CORNER_THICKNESS,
            curCropRect.bottom - line,
            cornerPaint
        )
        canvas.drawLine(
            curCropRect.left + line,
            curCropRect.bottom,
            curCropRect.left + line,
            curCropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint
        )

        canvas.drawLine(
            curCropRect.right - CORNER_THICKNESS,
            curCropRect.bottom - line,
            curCropRect.right - CORNER_LENGTH - CORNER_THICKNESS,
            curCropRect.bottom - line,
            cornerPaint
        )
        canvas.drawLine(
            curCropRect.right - line,
            curCropRect.bottom,
            curCropRect.right - line,
            curCropRect.bottom - CORNER_LENGTH - CORNER_THICKNESS,
            cornerPaint
        )
    }

    private var activePointerId: Int? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
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

    private fun isActivePointer(event: MotionEvent): Boolean {
        val curPointerId = event.getPointerId(event.actionIndex)
        if (activePointerId == null) {
            activePointerId = curPointerId
        }
        return activePointerId == curPointerId
    }

    private fun initTouchInfo() {
        touchStartPos.set(touchEndPos)
        touchCropRect.set(curCropRect)
        touchImageRect.set(curImageRect)
        touchMatrix.set(mainMatrix)
    }

    private fun setTouchPos(event: MotionEvent) {
        touchEndPos.set(event.x, event.y)
        if (event.action == MotionEvent.ACTION_DOWN) {
            initTouchInfo()
        }
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
            EventType.RESIZE -> if (enableAspectRatio && aspectRatio > 0f) {
                resizeCropWithFixedAspectRatio()
            } else {
                resizeCropWithFreeAspectRatio()
            }
            EventType.MOVE -> moveCrop()
            else -> {}
        }
    }

    private fun cleanEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                if (eventType == EventType.RESIZE) {
                    applyZoom(width, height)
                }

                eventType = EventType.NONE
                activePointerId = null

                val croppedRect = RectF(curCropRect)
                mainMatrix.invert(mainInverseMatrix)
                mainInverseMatrix.mapRect(croppedRect)
                onChangeCropRectListener?.onChange(croppedRect)
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

    private fun moveCrop() {
        val diff = touchEndPos.minus(touchStartPos)

        val screenStart = max(curImageRect.left, 0f)
        val screenEnd = min(curImageRect.right, width.toFloat())
        val screenTop = max(curImageRect.top, 0f)
        val screenBottom = min(curImageRect.bottom, height.toFloat())

        val cropOffsetX = when {
            touchCropRect.left + diff.x < screenStart -> screenStart - touchCropRect.left
            touchCropRect.right + diff.x > screenEnd -> screenEnd - touchCropRect.right
            else -> diff.x
        }
        val cropOffsetY = when {
            touchCropRect.top + diff.y < screenTop -> screenTop - touchCropRect.top
            touchCropRect.bottom + diff.y > screenBottom -> screenBottom - touchCropRect.bottom
            else -> diff.y
        }

        curCropRect.apply {
            set(touchCropRect)
            offset(cropOffsetX, cropOffsetY)
        }

        val overOffsetX = (cropOffsetX - diff.x) * zoom
        val overOffsetY = (cropOffsetY - diff.y) * zoom

        val imageOffsetX = when {
            touchImageRect.left + overOffsetX > screenStart -> screenStart - touchImageRect.left
            touchImageRect.right + overOffsetX < screenEnd -> screenEnd - touchImageRect.right
            else -> overOffsetX
        }

        val imageOffsetY = when {
            touchImageRect.top + overOffsetY > screenTop -> screenTop - touchImageRect.top
            touchImageRect.bottom + overOffsetY < screenBottom -> screenBottom - touchImageRect.bottom
            else -> overOffsetY
        }

        if (imageOffsetX != 0f || imageOffsetY != 0f) {
            mainMatrix.set(touchMatrix)
            mainMatrix.postTranslate(imageOffsetX, imageOffsetY)
            mapCurrentImageRectByMatrix()
            initTouchInfo()
        }
        invalidate()
    }

    private fun resizeCropWithFreeAspectRatio() {
        val range = touchRange ?: return
        val diff = touchEndPos.minus(touchStartPos)

        when (range) {
            TouchRange.LEFT,
            TouchRange.LEFT_TOP,
            TouchRange.LEFT_BOTTOM -> resizeLeft(diff.x)
            TouchRange.RIGHT,
            TouchRange.RIGHT_TOP,
            TouchRange.RIGHT_BOTTOM -> resizeRight(diff.x)
            else -> {}
        }

        when (range) {
            TouchRange.TOP,
            TouchRange.LEFT_TOP,
            TouchRange.RIGHT_TOP -> resizeTop(diff.y)
            TouchRange.BOTTOM,
            TouchRange.LEFT_BOTTOM,
            TouchRange.RIGHT_BOTTOM -> resizeBottom(diff.y)
            else -> {}
        }
        invalidate()
    }

    private fun resizeLeft(diffX: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedLeft = touchCropRect.left + diffX
        val boundLeft = boundLeft
        val boundTop = boundTop
        val boundBottom = boundBottom
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedLeft < boundLeft) {
            resizedLeft = boundLeft
        }
        if (resizedLeft > curCropRect.right - minCropWidth) {
            resizedLeft = curCropRect.right - minCropWidth
        }

        if (dir != ResizeAddDir.NONE) {
            var newHeight = (curCropRect.right - resizedLeft) / aspectRatio
            if (newHeight < minCropHeight) {
                resizedLeft = max(boundLeft, curCropRect.right - minCropHeight * aspectRatio)
                newHeight = (curCropRect.right - resizedLeft) / aspectRatio
            }

            when (dir) {
                ResizeAddDir.TOP -> {
                    if (newHeight > curCropRect.bottom - boundTop) {
                        resizedLeft = max(boundLeft, curCropRect.right - (curCropRect.bottom - boundTop) * aspectRatio)
                    }
                }
                ResizeAddDir.BOTTOM -> {
                    if (newHeight > boundBottom - curCropRect.top) {
                        resizedLeft =
                            maxOf(
                                resizedLeft,
                                boundLeft,
                                curCropRect.right - (boundBottom - curCropRect.top) * aspectRatio
                            )
                    }
                }
                ResizeAddDir.VERTICAL -> {
                    resizedLeft =
                        maxOf(resizedLeft, boundLeft, curCropRect.right - (boundBottom - boundTop) * aspectRatio)
                }
                else -> {}
            }
        }
        curCropRect.left = resizedLeft
    }

    private fun resizeRight(diffX: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedRight = touchCropRect.right + diffX
        val boundRight = boundRight
        val boundTop = boundTop
        val boundBottom = boundBottom
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedRight > boundRight) {
            resizedRight = boundRight
        }
        if (resizedRight < curCropRect.left + minCropWidth) {
            resizedRight = curCropRect.left + minCropWidth
        }
        if (dir != ResizeAddDir.NONE) {
            var newHeight = (resizedRight - curCropRect.left) / aspectRatio
            if (newHeight < minCropHeight) {
                resizedRight = min(boundRight, curCropRect.left + minCropHeight * aspectRatio)
                newHeight = (resizedRight - curCropRect.left) / aspectRatio
            }

            when (dir) {
                ResizeAddDir.TOP -> {
                    if (newHeight > curCropRect.bottom - boundTop) {
                        resizedRight = min(boundRight, curCropRect.left + (curCropRect.bottom - boundTop) * aspectRatio)
                    }
                }
                ResizeAddDir.BOTTOM -> {
                    if (newHeight > boundBottom - curCropRect.top) {
                        resizedRight =
                            minOf(
                                resizedRight,
                                boundRight,
                                curCropRect.left + (boundBottom - curCropRect.top) * aspectRatio
                            )
                    }
                }
                ResizeAddDir.VERTICAL -> {
                    resizedRight =
                        minOf(resizedRight, boundRight, curCropRect.left + (boundBottom - boundTop) * aspectRatio)
                }
                else -> {}
            }
        }

        curCropRect.right = resizedRight
    }

    private fun resizeTop(diffY: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedTop = touchCropRect.top + diffY
        val boundLeft = boundLeft
        val boundTop = boundTop
        val boundRight = boundRight
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedTop < boundTop) {
            resizedTop = boundTop
        }
        if (resizedTop > curCropRect.bottom - minCropHeight) {
            resizedTop = curCropRect.bottom - minCropHeight
        }
        if (dir != ResizeAddDir.NONE) {
            var newWidth = (curCropRect.bottom - resizedTop) * aspectRatio
            if (newWidth < minCropWidth) {
                resizedTop = max(boundTop, curCropRect.bottom - minCropWidth / aspectRatio)
                newWidth = (curCropRect.bottom - resizedTop) * aspectRatio
            }

            when (dir) {
                ResizeAddDir.LEFT -> {
                    if (newWidth > curCropRect.right - boundLeft) {
                        resizedTop = max(boundTop, curCropRect.bottom - (curCropRect.right - boundLeft) / aspectRatio)
                    }
                }
                ResizeAddDir.RIGHT -> {
                    if (newWidth > boundRight - curCropRect.left) {
                        resizedTop =
                            maxOf(
                                resizedTop,
                                boundTop,
                                curCropRect.bottom - (boundRight - curCropRect.left) / aspectRatio
                            )
                    }
                }
                ResizeAddDir.HORIZONTAL -> {
                    resizedTop =
                        maxOf(resizedTop, boundTop, curCropRect.bottom - (boundRight - boundLeft) / aspectRatio)
                }
                else -> {}
            }
        }
        curCropRect.top = resizedTop
    }

    private fun resizeBottom(diffY: Float, dir: ResizeAddDir = ResizeAddDir.NONE) {
        var resizedBottom = touchCropRect.bottom + diffY
        val boundLeft = boundLeft
        val boundBottom = boundBottom
        val boundRight = boundRight
        val minCropWidth = calculateMinCropWidth
        val minCropHeight = calculateMinCropHeight

        if (resizedBottom > boundBottom) {
            resizedBottom = boundBottom
        }
        if (resizedBottom < curCropRect.top + minCropHeight) {
            resizedBottom = curCropRect.top + minCropHeight
        }
        if (dir != ResizeAddDir.NONE) {
            var newWidth = (resizedBottom - curCropRect.top) * aspectRatio
            if (newWidth < minCropWidth) {
                resizedBottom = min(boundBottom, curCropRect.top + minCropWidth / aspectRatio)
                newWidth = (resizedBottom - curCropRect.top) * aspectRatio
            }

            when (dir) {
                ResizeAddDir.LEFT -> {
                    if (newWidth > curCropRect.right - boundLeft) {
                        resizedBottom =
                            min(boundBottom, curCropRect.top + (curCropRect.right - boundLeft) / aspectRatio)
                    }
                }
                ResizeAddDir.RIGHT -> {
                    if (newWidth > boundRight - curCropRect.left) {
                        resizedBottom =
                            minOf(
                                resizedBottom,
                                boundBottom,
                                curCropRect.top + (boundRight - curCropRect.left) / aspectRatio
                            )
                    }
                }
                ResizeAddDir.HORIZONTAL -> {
                    resizedBottom =
                        minOf(resizedBottom, boundBottom, curCropRect.top + (boundRight - boundLeft) / aspectRatio)
                }
                else -> {}
            }
        }
        curCropRect.bottom = resizedBottom
    }

    private fun resizeCropWithFixedAspectRatio() {
        val range = touchRange ?: return
        val diff = touchEndPos.minus(touchStartPos)
        when (range) {
            TouchRange.LEFT_TOP -> resizeLeftTopWithAspectRatio(diff)
            TouchRange.TOP -> resizeTopWithAspectRatio(diff.y)
            TouchRange.RIGHT_TOP -> resizeRightTopWithAspectRatio(diff)
            TouchRange.RIGHT -> resizeRightWithAspectRatio(diff.x)
            TouchRange.RIGHT_BOTTOM -> resizeRightBottomWithAspectRatio(diff)
            TouchRange.BOTTOM -> resizeBottomWithAspectRatio(diff.y)
            TouchRange.LEFT_BOTTOM -> resizeLeftBottomWithAspectRatio(diff)
            TouchRange.LEFT -> resizeLeftWithAspectRatio(diff.x)
            else -> {}
        }
        invalidate()
    }

    private fun calculateAspectRatio(left: Float, top: Float, right: Float, bottom: Float): Float {
        return (right - left) / (bottom - top)
    }

    private fun resizeLeftByAspectRatio() {
        curCropRect.left = curCropRect.right - curCropRect.height() * aspectRatio
    }

    private fun resizeTopByAspectRatio() {
        curCropRect.top = curCropRect.bottom - curCropRect.width() / aspectRatio
    }

    private fun resizeRightByAspectRatio() {
        curCropRect.right = curCropRect.left + curCropRect.height() * aspectRatio
    }

    private fun resizeBottomByAspectRatio() {
        curCropRect.bottom = curCropRect.top + curCropRect.width() / aspectRatio
    }

    private fun resizeHorizontalByAspectRatio() {
        curCropRect.inset((curCropRect.width() - curCropRect.height() * aspectRatio) / 2, 0f)
        val boundLeft = boundLeft
        val boundRight = boundRight
        if (curCropRect.left < boundLeft) {
            curCropRect.offset(boundLeft - curCropRect.left, 0f)
        }
        if (curCropRect.right > boundRight) {
            curCropRect.offset(boundRight - curCropRect.right, 0f)
        }
    }

    private fun resizeVerticalByAspectRatio() {
        curCropRect.inset(0f, (curCropRect.height() - curCropRect.width() / aspectRatio) / 2)
        val boundTop = boundTop
        val boundBottom = boundBottom
        if (curCropRect.top < boundTop) {
            curCropRect.offset(0f, boundTop - curCropRect.top)
        }
        if (curCropRect.bottom > boundBottom) {
            curCropRect.offset(0f, boundBottom - curCropRect.bottom)
        }
    }

    private fun resizeLeftTopWithAspectRatio(diff: PointF) {
        if (calculateAspectRatio(
                min(curCropRect.left + diff.x, curCropRect.right - calculateMinCropWidth),
                min(curCropRect.top + diff.y, curCropRect.bottom - calculateMinCropHeight),
                curCropRect.right,
                curCropRect.bottom
            ) < aspectRatio
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
                curCropRect.bottom
            ) < aspectRatio
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
                max(curCropRect.bottom + diff.y, curCropRect.top + calculateMinCropHeight)
            ) < aspectRatio
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
                max(curCropRect.bottom + diff.y, curCropRect.top + calculateMinCropHeight)
            ) < aspectRatio
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

    enum class TouchRange {
        LEFT, LEFT_TOP, TOP, RIGHT_TOP, RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, CENTER
    }

    enum class EventType {
        NONE, MOVE, RESIZE
    }

    enum class ResizeAddDir {
        LEFT, TOP, RIGHT, BOTTOM, VERTICAL, HORIZONTAL, NONE
    }

    companion object {
        private val RECT_F_EMPTY = RectF()

        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 4f

        private const val DEFAULT_MARGIN_PERCENT = 0.1f

        private val CORNER_THICKNESS = 3.dp
        private val CORNER_LENGTH = 12.dp.toInt()
        private val MIN_SIZE = (CORNER_LENGTH + CORNER_THICKNESS) * 2
        private val EDGE_TOUCH_RANGE = 24.dp.toInt()
    }
}
