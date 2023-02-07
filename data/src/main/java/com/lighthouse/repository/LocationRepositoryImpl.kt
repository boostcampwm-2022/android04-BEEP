package com.lighthouse.repository

import com.lighthouse.common.utils.location.SharedLocationManager
import com.lighthouse.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val sharedLocationManager: SharedLocationManager
) : LocationRepository {

    override fun getLocations() = sharedLocationManager.locationFlow()
}
