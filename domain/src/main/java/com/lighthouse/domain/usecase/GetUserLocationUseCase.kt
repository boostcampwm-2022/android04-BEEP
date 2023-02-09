package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.location.VertexLocation
import com.lighthouse.domain.repository.location.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    operator fun invoke(): Flow<VertexLocation> = repository.getLocations()
}
