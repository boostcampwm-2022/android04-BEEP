package com.lighthouse.mapper

import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.domain.model.GifticonCropForUpdate

fun GifticonCropForUpdate.toEntity(): GifticonCropEntity {
    return GifticonCropEntity(
        gifticonId = gifticonId,
        croppedRect = croppedRect.toEntity()
    )
}
