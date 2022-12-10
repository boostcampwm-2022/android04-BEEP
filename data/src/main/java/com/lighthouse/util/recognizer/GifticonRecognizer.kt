package com.lighthouse.util.recognizer

import android.graphics.Bitmap
import com.lighthouse.util.recognizer.parser.BalanceParser
import com.lighthouse.util.recognizer.parser.BarcodeParser
import com.lighthouse.util.recognizer.parser.ExpiredParser
import com.lighthouse.util.recognizer.processor.ScaleProcessor
import com.lighthouse.util.recognizer.recognizer.TemplateRecognizer
import com.lighthouse.util.recognizer.recognizer.giftishow.GiftishowRecognizer
import com.lighthouse.util.recognizer.recognizer.kakao.KakaoRecognizer
import com.lighthouse.util.recognizer.recognizer.smilecon.SmileConRecognizer
import com.lighthouse.util.recognizer.recognizer.syrup.SyrupRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GifticonRecognizer {
    private val barcodeParser = BarcodeParser()
    private val expiredParser = ExpiredParser()
    private val balanceParser = BalanceParser()

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

    suspend fun recognize(bitmap: Bitmap): GifticonRecognizeInfo? = withContext(Dispatchers.IO) {
        val origin = scaleProcessor.scaleProcess(bitmap)
        val inputs = textRecognizer.recognize(origin)
        origin.recycle()
        val barcodeResult = barcodeParser.parseBarcode(inputs)
        if (barcodeResult.barcode == "") {
            return@withContext null
        }
        val expiredResult = expiredParser.parseExpiredDate(barcodeResult.filtered)
        var info = GifticonRecognizeInfo(candidate = expiredResult.filtered)
        getTemplateRecognizer(info.candidate)?.run {
            info = recognize(bitmap, info.candidate)
        }
        val balanceResult = balanceParser.parseCashCard(info.candidate)
        info.copy(
            barcode = barcodeResult.barcode,
            expiredAt = expiredResult.expired,
            isCashCard = balanceResult.balance > 0,
            balance = balanceResult.balance
        )
    }
}
