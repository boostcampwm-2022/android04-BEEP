package com.lighthouse.mapper

import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.domain.model.GifticonCrop

fun GifticonCrop.toEntity(): GifticonCropEntity {
    return GifticonCropEntity(
        gifticonId = gifticonId,
        croppedRect = rect.toEntity()
    )
}
