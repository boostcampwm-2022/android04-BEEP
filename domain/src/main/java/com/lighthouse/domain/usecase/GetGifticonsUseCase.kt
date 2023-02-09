package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGifticonsUseCase @Inject constructor(
    private val userUserRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {
    operator fun invoke(
        isUsed: Boolean,
        filteredExpired: Boolean
    ): Flow<List<Gifticon>> {
        return gifticonRepository.getAllGifticons(
            userUserRepository.getUserId(),
            isUsed,
            filteredExpired
        )
    }
}
