package com.lighthouse.common.recognizer.recognizer.giftishow

import android.graphics.Bitmap
import com.lighthouse.common.recognizer.processor.BaseProcessor
import com.lighthouse.common.recognizer.processor.GifticonProcessImage
import com.lighthouse.common.recognizer.processor.GifticonProcessText
import com.lighthouse.common.recognizer.processor.GifticonProcessTextTag

class GiftishowProcessor : BaseProcessor() {

    override fun processTextImage(bitmap: Bitmap): List<GifticonProcessText> {
        return listOf(
            cropAndScaleTextImage(GifticonProcessTextTag.GIFTICON_BRAND_NAME, bitmap, 0.23f, 0.83f, 0.96f, 0.94f)
        )
    }

    override fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage {
        return cropGifticonImage(bitmap, 0.1f, 0.04f, 0.43f, 0.37f)
    }
}
