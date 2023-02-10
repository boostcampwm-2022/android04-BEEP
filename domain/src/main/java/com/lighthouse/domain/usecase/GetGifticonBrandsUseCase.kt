package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGifticonBrandsUseCase @Inject constructor(
    private val userUserRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return gifticonRepository.getGifticonBrands(userUserRepository.getUserId())
    }
}
