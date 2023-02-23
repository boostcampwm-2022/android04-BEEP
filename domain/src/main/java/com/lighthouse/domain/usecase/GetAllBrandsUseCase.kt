package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetAllBrandsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    authRepository: AuthRepository,
) {
    val userId = authRepository.getCurrentUserId()

    operator fun invoke(): Flow<DbResult<List<Brand>>> {
        return gifticonRepository.getAllBrands(userId).transform {
            if (it is DbResult.Success) {
                emit(DbResult.Success(it.data.sortedByDescending { brand -> brand.count }))
            } else {
                emit(it)
            }
        }
    }
}
