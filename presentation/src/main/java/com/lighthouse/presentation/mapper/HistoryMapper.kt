package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.History
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toString
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.model.HistoryUiModel
import com.lighthouse.presentation.model.LocationUiModel
import com.lighthouse.presentation.util.Geography
import com.lighthouse.presentation.util.resource.UIText

fun History.toUiModel(gifticonName: String, geography: Geography): HistoryUiModel.History {
    val typeRes = when (this) {
        is History.Init -> R.string.history_type_init
        is History.Use -> R.string.history_type_use
        is History.UseCashCard -> R.string.history_type_use
        is History.CancelUsage -> R.string.history_type_cancel
        is History.ModifyAmount -> R.string.history_type_modify_balance
    }
    val location = when (this) {
        is History.Use -> location?.let { LocationUiModel(it, geography.getAddress(it)) }
        is History.UseCashCard -> location?.let { LocationUiModel(it, geography.getAddress(it)) }
        else -> null
    }
    val amount = when (this) {
        is History.Init -> amount?.let {
            UIText.NumberFormatString(
                it,
                R.string.all_cash_unit,
            )
        } ?: UIText.Empty

        is History.UseCashCard -> UIText.NumberFormatString(
            amount,
            R.string.all_cash_unit,
        )

        is History.ModifyAmount -> UIText.NumberFormatString(
            balance ?: throw IllegalStateException("balance should not be null"),
            R.string.all_cash_unit,
        )

        else -> UIText.Empty
    }
    val balance = when (this) {
        is History.Init -> this.amount?.let {
            UIText.NumberFormatString(
                it,
                R.string.history_balance,
            )
        } ?: UIText.Empty

        is History.UseCashCard -> UIText.NumberFormatString(
            balance ?: throw IllegalStateException("balance should not be null"),
            R.string.history_balance,
        )

        is History.ModifyAmount -> UIText.NumberFormatString(
            balance ?: throw IllegalStateException("balance should not be null"),
            R.string.history_balance,
        )

        else -> UIText.Empty
    }
    return HistoryUiModel.History(
        date = date,
        type = UIText.StringResource(typeRes),
        gifticonName = gifticonName,
        amount = amount,
        balance = balance,
        location = location,
    )
}

fun List<History>.toUiModel(gifticon: GifticonUIModel, geography: Geography): List<HistoryUiModel> {
    val histories = this@toUiModel
    val dateFormat = "yyyy-MM-dd"
    return histories.fold(mutableListOf()) { acc, history ->
        if (acc.isEmpty()) {
            history.date
            acc.add(HistoryUiModel.Header(history.date.toString(dateFormat)))
        } else if (
            acc.last() is HistoryUiModel.History &&
            (acc.last() as HistoryUiModel.History).date.toString(dateFormat) != history.date.toString(dateFormat)
        ) {
            acc.add(HistoryUiModel.Header(history.date.toString(dateFormat)))
        }
        acc.also {
            it.add(history.toUiModel(gifticon.name, geography))
        }
    }
}
