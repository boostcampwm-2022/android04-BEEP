package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.domain.repository.gifticon.GifticonUsageHistoryRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsageHistoriesUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonUsageHistoryRepository
) {

    operator fun invoke(gifticonId: String): Flow<List<UsageHistory>> {
        return gifticonRepository.getUsageHistory(
            userRepository.getUserId(),
            gifticonId
        )
    }
}
