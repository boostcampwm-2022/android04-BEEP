package com.lighthouse.datasource.gifticoncrop

import com.lighthouse.domain.model.GifticonCrop
import kotlinx.coroutines.flow.Flow

interface GifticonCropLocalDataSource {

    fun getGifticonCrop(gifticonId: String): Flow<GifticonCrop>

    suspend fun insertGifticonCrop(gifticonCrop: GifticonCrop)

    suspend fun updateGifticonCrop(gifticonCrop: GifticonCrop)
}
