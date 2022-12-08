package com.lighthouse.presentation.mapper

import android.net.Uri
import androidx.core.net.toUri
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.EditTextInfo
import java.io.File

fun GifticonForAddition.toPresentation(id: Long): AddGifticonUIModel {
    return AddGifticonUIModel(
        id = id,
        hasImage = hasImage,
        name = name,
        brandName = brandName,
        barcode = EditTextInfo(barcode, 0),
        expiredAt = expiredAt,
        isCashCard = isCashCard,
        balance = EditTextInfo(balance.toString(), 0),
        memo = memo,
        origin = Uri.parse(originUri),
        thumbnailImage = CroppedImage(uri = if (croppedUri != "") File(croppedUri).toUri() else null)
    )
}
