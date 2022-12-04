package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.LocationRepository
import javax.inject.Inject

class HasLocationPermissionsUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    operator fun invoke() = repository.hasLocationUpdates()
}
