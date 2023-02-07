package com.lighthouse.beep.model.error

sealed class BeepError(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    object NetworkFailure : BeepError()
}
