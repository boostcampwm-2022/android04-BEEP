package com.lighthouse.repository.location

import com.lighthouse.domain.repository.location.LocationRepository
import com.lighthouse.utils.location.SharedLocationManager
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val sharedLocationManager: SharedLocationManager
) : LocationRepository {

    override fun getLocations() = sharedLocationManager.locationFlow()
}
