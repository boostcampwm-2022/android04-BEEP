package com.lighthouse.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.lighthouse.data.database.entity.DBUsageHistoryEntity.Companion.USAGE_HISTORY_TABLE
import java.util.Date

@Entity(
    tableName = USAGE_HISTORY_TABLE,
    primaryKeys = ["gifticon_id", "date"],
    foreignKeys = [
        ForeignKey(
            entity = DBGifticonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("gifticon_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class DBUsageHistoryEntity(
    @ColumnInfo(name = "gifticon_id") val gifticonId: String,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "amount") val amount: Int
) {
    companion object {
        const val USAGE_HISTORY_TABLE = "usage_history_table"
    }
}
