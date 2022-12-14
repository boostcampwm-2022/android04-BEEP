package com.lighthouse.util.recognizer

import android.graphics.Bitmap
import com.lighthouse.util.recognizer.parser.BalanceParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BalanceRecognizer {

    private val balanceParser = BalanceParser()

    private val textRecognizer = TextRecognizer()

    suspend fun recognize(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val inputs = textRecognizer.recognize(bitmap)
        balanceParser.parseCashCard(inputs)
    }
}
