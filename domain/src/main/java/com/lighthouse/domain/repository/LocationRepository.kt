package com.lighthouse.domain.repository

import com.lighthouse.domain.VertexLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface LocationRepository {

    fun getLocationInterval(): Flow<VertexLocation>

    fun getLastLocation(): MutableStateFlow<VertexLocation?>
}
