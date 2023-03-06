package com.lighthouse.domain.model

import com.lighthouse.domain.VertexLocation
import java.util.Date

sealed interface History {
    val date: Date
    val gifticonId: String

    data class Init(
        override val date: Date,
        override val gifticonId: String,
        val amount: Int? = null,
    ) : History

    data class Use(
        override val date: Date,
        override val gifticonId: String,
        val location: VertexLocation? = null,
    ) : History

    data class UseCashCard(
        override val date: Date,
        override val gifticonId: String,
        val amount: Int,
        val balance: Int? = null,
        val location: VertexLocation? = null,
    ) : History

    data class CancelUsage(
        override val date: Date,
        override val gifticonId: String,
    ) : History

    data class ModifyAmount(
        override val date: Date,
        override val gifticonId: String,
        val balance: Int? = null,
    ) : History

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
