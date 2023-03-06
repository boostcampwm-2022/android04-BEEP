package com.lighthouse.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.lighthouse.database.entity.HistoryEntity.Companion.HISTORY_TABLE
import java.util.Date

@Entity(
    tableName = HISTORY_TABLE,
    primaryKeys = ["history_type", "gifticon_id", "date"],
    foreignKeys = [
        ForeignKey(
            entity = GifticonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("gifticon_id"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class HistoryEntity(
    @ColumnInfo(name = "history_type") val historyType: Int,
    @ColumnInfo(name = "gifticon_id") val gifticonId: String,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "balance") val balance: Int?,
    @ColumnInfo(name = "amount") val amount: Int?,
) {
    companion object {
        const val HISTORY_TABLE = "history_table"
    }
}
