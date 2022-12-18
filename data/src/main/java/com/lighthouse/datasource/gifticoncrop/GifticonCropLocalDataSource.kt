package com.lighthouse.datasource.gifticoncrop

import com.lighthouse.database.entity.GifticonCropEntity

interface GifticonCropLocalDataSource {

    suspend fun insertGifticonCrop(gifticonCropEntity: GifticonCropEntity)

    suspend fun updateGifticonCrop(gifticonCropEntity: GifticonCropEntity)
}
