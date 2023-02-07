package com.lighthouse.beep.model.result

sealed class DbResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : DbResult<T>()
    object Loading : DbResult<Nothing>()
    object Empty : DbResult<Nothing>()
    data class Failure(val throwable: Throwable) : DbResult<Nothing>()
}
