package com.lighthouse.mapper

import com.lighthouse.domain.model.BeepError
import com.lighthouse.model.BeepErrorData

internal fun BeepErrorData.toDomain(): BeepError = when (this) {
    is BeepErrorData.NetworkFailure -> BeepError.NetworkFailure
}
