package com.lighthouse.database.mapper

import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.domain.model.GifticonCrop
import com.lighthouse.mapper.toDomain

fun GifticonCropEntity.toDomain(): GifticonCrop {
    return GifticonCrop(
        gifticonId = gifticonId,
        rect = croppedRect.toDomain()
    )
}
