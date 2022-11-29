package com.lighthouse.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class GetAllBrandsUseCase {

    operator fun invoke(userId: String): Flow<List<String>> {
        return emptyFlow()
    }
}
