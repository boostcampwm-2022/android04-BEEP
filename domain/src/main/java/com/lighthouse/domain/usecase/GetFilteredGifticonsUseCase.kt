package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredGifticonsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    authRepository: AuthRepository
) {
    val userId = authRepository.getCurrentUserId()

    operator fun invoke(filter: Set<String>): Flow<DbResult<List<Gifticon>>> {
        return if (filter.isEmpty()) {
            gifticonRepository.getAllGifticons(userId)
        } else {
            gifticonRepository.getFilteredGifticons(userId, filter)
        }
    }
}
