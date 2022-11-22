package com.lighthouse.domain.repository

import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import kotlinx.coroutines.flow.Flow

interface GifticonRepository {

    fun getGifticon(id: String): Flow<DbResult<Gifticon>>
    suspend fun saveGifticons(gifticons: List<Gifticon>)
//  TODO: fun getGifticonSharedDate(id: String): Result<Flow<Date?>>
//  TODO: fun getGifticonUsageHistory(id: String): Result<Flow<List<UsageHistory>>>
}
