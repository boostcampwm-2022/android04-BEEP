package com.lighthouse.database.dao

import android.graphics.Rect
import android.net.Uri
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.database.entity.GifticonCropEntity.Companion.GIFTICON_CROP_TABLE
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.GifticonEntity.Companion.GIFTICON_TABLE
import com.lighthouse.database.entity.GifticonWithCrop
import com.lighthouse.database.entity.HistoryEntity
import com.lighthouse.database.entity.HistoryEntity.Companion.HISTORY_TABLE
import com.lighthouse.database.mapper.toGifticonCropEntity
import com.lighthouse.database.mapper.toGifticonEntity
import com.lighthouse.domain.model.Brand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date

@Dao
interface GifticonDao {

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE id = :id")
    fun getGifticon(id: String): Flow<GifticonEntity>

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE user_id = :userId AND is_used = 0 ORDER BY created_at DESC")
    fun getAllGifticons(userId: String): Flow<List<GifticonEntity>>

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE user_id = :userId AND is_used = 1 ORDER BY created_at DESC")
    fun getAllUsedGifticons(userId: String): Flow<List<GifticonEntity>>

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE user_id = :userId AND expire_at >= :time AND is_used = 0 ORDER BY expire_at")
    fun getAllUsableGifticons(userId: String, time: Date): Flow<List<GifticonEntity>>

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE user_id = :userId AND is_used = 0 ORDER BY expire_at")
    fun getAllGifticonsSortByDeadline(userId: String): Flow<List<GifticonEntity>>

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE user_id = :userId AND is_used = 0 AND UPPER(brand) IN(:filters) ORDER BY created_at DESC")
    fun getFilteredGifticons(userId: String, filters: Set<String>): Flow<List<GifticonEntity>>

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE user_id = :userId AND is_used = 0 AND UPPER(brand) IN(:filters) ORDER BY expire_at")
    fun getFilteredGifticonsSortByDeadline(
        userId: String,
        filters: Set<String>,
    ): Flow<List<GifticonEntity>>

    @Query(
        "SELECT * FROM $GIFTICON_TABLE " +
            "INNER JOIN $GIFTICON_CROP_TABLE " +
            "ON $GIFTICON_TABLE.id = $GIFTICON_CROP_TABLE.gifticon_id " +
            "WHERE id = :id AND user_id = :userId " +
            "LIMIT 1",
    )
    suspend fun getGifticonWithCrop(userId: String, id: String): GifticonWithCrop?

    @Query(
        "SELECT brand AS name, COUNT(*) AS count " +
            "FROM $GIFTICON_TABLE " +
            "WHERE user_id = :userId " +
            "GROUP BY brand ORDER BY count DESC",
    )
    fun getAllBrands(userId: String): Flow<List<Brand>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGifticon(gifticon: GifticonEntity)

    @Insert
    suspend fun insertGifticonCrop(cropEntity: GifticonCropEntity)

    @Transaction
    suspend fun insertGifticonWithCropTransaction(gifticonWithCropList: List<GifticonWithCrop>) {
        gifticonWithCropList.forEach {
            insertGifticon(it.toGifticonEntity())
            insertGifticonCrop(it.toGifticonCropEntity())
        }
    }

    /**
     * 기프티콘을 사용 상태로 변경한다
     * */
    @Query("UPDATE $GIFTICON_TABLE SET is_used = 1 WHERE id = :gifticonId")
    suspend fun useGifticon(gifticonId: String)

    /**
     * 기프티콘을 사용 안 됨 상태로 변경한다
     * */
    @Query("UPDATE $GIFTICON_TABLE SET is_used = 0 WHERE id = :gifticonId")
    suspend fun unUseGifticon(gifticonId: String)

    /**
     * 금액권 기프티콘의 잔액을 차감한다
     * */
    @Query("UPDATE $GIFTICON_TABLE SET balance = :balance WHERE id = :gifticonId")
    fun useCashCardGifticon(gifticonId: String, balance: Int)

    /**
     * 기프티콘을 삭제한다
     */
    @Query("DELETE FROM $GIFTICON_TABLE WHERE id = :gifticonId")
    suspend fun removeGifticon(gifticonId: String)

    /**
     * 기프티콘의 사용 기록을 조회한다
     * */
    @Query("SELECT * FROM $HISTORY_TABLE WHERE gifticon_id = :gifticonId")
    fun getUsageHistory(gifticonId: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM $HISTORY_TABLE WHERE gifticon_id = :gifticonId ORDER BY date DESC")
    fun getHistory(gifticonId: String): Flow<List<HistoryEntity>>

    @Query(
        "SELECT balance FROM $HISTORY_TABLE " +
            "WHERE gifticon_id = :gifticonId ORDER BY date DESC LIMIT 1",
    )
    suspend fun getLatestAmount(gifticonId: String): Int?

    @Query(
        "SELECT balance FROM $HISTORY_TABLE " +
            "WHERE gifticon_id = :gifticonId ORDER BY date DESC LIMIT 1 OFFSET 1",
    )
    suspend fun getSecondLatestAmount(gifticonId: String): Int?

    /**
     * 기프티콘의 사용 기록을 추가한다
     * */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsageHistory(history: HistoryEntity)

    /**
     * 기프티콘의 정보를 업데이트한다
     * */
    @Query(
        "UPDATE $GIFTICON_TABLE " +
            "SET cropped_uri = :croppedUri, " +
            "name = :name, " +
            "brand = :brand, " +
            "expire_at = :expire_at, " +
            "barcode = :barcode, " +
            "is_cash_card = :isCashCard, " +
            "balance = :balance, " +
            "memo = :memo " +
            "WHERE id = :id",
    )
    suspend fun updateGifticon(
        id: String,
        croppedUri: Uri?,
        name: String,
        brand: String,
        expire_at: Date,
        barcode: String,
        isCashCard: Boolean,
        balance: Int,
        memo: String,
    )

    @Query("SELECT is_cash_card FROM $GIFTICON_TABLE WHERE id = :gifticonId")
    fun isCashCard(gifticonId: String): Boolean

    @Query("UPDATE $GIFTICON_CROP_TABLE SET cropped_rect = :croppedRect WHERE gifticon_id = :id")
    suspend fun updateGifticonCrop(id: String, croppedRect: Rect)

    @Transaction
    suspend fun updateGifticonWithCropTransaction(gifticonWithCrop: GifticonWithCrop) {
        with(gifticonWithCrop) {
            updateGifticon(
                id,
                croppedUri,
                name,
                brand,
                expireAt,
                barcode,
                isCashCard,
                balance,
                memo,
            )
            updateGifticonCrop(id, croppedRect)
        }
    }

    /**
     * 기프티콘을 사용 상태로 변경하고, 사용 기록에 추가한다
     * */
    @Transaction
    suspend fun useGifticonTransaction(history: HistoryEntity) {
        val gifticonId = history.gifticonId

        useGifticon(gifticonId)
        insertUsageHistory(history)
    }

    /**
     * 금액권 기프티콘의 잔액을 차감하고 사용 기록에 추가한다. 잔액이 0원이 된다면 사용 상태로 변경한다
     * */
    @Transaction
    suspend fun useCashCardGifticonTransaction(amount: Int, history: HistoryEntity) {
        val gifticonId = history.gifticonId
        val balance = getLatestAmount(gifticonId) ?: throw IllegalStateException("balance should not be null")

        assert(balance >= amount) // 사용할 금액이 잔액보다 많으면 안된다

        useCashCardGifticon(gifticonId, balance - amount)
        insertUsageHistory(history)

        if (balance == amount) {
            useGifticon(gifticonId)
        }
    }

    @Transaction
    suspend fun unUseGifticonTransaction(history: HistoryEntity) {
        val gifticonId = history.gifticonId

        // 가장 최근 금액으로 복구
        if (isCashCard(gifticonId)) {
            val gifticon = getGifticon(gifticonId).first()
            updateGifticon(
                id = gifticonId,
                croppedUri = gifticon.croppedUri,
                name = gifticon.name,
                brand = gifticon.brand,
                expire_at = gifticon.expireAt,
                barcode = gifticon.barcode,
                isCashCard = gifticon.isCashCard,
                balance = history.balance ?: throw IllegalStateException("balance should not be null"),
                memo = gifticon.memo,
            )
        }
        unUseGifticon(gifticonId)
        insertUsageHistory(history)
    }

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE brand =:brand")
    fun getGifticonByBrand(brand: String): Flow<List<GifticonEntity>>

    @Query(
        "SELECT EXISTS (SELECT * FROM $GIFTICON_TABLE " +
            "WHERE expire_at >= :time AND is_used = 0 AND user_id = :userId)",
    )
    fun hasUsableGifticon(userId: String, time: Date): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 from $GIFTICON_TABLE WHERE LOWER(brand)=LOWER(:brand) LIMIT 1)")
    suspend fun hasGifticonBrand(brand: String): Boolean

    @Query("UPDATE $GIFTICON_TABLE SET user_id = :newUserId WHERE user_id = :oldUserId")
    suspend fun moveUserIdGifticon(oldUserId: String, newUserId: String)

    /**
     * 오늘 날짜를 기준으로 사용 가능한 기프티콘의 브랜드명 갖고 오기
     */
    @Query(
        "SELECT DISTINCT brand FROM $GIFTICON_TABLE " +
            "WHERE user_id = :userId AND expire_at >= :time AND is_used = 0",
    )
    fun getGifticonBrands(userId: String, time: Date): Flow<List<String>>

    /**
     * 사용 가능한 기프티콘 중에서 count 만큼만 갖고 오기
     */
    @Query(
        "SELECT * FROM $GIFTICON_TABLE " +
            "WHERE user_id = :userId AND expire_at >= :time AND is_used = 0 LIMIT :count",
    )
    fun getSomeGifticons(userId: String, time: Date, count: Int): Flow<List<GifticonEntity>>
}
