package com.lighthouse.util.recognizer

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first

class TextRecognizer {
    private fun getRecognition(language: TextRecognizerLanguage): TextRecognizer {
        val options = when (language) {
            TextRecognizerLanguage.KOREAN -> KoreanTextRecognizerOptions.Builder().build()
            TextRecognizerLanguage.ENGLISH -> TextRecognizerOptions.DEFAULT_OPTIONS
        }
        return TextRecognition.getClient(options)
    }

    suspend fun recognize(bitmap: Bitmap, language: TextRecognizerLanguage = TextRecognizerLanguage.KOREAN) =
        callbackFlow {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognition = getRecognition(language)
            recognition.process(image).addOnSuccessListener {
                trySend(it.text.lines())
                close()
            }
            awaitClose()
        }.first()
}
