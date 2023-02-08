package com.lighthouse.data.database.entity

import android.graphics.Rect
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lighthouse.data.database.entity.DBGifticonCropEntity.Companion.GIFTICON_CROP_TABLE

@Entity(
    tableName = GIFTICON_CROP_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = DBGifticonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("gifticon_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class DBGifticonCropEntity(
    @PrimaryKey
    @ColumnInfo(name = "gifticon_id")
    val gifticonId: String,
    @ColumnInfo(name = "cropped_rect")
    val croppedRect: Rect
) {
    companion object {
        const val GIFTICON_CROP_TABLE = "gifticon_crop_table"
    }
}
