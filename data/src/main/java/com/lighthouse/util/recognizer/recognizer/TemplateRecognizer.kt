package com.lighthouse.util.recognizer.recognizer

import android.graphics.Bitmap
import com.lighthouse.util.recognizer.GifticonRecognizeInfo
import com.lighthouse.util.recognizer.TextRecognizer
import com.lighthouse.util.recognizer.parser.BaseParser
import com.lighthouse.util.recognizer.processor.BaseProcessor

abstract class TemplateRecognizer {

    protected abstract val parser: BaseParser

    protected abstract val processor: BaseProcessor

    private val textRecognizer = TextRecognizer()

    fun match(inputs: List<String>) = parser.match(inputs)

    suspend fun recognize(bitmap: Bitmap, info: GifticonRecognizeInfo): GifticonRecognizeInfo {
        var newInfo = parser.parseKeyword(info)
        val result = processor.process(bitmap)

        newInfo = newInfo.copy(croppedImage = result.image.bitmap, croppedRect = result.image.rect)
        for (text in result.textList) {
            val inputs = textRecognizer.recognize(text.bitmap)
            text.bitmap.recycle()
            newInfo = parser.parseText(newInfo, text.tag, inputs)
        }
        return newInfo
    }
}
