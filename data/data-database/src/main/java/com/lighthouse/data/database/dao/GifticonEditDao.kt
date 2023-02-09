package com.lighthouse.data.database.dao

import android.graphics.Rect
import android.net.Uri
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lighthouse.data.database.entity.DBGifticonCropEntity
import com.lighthouse.data.database.entity.DBGifticonEntity
import com.lighthouse.data.database.exception.DBNotFoundException
import com.lighthouse.data.database.mapper.gifticon.edit.toGifticonCropEntity
import com.lighthouse.data.database.mapper.gifticon.edit.toGifticonEntity
import com.lighthouse.data.database.model.DBGifticonWithCrop
import java.util.Date

@Dao
internal interface GifticonEditDao {

    /**
     * 기프티콘 정보를 추가한다
     * 1. 기프티콘
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGifticon(gifticon: DBGifticonEntity)

    /**
     * 기프티콘 크롭 정보를 추가한다
     * 1. 기프티콘 크롭 정보
     * */
    @Insert
    suspend fun insertGifticonCrop(cropEntity: DBGifticonCropEntity)

    /**
     * 기프티콘 정보와 크롭 정보를 추가한다
     * 1. 기프티콘 과 크롭정보가 합쳐진 객체
     * */
    @Transaction
    suspend fun insertGifticonWithCrop(gifticonWithCropList: List<DBGifticonWithCrop>) {
        gifticonWithCropList.forEach {
            insertGifticon(it.toGifticonEntity())
            insertGifticonCrop(it.toGifticonCropEntity())
        }
    }

    /**
     * 기프티콘의 정보를 업데이트한다
     * 1. 유저 ID
     * etc. 변경될 정보들
     * */
    @Query(
        "UPDATE gifticon_table " +
            "SET cropped_uri = :croppedUri, " +
            "name = :name, " +
            "brand = :brand, " +
            "expire_at = :expire_at, " +
            "barcode = :barcode, " +
            "is_cash_card = :isCashCard, " +
            "balance = :balance, " +
            "memo = :memo " +
            "WHERE user_id = :userId AND id = :gifticonId"
    )
    suspend fun updateGifticon(
        userId: String,
        gifticonId: String,
        croppedUri: Uri?,
        name: String,
        brand: String,
        expire_at: Date,
        barcode: String,
        isCashCard: Boolean,
        balance: Int,
        memo: String
    ): Int

    /**
     * 기프티콘의 크롭 정보를 업데이트한다
     * 1. 유저 ID
     * 2. 기프티콘 ID
     * 3. 크롭 Rect
     * */
    @Query(
        "UPDATE gifticon_crop_table " +
            "SET cropped_rect = :croppedRect " +
            "WHERE gifticon_id = :gifticonId AND " +
            "EXISTS(SELECT 1 FROM gifticon_table WHERE user_id = :userId)"
    )
    suspend fun updateGifticonCrop(userId: String, gifticonId: String, croppedRect: Rect): Int

    /**
     * 기프티콘 정보가 크롭 정보를 업데이트한다
     * 1. 유저 ID
     * 2. 업데이트할 기프티콘 정보
     *
     * DBNotFoundException
     * 업데이트 된 정보가 없을 경우 에러
     * */
    @Transaction
    suspend fun updateGifticonWithCrop(
        gifticonWithCrop: DBGifticonWithCrop
    ) {
        with(gifticonWithCrop) {
            val updatedCount = updateGifticon(
                userId,
                id,
                croppedUri,
                name,
                brand,
                expireAt,
                barcode,
                isCashCard,
                balance,
                memo
            )
            if (updatedCount == 0) {
                throw DBNotFoundException("업데이트할 기프티콘을 찾을 수 없습니다.")
            }
            updateGifticonCrop(userId, id, croppedRect)
        }
    }

    /**
     * 기프티콘 정보 삭제한다
     * 1. 유저 ID
     * 2. 기프티콘 ID
     * */
    @Query(
        "DELETE FROM gifticon_table" +
            " WHERE user_id = :userId AND id = :gifticonId"
    )
    suspend fun deleteGifticon(userId: String, gifticonId: String): Int

    /**
     * 특정 유저의 기프티콘 정보를 다른 유저에게 넘긴다
     * 1. 기존 유저 ID
     * 2. 새로운 유저 ID
     * */
    @Query(
        "UPDATE gifticon_table " +
            "SET user_id = :newUserId " +
            "WHERE user_id = :oldUserId"
    )
    suspend fun moveUserIdGifticon(oldUserId: String, newUserId: String)
}
