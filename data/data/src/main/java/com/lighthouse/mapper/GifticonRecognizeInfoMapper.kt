package com.lighthouse.mapper

import android.net.Uri
import com.lighthouse.beep.model.gifticon.GifticonRecognizeResult
import com.lighthouse.common.mapper.toDomain
import com.lighthouse.utils.recognizer.model.GifticonRecognizeInfo

internal fun GifticonRecognizeInfo.toDomain(
    originUri: Uri,
    croppedUri: Uri?
): GifticonRecognizeResult {
    return GifticonRecognizeResult(
        name,
        brand,
        barcode,
        expiredAt,
        isCashCard,
        balance,
        originUri.toDomain(),
        croppedUri.toDomain(),
        croppedRect.toDomain()
    )
}
