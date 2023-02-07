package com.lighthouse.mapper

import android.net.Uri
import com.lighthouse.beep.model.gifticon.GifticonForAddition
import com.lighthouse.common.recognizer.GifticonRecognizeInfo

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
