package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.History
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toString
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.model.HistoryUiModel
import com.lighthouse.presentation.util.Geography
import com.lighthouse.presentation.util.resource.UIText

fun List<History>.toUiModel(gifticon: GifticonUIModel, geography: Geography): List<HistoryUiModel> {
    val histories = this@toUiModel
    val dateFormat = "yyyy-MM-dd"
    return histories.fold(mutableListOf()) { acc, history ->
        if (acc.isEmpty()) {
            history.date
            acc.add(HistoryUiModel.Header(history.date.toString(dateFormat)))
        } else if (acc.last() is HistoryUiModel.History && (acc.last() as HistoryUiModel.History).date.toString(
                dateFormat,
            ) != history.date.toString(dateFormat)
        ) {
            acc.add(HistoryUiModel.Header(history.date.toString(dateFormat)))
        }

        val typeRes = when (history) {
            is History.Init -> R.string.history_type_init
            is History.Use -> R.string.history_type_use
            is History.UseCashCard -> R.string.history_type_use
            is History.CancelUsage -> R.string.history_type_cancel
            is History.ModifyAmount -> R.string.history_type_modify_balance
        }

        val location = when (history) {
            is History.Use -> geography.getAddress(history.location)
            is History.UseCashCard -> geography.getAddress(history.location)
            else -> ""
        }
        val balance = when (history) {
            is History.UseCashCard -> UIText.StringResource(
                R.string.all_cash_unit,
                history.balance ?: throw IllegalStateException("balance should not be null"),
            )

            is History.ModifyAmount -> UIText.StringResource(
                R.string.all_cash_unit,
                history.balance ?: throw IllegalStateException("balance should not be null"),
            )

            else -> UIText.Empty
        }
        acc.add(
            HistoryUiModel.History(
                date = history.date,
                type = UIText.StringResource(typeRes),
                gifticonName = gifticon.name,
                balance = balance,
                location = location,
            ),
        )
        acc
    }
}
