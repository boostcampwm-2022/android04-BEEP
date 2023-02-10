package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.etc.SortBy
import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetFilteredGifticonsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        filter: Set<String>,
        sortBy: SortBy = SortBy.DEADLINE
    ): Flow<List<Gifticon>> {
        return userRepository.getFilterExpired().flatMapLatest { filterExpired ->
            if (filter.isEmpty()) {
                gifticonRepository.getAllGifticons(
                    userRepository.getUserId(),
                    false,
                    filterExpired.getOrDefault(false),
                    sortBy
                )
            } else {
                gifticonRepository.getFilteredGifticons(
                    userRepository.getUserId(),
                    false,
                    filter,
                    filterExpired.getOrDefault(false),
                    sortBy
                )
            }
        }
    }
}
