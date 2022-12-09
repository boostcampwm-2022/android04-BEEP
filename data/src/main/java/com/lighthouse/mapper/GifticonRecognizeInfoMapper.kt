package com.lighthouse.mapper

import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.util.recognizer.GifticonRecognizeInfo

fun GifticonRecognizeInfo.toDomain(path: String, croppedPath: String): GifticonForAddition {
    return GifticonForAddition(
        true,
        name,
        brand,
        barcode,
        expiredAt,
        isCashCard,
        balance,
        path,
        croppedPath,
        croppedRect.toDomain(),
        ""
    )
}
