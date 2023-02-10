package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetGifticonsUseCase @Inject constructor(
    private val userUserRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {

    operator fun invoke(
        isUsed: Boolean
    ): Flow<List<Gifticon>> {
        return userUserRepository.getFilterExpired().flatMapLatest { filterExpired ->
            gifticonRepository.getAllGifticons(
                userUserRepository.getUserId(),
                isUsed,
                filterExpired.getOrDefault(false)
            )
        }
    }

    operator fun invoke(
        count: Int
    ): Flow<List<Gifticon>> {
        return gifticonRepository.getGifticons(
            userUserRepository.getUserId(),
            count
        )
    }
}
