package com.lighthouse.model

sealed class BeepErrorData(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    object NetworkFailure : BeepErrorData()
}
