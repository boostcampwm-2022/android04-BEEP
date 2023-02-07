package com.lighthouse.domain.repository

import com.lighthouse.beep.model.location.VertexLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocations(): Flow<VertexLocation>
}
