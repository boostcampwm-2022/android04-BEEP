package com.lighthouse.database.entity

import android.graphics.Rect
import android.net.Uri
import androidx.room.ColumnInfo
import java.util.Date

class GifticonWithCrop(
    @ColumnInfo(name = "id") val id: String,
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
    @ColumnInfo(name = "cropped_rect") val croppedRect: Rect,
    @ColumnInfo(name = "is_used") val isUsed: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Date
)
