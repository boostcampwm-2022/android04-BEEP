package com.lighthouse.utils.recognizer

import android.graphics.Bitmap
import com.lighthouse.utils.recognizer.model.GifticonRecognizeInfo
import com.lighthouse.utils.recognizer.parser.BalanceParser
import com.lighthouse.utils.recognizer.parser.BarcodeParser
import com.lighthouse.utils.recognizer.parser.ExpiredParser
import com.lighthouse.utils.recognizer.recognizer.TemplateRecognizer
import com.lighthouse.utils.recognizer.recognizer.giftishow.GiftishowRecognizer
import com.lighthouse.utils.recognizer.recognizer.kakao.KakaoRecognizer
import com.lighthouse.utils.recognizer.recognizer.smilecon.SmileConRecognizer
import com.lighthouse.utils.recognizer.recognizer.syrup.SyrupRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GifticonRecognizer {

    private val barcodeParser = BarcodeParser()
    private val expiredParser = ExpiredParser()
    private val balanceParser = BalanceParser()

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

    suspend fun recognize(bitmap: Bitmap): GifticonRecognizeInfo = withContext(Dispatchers.IO) {
        val inputs = textRecognizer.recognize(bitmap)
        val barcodeResult = barcodeParser.parseBarcode(inputs)
        val expiredResult = expiredParser.parseExpiredDate(barcodeResult.filtered)
        var info =
            GifticonRecognizeInfo(candidate = expiredResult.filtered)
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
