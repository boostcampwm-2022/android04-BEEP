package com.lighthouse.presentation.ui.common

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val item: T) : UiState<T>()
    object NetworkFailure : UiState<Nothing>()
    object NotFoundResults : UiState<Nothing>()
    data class Failure(val throwable: Throwable) : UiState<Nothing>()
}
