package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.History
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    val repository: GifticonRepository,
) {
    operator fun invoke(gifticonId: String): Flow<DbResult<List<History>>> {
        return repository.getHistory(gifticonId)
    }
}
