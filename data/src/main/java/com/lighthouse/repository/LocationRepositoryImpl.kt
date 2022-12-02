package com.lighthouse.repository

import com.lighthouse.datasource.location.SharedLocationManager
import com.lighthouse.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val sharedLocationManager: SharedLocationManager
) : LocationRepository {

    override fun hasLocationUpdates(): Flow<Boolean> = sharedLocationManager.receivingLocationUpdates
    override fun getLocations() = sharedLocationManager.locationFlow()
    override fun updateLocationPermission(hasPermission: Boolean) {
        sharedLocationManager.changePermission(hasPermission)
    }
}
