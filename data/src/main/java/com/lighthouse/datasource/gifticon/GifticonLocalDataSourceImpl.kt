package com.lighthouse.datasource.gifticon

import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.mapper.toGifticon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GifticonLocalDataSourceImpl @Inject constructor(
    private val gifticonDao: GifticonDao
) : GifticonLocalDataSource {

    override fun getGifticon(id: String): Flow<Gifticon> {
        return gifticonDao.getGifticon(id).map { entity ->
            entity.toGifticon()
        }
    }
}
