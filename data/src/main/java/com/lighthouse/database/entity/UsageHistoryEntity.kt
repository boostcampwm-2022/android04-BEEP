package com.lighthouse.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.lighthouse.database.entity.UsageHistoryEntity.Companion.USAGE_HISTORY_TABLE
import java.util.Date

@Entity(
    tableName = USAGE_HISTORY_TABLE,
    primaryKeys = ["gifticon_id", "date"],
    foreignKeys = [
        ForeignKey(
            entity = GifticonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("gifticon_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UsageHistoryEntity(
    @ColumnInfo(name = "gifticon_id") val gifticonId: String,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "amount") val amount: Int
) {
    companion object {
        const val USAGE_HISTORY_TABLE = "usage_history_table"
    }
}
