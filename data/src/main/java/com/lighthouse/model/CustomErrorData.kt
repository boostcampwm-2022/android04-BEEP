package com.lighthouse.model

sealed class CustomErrorData(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    object NetworkFailure : CustomErrorData()
    object NotFoundBrandPlaceInfos : CustomErrorData()
}
