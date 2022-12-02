package com.lighthouse.domain.repository

import com.lighthouse.domain.VertexLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocations(): Flow<VertexLocation>
    fun hasLocationUpdates(): Flow<Boolean>
    fun updateLocationPermission(hasPermission: Boolean)
}
