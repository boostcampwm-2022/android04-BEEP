package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.brand.BrandWithGifticonCount
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetAllBrandsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<BrandWithGifticonCount>> {
        val filterExpiredFlow = userRepository.getFilterExpired()
        return filterExpiredFlow.flatMapLatest { filterExpired ->
            gifticonRepository.getAllBrands(
                userRepository.getUserId(),
                false,
                filterExpired.getOrDefault(false)
            )
        }
    }
}
