package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.History
import com.lighthouse.domain.repository.GifticonRepository
import com.lighthouse.domain.util.currentTime
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UseCashCardGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    private val getUserLocationUseCase: GetUserLocationUseCase,
) {

    suspend operator fun invoke(gifticonId: String, amount: Int, hasLocationPermission: Boolean) {
        val userLocation = if (hasLocationPermission) getUserLocationUseCase().first() else null
        val history = History.UseCashCard(currentTime, gifticonId, amount, userLocation)

        gifticonRepository.useCashCardGifticon(gifticonId, amount, history)
    }
}
