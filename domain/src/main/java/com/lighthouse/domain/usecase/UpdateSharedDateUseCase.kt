package com.lighthouse.domain.usecase

class UpdateSharedDateUseCase {

    operator fun invoke(id: String): Result<Unit> {
        return Result.success(Unit)
    }
}
