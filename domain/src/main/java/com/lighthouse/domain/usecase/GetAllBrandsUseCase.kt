package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBrandsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {
    // TODO userId 사용
    operator fun invoke(userId: String = ""): Flow<DbResult<List<Brand>>> {
        return gifticonRepository.getAllBrands()
    }
}
