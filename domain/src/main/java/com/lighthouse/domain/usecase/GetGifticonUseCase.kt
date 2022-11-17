package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Gifticon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Inject

class GetGifticonUseCase @Inject constructor() {

    // TODO (테스트 용 FAKE 제거)
    operator fun invoke(id: String): Flow<Gifticon> {
        return flowOf(
            Gifticon(
                id,
                "",
                "딸기마카롱설빙",
                "설빙", Date(),
                "998935552189",
                true,
                50000,
                "삡 그리고 다운 요 삡 그리고 다운",
                false
            )
        )
    }
}
