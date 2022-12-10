package com.lighthouse.mapper

import android.net.Uri
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.util.recognizer.GifticonRecognizeInfo

fun GifticonRecognizeInfo.toDomain(originUri: Uri, croppedUri: Uri?): GifticonForAddition {
    return GifticonForAddition(
        true,
        name,
        brand,
        barcode,
        expiredAt,
        isCashCard,
        balance,
        originUri.toString(),
        croppedUri?.toString() ?: "",
        croppedRect.toDomain(),
        ""
    )
}
