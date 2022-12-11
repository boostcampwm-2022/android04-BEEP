package com.lighthouse.datasource.gifticoncrop

import com.lighthouse.database.dao.GifticonCropDao
import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.database.mapper.toDomain
import com.lighthouse.domain.model.GifticonCrop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GifticonCropLocalDataSourceImpl @Inject constructor(
    private val gifticonCropDao: GifticonCropDao
) : GifticonCropLocalDataSource {

    override fun getGifticonCrop(gifticonId: String): Flow<GifticonCrop> {
        return gifticonCropDao.getGifticonCrop(gifticonId).map {
            it.toDomain()
        }
    }

    override suspend fun insertGifticonCrop(gifticonCropEntity: GifticonCropEntity) {
        gifticonCropDao.insertGifticonCrop(gifticonCropEntity)
    }

    override suspend fun updateGifticonCrop(gifticonCropEntity: GifticonCropEntity) {
        gifticonCropDao.updateGifticonCrop(gifticonCropEntity)
    }
}
