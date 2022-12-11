package com.lighthouse.domain.usecase.detail

import com.lighthouse.domain.model.GifticonCrop
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGifticonCropUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {
    operator fun invoke(gifticonId: String): Flow<GifticonCrop> {
        return gifticonRepository.getGifticonCrop(gifticonId)
    }
}
