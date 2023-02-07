package com.lighthouse.mapper

import com.lighthouse.beep.model.gifticon.GifticonCrop
import com.lighthouse.database.entity.GifticonCropEntity

fun GifticonCrop.toEntity(): GifticonCropEntity {
    return GifticonCropEntity(
        gifticonId = gifticonId,
        croppedRect = rect.toEntity()
    )
}
