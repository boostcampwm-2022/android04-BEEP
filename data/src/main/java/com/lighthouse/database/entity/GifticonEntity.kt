package com.lighthouse.database.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lighthouse.database.entity.GifticonEntity.Companion.GIFTICON_TABLE
import java.util.Date

@Entity(tableName = GIFTICON_TABLE)
data class GifticonEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "has_image") val hasImage: Boolean,
    @ColumnInfo(name = "cropped_uri") val croppedUri: Uri?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "brand") val brand: String,
    @ColumnInfo(name = "expire_at") val expireAt: Date,
    @ColumnInfo(name = "barcode") val barcode: String,
    @ColumnInfo(name = "is_cash_card") val isCashCard: Boolean,
    @ColumnInfo(name = "balance") val balance: Int,
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "is_used") val isUsed: Boolean
) {
    companion object {
        const val GIFTICON_TABLE = "gifticon_table"
    }
}
