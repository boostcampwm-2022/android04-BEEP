package com.lighthouse.utils.recognizer.recognizer

import android.graphics.Bitmap
import com.lighthouse.utils.recognizer.TextRecognizer
import com.lighthouse.utils.recognizer.model.GifticonRecognizeInfo
import com.lighthouse.utils.recognizer.parser.BaseParser
import com.lighthouse.utils.recognizer.processor.BaseProcessor

internal abstract class TemplateRecognizer {

    protected abstract val parser: BaseParser

    protected abstract val processor: BaseProcessor

    private val textRecognizer = TextRecognizer()

    fun match(inputs: List<String>) = parser.match(inputs)

    suspend fun recognize(bitmap: Bitmap, inputs: List<String>): GifticonRecognizeInfo {
        val result = processor.process(bitmap)
        var newInfo =
            GifticonRecognizeInfo(candidate = inputs)
        newInfo = newInfo.copy(croppedImage = result.image.bitmap, croppedRect = result.image.rect)
        for (text in result.textList) {
            val newInputs = textRecognizer.recognize(text.bitmap)
            newInfo = parser.parseText(newInfo, text.tag, newInputs)
        }
        return newInfo
    }
}
