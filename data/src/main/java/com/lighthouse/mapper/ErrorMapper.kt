package com.lighthouse.mapper

import com.lighthouse.domain.model.CustomError
import com.lighthouse.model.CustomErrorData

internal fun CustomErrorData.toDomain(): CustomError = when (this) {
    is CustomErrorData.NetworkFailure -> CustomError.NetworkFailure
    is CustomErrorData.NotFoundBrandPlaceInfos -> CustomError.EmptyResults
}
