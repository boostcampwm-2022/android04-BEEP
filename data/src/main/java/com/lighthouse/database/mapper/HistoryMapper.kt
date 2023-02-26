package com.lighthouse.database.mapper

import com.lighthouse.database.entity.HistoryEntity
import com.lighthouse.domain.VertexLocation
import com.lighthouse.domain.model.History

fun HistoryEntity.toHistory(): History {
    val location: VertexLocation? = if (longitude != null && latitude != null) {
        VertexLocation(longitude, latitude)
    } else {
        null
    }
    return when (historyType) {
        0 -> {
            History.Init(date, gifticonId)
        }

        1 -> {
            History.Use(date, gifticonId, location)
        }

        2 -> {
            History.UseCashCard(
                date,
                gifticonId,
                amount ?: throw IllegalArgumentException("amount should not be null"),
                location,
            )
        }

        3 -> {
            History.ModifyAmount(
                date,
                gifticonId,
            )
        }

        4 -> {
            History.CancelUsage(date, gifticonId)
        }

        else -> throw IllegalArgumentException("Not supported History type")
    }
}

/**
 * @param balance 사용이나 사용 취소, 수정 등의 동작이 수행된 후의 최종 금액
 * */
fun History.toHistoryEntity(balance: Int?): HistoryEntity {
    return HistoryEntity(
        historyType = History.getType(this),
        gifticonId = gifticonId,
        date = date,
        longitude = when (this) {
            is History.Use -> location?.longitude
            is History.UseCashCard -> location?.longitude
            else -> null
        },
        latitude = when (this) {
            is History.Use -> location?.latitude
            is History.UseCashCard -> location?.latitude
            else -> null
        },
        balance = balance,
        amount = when (this) {
            is History.UseCashCard -> amount
            is History.ModifyAmount -> balance
            else -> null
        },
    )
}
