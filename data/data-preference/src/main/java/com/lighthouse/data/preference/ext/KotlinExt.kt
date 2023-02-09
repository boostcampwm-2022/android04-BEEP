package com.lighthouse.data.preference.ext

import com.lighthouse.beep.model.exception.common.NotFoundException
import com.lighthouse.data.preference.exception.PrefNotFoundException

internal inline fun <T, R> T.runCatchingPref(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: PrefNotFoundException) {
        Result.failure(NotFoundException(e.message))
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
