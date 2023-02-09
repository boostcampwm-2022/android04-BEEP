package com.lighthouse.utils.recognizer

import android.graphics.Bitmap
import com.lighthouse.utils.recognizer.parser.ExpiredParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpiredRecognizer {

    private val expiredParser = ExpiredParser()

    private val textRecognizer = TextRecognizer()

    suspend fun recognize(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val inputs = textRecognizer.recognize(bitmap)
        expiredParser.parseExpiredDate(inputs)
    }
}
