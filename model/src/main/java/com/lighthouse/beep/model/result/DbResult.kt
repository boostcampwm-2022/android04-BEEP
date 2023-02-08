package com.lighthouse.beep.model.result

sealed class DbResult<out T> {
    data class Success<out T>(val data: T) : DbResult<T>()
    object Loading : DbResult<Nothing>()
    object Empty : DbResult<Nothing>()
    data class Failure(val throwable: Throwable) : DbResult<Nothing>()

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure
}
