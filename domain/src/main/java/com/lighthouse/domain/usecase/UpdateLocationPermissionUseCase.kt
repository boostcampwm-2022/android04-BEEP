package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateLocationPermissionUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(hasPermission: Boolean) {
        locationRepository.updateLocationPermission(hasPermission)
    }
}
