package com.lighthouse.domain.usecase.detail

import com.lighthouse.domain.model.GifticonCropForUpdate
import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class UpdateGifticonCropUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    suspend operator fun invoke(gifticonCropForUpdate: GifticonCropForUpdate) {
        gifticonRepository.updateGifticonCrop(gifticonCropForUpdate)
    }
}
