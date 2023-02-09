package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.brand.BrandWithGifticonCount
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBrandsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {
    operator fun invoke(filterExpired: Boolean = false): Flow<Result<List<BrandWithGifticonCount>>> {
        return gifticonRepository.getAllBrands(
            userRepository.getUserId(),
            false,
            filterExpired
        )
    }
}
