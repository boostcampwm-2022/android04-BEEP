package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.GifticonEntity.Companion.GIFTICON_TABLE
import com.lighthouse.database.entity.UsageHistoryEntity
import com.lighthouse.database.entity.UsageHistoryEntity.Companion.USAGE_HISTORY_TABLE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface GifticonDao {

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE id = :id")
    fun getGifticon(id: String): Flow<GifticonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGifticon(vararg gifticon: GifticonEntity)

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
     * 기프티콘의 사용 기록을 조회한다
     * */
    @Query("SELECT * FROM $USAGE_HISTORY_TABLE WHERE gifticon_id = :gifticonId")
    fun getUsageHistory(gifticonId: String): Flow<List<UsageHistoryEntity>>

    /**
     * 기프티콘의 사용 기록을 추가한다
     * */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsageHistory(usageHistory: UsageHistoryEntity)

    /**
     * 기프티콘을 사용 상태로 변경하고, 사용 기록에 추가한다
     * */
    @Transaction
    suspend fun useGifticonTransaction(usageHistory: UsageHistoryEntity) {
        val gifticonId = usageHistory.gifticonId

        useGifticon(gifticonId)
        insertUsageHistory(usageHistory)
    }

    /**
     * 금액권 기프티콘의 잔액을 차감하고 사용 기록에 추가한다. 잔액이 0원이 된다면 사용 상태로 변경한다
     * */
    @Transaction
    suspend fun useCashCardGifticonTransaction(amount: Int, usageHistory: UsageHistoryEntity) {
        val gifticonId = usageHistory.gifticonId
        val balance = getGifticon(gifticonId).first().balance

        assert(balance >= amount) // 사용할 금액이 잔액보다 많으면 안된다

        useCashCardGifticon(gifticonId, balance - amount)
        insertUsageHistory(usageHistory)

        if (balance == amount) {
            useGifticon(gifticonId)
        }
    }
}
