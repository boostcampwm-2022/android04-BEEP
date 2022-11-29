package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import java.util.Date
import javax.inject.Inject

class UseCashCardGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    suspend operator fun invoke(gifticonId: String, amount: Int) {
        val usageHistory = UsageHistory(Date(), "광주 광산구 일곡동", amount) // TODO 위치 얻어오기
        gifticonRepository.useCashCardGifticon(gifticonId, amount, usageHistory)
    }
}
