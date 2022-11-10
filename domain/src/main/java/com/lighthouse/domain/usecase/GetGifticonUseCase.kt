package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Gifticon
import java.util.Date

class GetGifticonUseCase {

    operator fun invoke(id: String): Gifticon {
        return Gifticon("", "", "", "", Date(), "", true, 0, "", false)
    }
}
