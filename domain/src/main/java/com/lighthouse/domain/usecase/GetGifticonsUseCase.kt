package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetGifticonsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    operator fun invoke(): Flow<DbResult<List<Gifticon>>> {
        return gifticonRepository.getAllGifticon()
    }

    operator fun invoke(query: String): Flow<List<Gifticon>> {
        return emptyFlow()
    }

    operator fun invoke(filter: List<String>): Flow<List<Gifticon>> {
        return emptyFlow()
    }
}
