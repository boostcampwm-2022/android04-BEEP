package com.lighthouse.domain.usecase

class UpdateUsageHistoryUseCase {

    operator fun invoke(id: String): Result<Unit> {
        return Result.success(Unit)
    }
}
