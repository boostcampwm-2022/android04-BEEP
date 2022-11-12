package com.lighthouse.domain.model

sealed class CustomError(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    object NetworkFailure : CustomError()
}
