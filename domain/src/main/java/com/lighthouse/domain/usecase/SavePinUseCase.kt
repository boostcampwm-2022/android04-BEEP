package com.lighthouse.domain.usecase

class SavePinUseCase {

    operator fun invoke(pin: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
