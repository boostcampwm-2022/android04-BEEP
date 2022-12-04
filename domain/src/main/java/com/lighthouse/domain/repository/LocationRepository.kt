package com.lighthouse.domain.repository

import com.lighthouse.domain.VertexLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {

    fun getLocations(): Flow<VertexLocation>
    fun hasLocationUpdates(): StateFlow<Boolean>
    fun updateLocationPermission(hasPermission: Boolean)
}
