package com.lighthouse.util.recognizer.recognizer.kakao

import android.graphics.Bitmap
import com.lighthouse.util.recognizer.processor.BaseProcessor
import com.lighthouse.util.recognizer.processor.GifticonProcessImage
import com.lighthouse.util.recognizer.processor.GifticonProcessText
import com.lighthouse.util.recognizer.processor.GifticonProcessTextTag

class KakaoProcessor : BaseProcessor() {

    private val aspectRatio: Float = 0.48f

    override fun preprocess(bitmap: Bitmap): Bitmap {
        val originWidth = bitmap.width
        val originHeight = bitmap.height
        val originAspect = originWidth.toFloat() / originHeight
        return if (aspectRatio > originAspect) {
            val croppedHeight = (originWidth / aspectRatio).toInt()
            val verticalMargin = (originHeight - croppedHeight) / 2
            Bitmap.createBitmap(bitmap, 0, verticalMargin, originWidth, croppedHeight)
        } else {
            val croppedWidth = (originHeight * aspectRatio).toInt()
            val horizontalMargin = (originWidth - croppedWidth) / 2
            Bitmap.createBitmap(bitmap, horizontalMargin, 0, croppedWidth, originHeight)
        }
    }

    override fun processTextImage(bitmap: Bitmap): List<GifticonProcessText> {
        return listOf(
            cropAndScaleTextImage(GifticonProcessTextTag.BRAND_GIFTICON_NAME, bitmap, 0f, 0.4f, 0.75f, 0.55f)
        )
    }

    override fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage {
        return cropImage(bitmap, 0.13125f, 0.05282f, 0.86875f, 0.41951f)
    }
}
