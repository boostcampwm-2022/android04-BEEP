package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import com.lighthouse.domain.util.currentTime
import javax.inject.Inject

class UseGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {
    suspend operator fun invoke(gifticonId: String) {
        val usageHistory = UsageHistory(currentTime, "광주 광산구 일곡동", 0) // TODO 위치 얻어오기

        gifticonRepository.useGifticon(gifticonId, usageHistory)
    }
}
