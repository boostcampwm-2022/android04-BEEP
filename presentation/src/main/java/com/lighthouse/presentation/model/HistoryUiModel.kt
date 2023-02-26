package com.lighthouse.presentation.model

import java.util.Date

sealed class HistoryUiModel {
    data class Header(val date: String) : HistoryUiModel()

    data class History(
        val date: Date,
        val type: String,
        val gifticonName: String,
        val balance: String,
        val location: String,
    ) : HistoryUiModel()
}
