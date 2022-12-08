package com.lighthouse.util.recognizer.recognizer.giftishow

import android.graphics.Bitmap
import com.lighthouse.util.recognizer.processor.BaseProcessor
import com.lighthouse.util.recognizer.processor.GifticonProcessImage
import com.lighthouse.util.recognizer.processor.GifticonProcessText
import com.lighthouse.util.recognizer.processor.GifticonProcessTextTag

class GiftishowProcessor : BaseProcessor() {

    override fun processTextImage(bitmap: Bitmap): List<GifticonProcessText> {
        return listOf(
            cropAndScaleTextImage(GifticonProcessTextTag.GIFTICON_BRAND_NAME, bitmap, 0.23f, 0.83f, 0.96f, 0.94f)
        )
    }

    override fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage {
        return cropImage(bitmap, 0.1f, 0.04f, 0.43f, 0.37f)
    }
}
