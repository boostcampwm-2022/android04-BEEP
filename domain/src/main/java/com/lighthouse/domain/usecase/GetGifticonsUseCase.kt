package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Gifticon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class GetGifticonsUseCase {

    operator fun invoke(userId: String): Flow<List<Gifticon>> {
        return emptyFlow()
    }

    operator fun invoke(userId: String, query: String): Flow<List<Gifticon>> {
        return emptyFlow()
    }

    operator fun invoke(userId: String, filter: List<String>): Flow<List<Gifticon>> {
        return emptyFlow()
    }
}
