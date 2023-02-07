package com.lighthouse.database.entity

import android.graphics.Rect
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lighthouse.database.entity.GifticonCropEntity.Companion.GIFTICON_CROP_TABLE

@Entity(
    tableName = GIFTICON_CROP_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = GifticonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("gifticon_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GifticonCropEntity(
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
