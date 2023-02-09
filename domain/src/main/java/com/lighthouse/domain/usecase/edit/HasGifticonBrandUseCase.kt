package com.lighthouse.domain.usecase.edit

import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class HasGifticonBrandUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {
    suspend operator fun invoke(brand: String): Result<Boolean> {
        return gifticonRepository.hasGifticonBrand(userRepository.getUserId(), brand)
    }
}
