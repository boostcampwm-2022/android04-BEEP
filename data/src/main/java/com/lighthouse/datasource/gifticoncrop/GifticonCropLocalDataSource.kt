package com.lighthouse.datasource.gifticoncrop

import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.domain.model.GifticonCrop
import kotlinx.coroutines.flow.Flow

interface GifticonCropLocalDataSource {

    fun getGifticonCrop(gifticonId: String): Flow<GifticonCrop>

    suspend fun insertGifticonCrop(gifticonCropEntity: GifticonCropEntity)

    suspend fun updateGifticonCrop(gifticonCropEntity: GifticonCropEntity)
}
