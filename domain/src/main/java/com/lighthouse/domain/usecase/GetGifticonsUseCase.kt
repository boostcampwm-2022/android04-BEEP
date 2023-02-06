package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGifticonsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    authRepository: AuthRepository
) {
    val userId = authRepository.getCurrentUserId()

    operator fun invoke(): Flow<DbResult<List<Gifticon>>> {
        return gifticonRepository.getAllGifticons(userId)
    }

    fun getUsableGifticons(): Flow<DbResult<List<Gifticon>>> {
        return gifticonRepository.getUsableGifticons(userId)
    }

    fun getUsedGifticons(): Flow<DbResult<List<Gifticon>>> {
        return gifticonRepository.getAllUsedGifticons(userId)
    }

    fun getGifticonBrands(): Flow<DbResult<List<String>>> {
        return gifticonRepository.getGifticonBrands(userId)
    }

    fun getSomeGifticons(count: Int): Flow<DbResult<List<Gifticon>>> {
        return gifticonRepository.getSomeGifticons(userId, count)
    }
}
