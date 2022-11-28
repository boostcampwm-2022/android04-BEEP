package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.LocationRepository
import javax.inject.Inject

class GetUserLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    operator fun invoke() = repository.getLocationInterval()

    fun lastLocation() = repository.getLastLocation()
}
