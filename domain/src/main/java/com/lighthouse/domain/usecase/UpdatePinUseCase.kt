package com.lighthouse.domain.usecase

class UpdatePinUseCase {

    operator fun invoke(pin: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
