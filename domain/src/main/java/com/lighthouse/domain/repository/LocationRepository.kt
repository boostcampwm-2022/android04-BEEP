package com.lighthouse.domain.repository

import com.lighthouse.domain.VertexLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocationInterval(): Flow<VertexLocation?>
}
