package com.lighthouse.util.recognizer

import android.graphics.Bitmap
import com.lighthouse.util.recognizer.parser.OriginParser
import com.lighthouse.util.recognizer.processor.ScaleProcessor
import com.lighthouse.util.recognizer.recognizer.TemplateRecognizer
import com.lighthouse.util.recognizer.recognizer.TextRecognizer
import com.lighthouse.util.recognizer.recognizer.giftishow.GiftishowRecognizer
import com.lighthouse.util.recognizer.recognizer.kakao.KakaoRecognizer
import com.lighthouse.util.recognizer.recognizer.smilecon.SmileConRecognizer
import com.lighthouse.util.recognizer.recognizer.syrup.SyrupRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GifticonRecognizer {

    private val originParser = OriginParser()
    private val scaleProcessor = ScaleProcessor()
    private val textRecognizer = TextRecognizer()

    private val templateRecognizerList = listOf(
        KakaoRecognizer(),
        SyrupRecognizer(),
        GiftishowRecognizer(),
        SmileConRecognizer()
    )

    private fun getTemplateRecognizer(inputs: List<String>): TemplateRecognizer? {
        return templateRecognizerList.firstOrNull {
            it.match(inputs)
        }
    }

    suspend fun recognize(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val origin = scaleProcessor.scaleProcess(bitmap)
        val originInputs = textRecognizer.recognize(origin)
        var info = originParser.parseBarcode(originInputs) ?: return@withContext null
        info = originParser.parseDate(info)
        info = originParser.filterTrash(info)

        val template = getTemplateRecognizer(info.candidate)
        info = if (template != null) {
            template.recognize(origin, info)
        } else {
            val gifticonName = info.candidate.first()
            val brandName = if (info.candidate.size > 1) info.candidate.last() else ""
            info.copy(name = gifticonName, brand = brandName)
        }
        info = originParser.parseCashCard(info)
        origin.recycle()
        return@withContext info
    }
}
