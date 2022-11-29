package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGifticonsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    operator fun invoke(): Flow<DbResult<List<Gifticon>>> {
        return gifticonRepository.getAllGifticons()
    }
}
