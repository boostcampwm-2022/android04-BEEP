package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Gifticon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class GetNotificationGifticonUseCase {

    operator fun invoke(day: Int): Flow<List<Gifticon>> {
        return emptyFlow()
    }
}
