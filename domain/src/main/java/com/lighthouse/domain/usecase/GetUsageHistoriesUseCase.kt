package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsageHistoriesUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    operator fun invoke(gifticonId: String): Flow<DbResult<List<UsageHistory>>> {
        return gifticonRepository.getUsageHistory(gifticonId)
    }
}
