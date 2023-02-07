package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.etc.SortBy
import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredGifticonsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    authRepository: AuthRepository
) {
    val userId = authRepository.getCurrentUserId()

    operator fun invoke(filter: Set<String>, sortBy: SortBy = SortBy.DEADLINE): Flow<DbResult<List<Gifticon>>> {
        return if (filter.isEmpty()) {
            gifticonRepository.getAllGifticons(userId, sortBy)
        } else {
            gifticonRepository.getFilteredGifticons(userId, filter, sortBy)
        }
    }
}
