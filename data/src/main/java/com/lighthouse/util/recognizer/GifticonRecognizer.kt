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
        origin.recycle()
        var info = originParser.parseBarcode(originInputs) ?: return@withContext null
        info = originParser.parseDateFormat(info)
        info = originParser.parseExpiredFormat(info)
        info = originParser.filterTrash(info)
        getTemplateRecognizer(info.candidate)?.run {
            info = recognize(bitmap, info)
        }
        originParser.parseCashCard(info)
    }
}
