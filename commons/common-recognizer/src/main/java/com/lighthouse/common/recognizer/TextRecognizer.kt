package com.lighthouse.common.recognizer

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class TextRecognizer {
    suspend fun recognize(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        callbackFlow {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognition = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            recognition.process(image).addOnSuccessListener {
                trySend(it.text.lines().filter { line -> line != "" })
                close()
            }
            awaitClose()
        }.first()
    }
}
