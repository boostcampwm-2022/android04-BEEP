package com.lighthouse.datasource.gifticoncrop

import com.lighthouse.database.dao.GifticonCropDao
import com.lighthouse.database.entity.GifticonCropEntity
import javax.inject.Inject

class GifticonCropLocalDataSourceImpl @Inject constructor(
    private val gifticonCropDao: GifticonCropDao
) : GifticonCropLocalDataSource {

    override suspend fun insertGifticonCrop(gifticonCropEntity: GifticonCropEntity) {
        gifticonCropDao.insertGifticonCrop(gifticonCropEntity)
    }

    override suspend fun updateGifticonCrop(gifticonCropEntity: GifticonCropEntity) {
        gifticonCropDao.updateGifticonCrop(gifticonCropEntity)
    }
}
