package com.lighthouse.common.recognizer.recognizer.kakao

import android.graphics.Bitmap
import com.lighthouse.common.recognizer.processor.BaseProcessor
import com.lighthouse.common.recognizer.processor.GifticonProcessImage
import com.lighthouse.common.recognizer.processor.GifticonProcessText
import com.lighthouse.common.recognizer.processor.GifticonProcessTextTag

class KakaoProcessor : BaseProcessor() {

    override val enableCenterCrop = true

    override val centerCropAspectRatio = 0.48f

    override fun processTextImage(bitmap: Bitmap): List<GifticonProcessText> {
        return listOf(
            cropAndScaleTextImage(GifticonProcessTextTag.BRAND_GIFTICON_NAME, bitmap, 0f, 0.4f, 0.75f, 0.55f)
        )
    }

    override fun processGifticonImage(bitmap: Bitmap): GifticonProcessImage {
        return cropGifticonImage(bitmap, 0.13125f, 0.05282f, 0.86875f, 0.41951f)
    }
}
