package com.lighthouse.common.recognizer.processor

import android.graphics.Bitmap
import android.graphics.Rect
import kotlin.math.max

abstract class BaseProcessor : ScaleProcessor() {

    open val enableCenterCrop = false

    open val centerCropAspectRatio = 1f

    private fun calculateRect(
        bitmap: Bitmap,
        leftPercent: Float,
        topPercent: Float,
        rightPercent: Float,
        bottomPercent: Float
    ): Rect {
        return Rect(
            (bitmap.width * leftPercent).toInt(),
            (bitmap.height * topPercent).toInt(),
            (bitmap.width * rightPercent).toInt(),
            (bitmap.height * bottomPercent).toInt()
        )
    }

    private fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }

    protected fun cropGifticonImage(
        bitmap: Bitmap,
        leftPercent: Float,
        topPercent: Float,
        rightPercent: Float,
        bottomPercent: Float
    ): GifticonProcessImage {
        val cropRect = calculateRect(bitmap, leftPercent, topPercent, rightPercent, bottomPercent)
        if (cropRect.width() != cropRect.height()) {
            cropRect.inset(
                max((cropRect.width() - cropRect.height()) / 2, 0),
                max((cropRect.height() - cropRect.width()) / 2, 0)
            )
        }
        return GifticonProcessImage(cropBitmap(bitmap, cropRect), cropRect)
    }

    protected fun cropAndScaleTextImage(
        tag: GifticonProcessTextTag,
        bitmap: Bitmap,
        leftPercent: Float,
        topPercent: Float,
        rightPercent: Float,
        bottomPercent: Float
    ): GifticonProcessText {
        val cropRect = calculateRect(bitmap, leftPercent, topPercent, rightPercent, bottomPercent)
        return GifticonProcessText(tag, cropBitmap(bitmap, cropRect))
    }

    protected abstract fun processTextImage(bitmap: Bitmap): List<GifticonProcessText>

    protected abstract fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage

    private fun centerCropBitmap(bitmap: Bitmap): Bitmap {
        val bitmapAspectRatio = bitmap.width.toFloat() / bitmap.height
        return if (bitmapAspectRatio > centerCropAspectRatio) {
            val newWidth = (bitmap.height * centerCropAspectRatio).toInt()
            Bitmap.createBitmap(bitmap, (bitmap.width - newWidth) / 2, 0, newWidth, bitmap.height)
        } else {
            val newHeight = (bitmap.width / centerCropAspectRatio).toInt()
            Bitmap.createBitmap(bitmap, 0, (bitmap.height - newHeight) / 2, bitmap.width, newHeight)
        }
    }

    private fun adjustCropRect(origin: Bitmap, cropped: Bitmap, image: GifticonProcessImage): GifticonProcessImage {
        val offsetX = (origin.width - cropped.width) / 2
        val offsetY = (origin.height - cropped.height) / 2
        return image.copy(
            rect = image.rect.apply {
                offset(offsetX, offsetY)
            }
        )
    }

    fun process(bitmap: Bitmap): GifticonProcessResult {
        val newBitmap = if (enableCenterCrop) centerCropBitmap(bitmap) else bitmap
        val image = processGifticonImage(newBitmap)
        val textList = processTextImage(newBitmap)
        return if (enableCenterCrop) {
            GifticonProcessResult(adjustCropRect(bitmap, newBitmap, image), textList)
        } else {
            GifticonProcessResult(image, textList)
        }
    }
}
