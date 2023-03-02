package com.lighthouse.presentation.model

import com.lighthouse.presentation.util.resource.UIText
import java.util.Date

sealed class HistoryUiModel {
    data class Header(val date: String) : HistoryUiModel()

    data class History(
        val date: Date,
        val type: UIText,
        val gifticonName: String,
        val balance: UIText,
        val location: String,
    ) : HistoryUiModel()
}
