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
        is History.Use -> location?.let { LocationUiModel(it, geography.getAddress(location)) }
        is History.UseCashCard -> location?.let { LocationUiModel(it, geography.getAddress(location)) }
        else -> null
    }
    val amount = when (this) {
        is History.Init -> amount?.let {
            UIText.StringResource(
                R.string.all_cash_unit,
                it,
            )
        } ?: UIText.Empty

        is History.UseCashCard -> UIText.StringResource(
            R.string.all_cash_unit,
            amount,
        )

        is History.ModifyAmount -> UIText.StringResource(
            R.string.all_cash_unit,
            balance ?: throw IllegalStateException("balance should not be null"),
        )

        else -> UIText.Empty
    }
    val balance = when (this) {
        is History.Init -> this.amount?.let {
            UIText.StringResource(
                R.string.history_balance,
                it,
            )
        } ?: UIText.Empty

        is History.UseCashCard -> UIText.StringResource(
            R.string.history_balance,
            balance ?: throw IllegalStateException("balance should not be null"),
        )

        is History.ModifyAmount -> UIText.StringResource(
            R.string.history_balance,
            balance ?: throw IllegalStateException("balance should not be null"),
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
