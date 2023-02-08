package com.lighthouse.beep.model.result

sealed class DbResult<out T> {
    data class Success<out T>(val data: T) : DbResult<T>()
    object Loading : DbResult<Nothing>()
    object Empty : DbResult<Nothing>()
    data class Failure(val throwable: Throwable) : DbResult<Nothing>()

    data class NotFoundError(val throwable: Throwable) : DbResult<Nothing>()
    data class SelectError(val throwable: Throwable) : DbResult<Nothing>()
    data class InsertError(val throwable: Throwable) : DbResult<Nothing>()
    data class UpdateError(val throwable: Throwable) : DbResult<Nothing>()
    data class DeleteError(val throwable: Throwable) : DbResult<Nothing>()
}
