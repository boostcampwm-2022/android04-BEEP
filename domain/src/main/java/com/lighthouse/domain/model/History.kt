package com.lighthouse.domain.model

import com.lighthouse.domain.VertexLocation
import java.util.Date

sealed class History(open val date: Date, open val gifticonId: String) {
    data class Init(
        override val date: Date,
        override val gifticonId: String,
    ) : History(date, gifticonId)

    data class Use(
        override val date: Date,
        override val gifticonId: String,
        val location: VertexLocation? = null,
    ) : History(date, gifticonId)

    data class UseCashCard(
        override val date: Date,
        override val gifticonId: String,
        val amount: Int,
        val location: VertexLocation? = null,
    ) : History(date, gifticonId)

    data class CancelUsage(
        override val date: Date,
        override val gifticonId: String,
    ) : History(date, gifticonId)

    data class ModifyAmount(
        override val date: Date,
        override val gifticonId: String,
        val newAmount: Int,
    ) : History(date, gifticonId)

    companion object {
        fun getType(history: History): Int = when (history) {
            is Init -> 0
            is Use -> 1
            is UseCashCard -> 2
            is ModifyAmount -> 3
            is CancelUsage -> 4
        }
    }
}
