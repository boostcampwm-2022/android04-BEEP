package com.lighthouse.util.recognizer.processor

import android.graphics.Bitmap
import android.graphics.Rect

abstract class BaseProcessor : ScaleProcessor() {

    protected fun cropImage(
        bitmap: Bitmap,
        leftPercent: Float,
        topPercent: Float,
        rightPercent: Float,
        bottomPercent: Float
    ): GifticonProcessImage {
        val cropRect = Rect(
            (bitmap.width * leftPercent).toInt(),
            (bitmap.height * topPercent).toInt(),
            (bitmap.width * rightPercent).toInt(),
            (bitmap.height * bottomPercent).toInt()
        )
        return GifticonProcessImage(
            Bitmap.createBitmap(
                bitmap,
                cropRect.left,
                cropRect.top,
                cropRect.width(),
                cropRect.height()
            ),
            cropRect
        )
    }

    protected fun cropAndScaleTextImage(
        tag: GifticonProcessTextTag,
        bitmap: Bitmap,
        leftPercent: Float,
        topPercent: Float,
        rightPercent: Float,
        bottomPercent: Float
    ): GifticonProcessText {
        val croppedImage = cropImage(bitmap, leftPercent, topPercent, rightPercent, bottomPercent)
        val image = scaleProcess(croppedImage.bitmap)
        croppedImage.bitmap.recycle()
        return GifticonProcessText(tag, image)
    }

    protected open fun preprocess(bitmap: Bitmap): Bitmap = bitmap

    protected abstract fun processTextImage(bitmap: Bitmap): List<GifticonProcessText>

    protected abstract fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage

    fun process(bitmap: Bitmap): GifticonProcessResult {
        val newBitmap = preprocess(bitmap)
        val result = GifticonProcessResult(
            processGifticonImage(bitmap),
            processTextImage(bitmap)
        )
        if (newBitmap != bitmap) {
            newBitmap.recycle()
        }
        return result
    }
}
