package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Gifticon

class UpdateGifticonInfoUseCase {

    operator fun invoke(gifticon: Gifticon): Result<Unit> {
        return Result.success(Unit)
    }
}
