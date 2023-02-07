package com.lighthouse.mapper

import com.lighthouse.beep.model.error.BeepError
import com.lighthouse.model.BeepErrorData

internal fun BeepErrorData.toDomain(): BeepError = when (this) {
    is BeepErrorData.NetworkFailure -> BeepError.NetworkFailure
}
