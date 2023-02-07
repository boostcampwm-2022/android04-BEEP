package com.lighthouse.common.recognizer.recognizer.smilecon

import android.graphics.Bitmap
import com.lighthouse.common.recognizer.processor.BaseProcessor
import com.lighthouse.common.recognizer.processor.GifticonProcessImage
import com.lighthouse.common.recognizer.processor.GifticonProcessText
import com.lighthouse.common.recognizer.processor.GifticonProcessTextTag

class SmileConProcessor : BaseProcessor() {

    override fun processTextImage(bitmap: Bitmap): List<GifticonProcessText> {
        return listOf(
            cropAndScaleTextImage(
                GifticonProcessTextTag.BRAND_GIFTICON_NAME,
                bitmap,
                0.19801f,
                0.45263f,
                0.99008f,
                0.56841f
            )
        )
    }

    override fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage {
        return cropGifticonImage(bitmap, 0.02475f, 0.05263f, 0.47029f, 0.43157f)
    }
}
