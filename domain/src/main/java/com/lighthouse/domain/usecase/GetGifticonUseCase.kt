package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    operator fun invoke(id: String): Flow<DbResult<Gifticon>> {
        return gifticonRepository.getGifticon(id)
    }
}
