package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.etc.SortBy
import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredGifticonsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {

    operator fun invoke(
        filter: Set<String>,
        sortBy: SortBy = SortBy.DEADLINE,
        filterExpired: Boolean = false
    ): Flow<Result<List<Gifticon>>> {
        return if (filter.isEmpty()) {
            gifticonRepository.getAllGifticons(
                userRepository.getUserId(),
                false,
                filterExpired,
                sortBy
            )
        } else {
            gifticonRepository.getFilteredGifticons(
                userRepository.getUserId(),
                false,
                filter,
                filterExpired,
                sortBy
            )
        }
    }
}
