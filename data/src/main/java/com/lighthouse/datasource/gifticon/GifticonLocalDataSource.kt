package com.lighthouse.datasource.gifticon

import com.lighthouse.domain.model.Gifticon
import kotlinx.coroutines.flow.Flow

interface GifticonLocalDataSource {
    fun getGifticon(id: String): Flow<Gifticon>
}
