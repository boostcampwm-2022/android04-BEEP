package com.lighthouse.common.recognizer.recognizer.syrup

import android.graphics.Bitmap
import com.lighthouse.common.recognizer.processor.BaseProcessor
import com.lighthouse.common.recognizer.processor.GifticonProcessImage
import com.lighthouse.common.recognizer.processor.GifticonProcessText
import com.lighthouse.common.recognizer.processor.GifticonProcessTextTag

class SyrupProcessor : BaseProcessor() {

    override fun processTextImage(bitmap: Bitmap): List<GifticonProcessText> {
        return listOf(
            cropAndScaleTextImage(GifticonProcessTextTag.GIFTICON_NAME, bitmap, 0.375f, 0.4375f, 0.98437f, 0.52083f),
            cropAndScaleTextImage(GifticonProcessTextTag.BRAND_NAME, bitmap, 0.5625f, 0.59375f, 0.98437f, 0.6625f)
        )
    }

    override fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage {
        return cropGifticonImage(bitmap, 0.0625f, 0.4375f, 0.375f, 0.6458f)
    }
}
