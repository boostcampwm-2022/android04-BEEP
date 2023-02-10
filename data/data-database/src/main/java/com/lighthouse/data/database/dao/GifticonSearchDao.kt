package com.lighthouse.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.lighthouse.data.database.entity.DBGifticonEntity
import com.lighthouse.data.database.model.DBBrandWithGifticonCount
import com.lighthouse.data.database.model.DBGifticonWithCrop
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
internal interface GifticonSearchDao {

    /**
     * 기프티콘 1개를 가져온다
     * 1. 유저 ID
     * 2. 기프티콘 ID
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND id = :gifticonId"
    )
    fun getGifticon(
        userId: String,
        gifticonId: String
    ): Flow<DBGifticonEntity>

    /**
     * 사용 가능한 기프티콘 중에서 count 만큼만 갖고 오기
     * 1. 유저 ID
     * 2. 기준 시간
     * 3. 가져올 개수
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND expire_at = :time AND is_used = 0 " +
            "LIMIT :count"
    )
    fun getGifticons(
        userId: String,
        time: Date,
        count: Int
    ): Flow<List<DBGifticonEntity>>

    /**
     * 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 최신순으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed " +
            "ORDER BY created_at DESC"
    )
    fun getAllGifticonsSortByRecent(
        userId: String,
        isUsed: Boolean
    ): Flow<List<DBGifticonEntity>>

    /**
     * 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 만료일
     * 최신순으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed AND expire_at > :expired " +
            "ORDER BY created_at DESC"
    )
    fun getAllGifticonsSortByRecent(
        userId: String,
        isUsed: Boolean,
        expired: Date
    ): Flow<List<DBGifticonEntity>>

    /**
     * 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 만료일기준으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed " +
            "ORDER BY expire_at"
    )
    fun getAllGifticonsSortByDeadline(
        userId: String,
        isUsed: Boolean
    ): Flow<List<DBGifticonEntity>>

    /**
     * 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 만료일
     * 만료일기준으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed AND expire_at > :expired " +
            "ORDER BY expire_at"
    )
    fun getAllGifticonsSortByDeadline(
        userId: String,
        isUsed: Boolean,
        expired: Date
    ): Flow<List<DBGifticonEntity>>

    /**
     * 브랜드 이름으로 필터된 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 필터할 브랜드 Set
     * 최신순으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed AND UPPER(brand) IN(:filters) " +
            "ORDER BY created_at DESC"
    )
    fun getFilteredGifticonsSortByRecent(
        userId: String,
        isUsed: Boolean,
        filters: Set<String>
    ): Flow<List<DBGifticonEntity>>

    /**
     * 브랜드 이름으로 필터된 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 필터할 브랜드 Set
     * 4. 만료일
     * 최신순으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND " +
            "is_used = :isUsed AND " +
            "UPPER(brand) IN(:filters) AND " +
            "expire_at > :expired " +
            "ORDER BY created_at DESC"
    )
    fun getFilteredGifticonsSortByRecent(
        userId: String,
        isUsed: Boolean,
        filters: Set<String>,
        expired: Date
    ): Flow<List<DBGifticonEntity>>

    /**
     * 브랜드 이름으로 필터된 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 필터할 브랜드 Set
     * 만료일기준으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed AND UPPER(brand) IN(:filters) " +
            "ORDER BY expire_at"
    )
    fun getFilteredGifticonsSortByDeadline(
        userId: String,
        isUsed: Boolean,
        filters: Set<String>
    ): Flow<List<DBGifticonEntity>>

    /**
     * 브랜드 이름으로 필터된 기프티콘 리스트를 가져온다
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 필터할 브랜드 Set
     * 4. 만료일
     * 만료일기준으로 정렬
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND " +
            "is_used = :isUsed AND " +
            "UPPER(brand) IN(:filters) AND " +
            "expire_at > :expired " +
            "ORDER BY expire_at"
    )
    fun getFilteredGifticonsSortByDeadline(
        userId: String,
        isUsed: Boolean,
        filters: Set<String>,
        expired: Date
    ): Flow<List<DBGifticonEntity>>

    /**
     * 브랜드 이름과 해당 브랜드의 기프티콘 개수를 가져오기
     * 1. 유저 ID
     * 2. 사용여부
     * 개수를 기준으로 정렬
     * */
    @Query(
        "SELECT brand AS name, COUNT(*) AS count FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed " +
            "GROUP BY brand ORDER BY count DESC"
    )
    fun getAllBrands(
        userId: String,
        isUsed: Boolean
    ): Flow<List<DBBrandWithGifticonCount>>

    /**
     * 브랜드 이름과 해당 브랜드의 기프티콘 개수를 가져오기
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 만료일
     * 개수를 기준으로 정렬
     * */
    @Query(
        "SELECT brand AS name, COUNT(*) AS count FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed AND expire_at > :expired " +
            "GROUP BY brand ORDER BY count DESC"
    )
    fun getAllBrands(
        userId: String,
        isUsed: Boolean,
        expired: Date
    ): Flow<List<DBBrandWithGifticonCount>>

    /**
     * 기프티콘과 크롭정보를 합쳐서 가져오기
     * 1. 유저 ID
     * 2. 기프티콘 ID
     * */
    @Query(
        "SELECT * FROM gifticon_table AS gt " +
            "INNER JOIN gifticon_crop_table AS gct ON gt.id = gct.gifticon_id " +
            "WHERE gt.user_id = :userId AND gt.id = :gifticonId"
    )
    suspend fun getGifticonWithCrop(
        userId: String,
        gifticonId: String
    ): DBGifticonWithCrop?

    /**
     * 기프티콘 리스트 가져오기
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 브랜드 이름
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed AND brand =:brand"
    )
    fun getGifticonByBrand(
        userId: String,
        isUsed: Boolean,
        brand: String
    ): Flow<List<DBGifticonEntity>>

    /**
     * 기프티콘 리스트 가져오기
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 브랜드 이름
     * 4. 만료일
     * */
    @Query(
        "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND " +
            "is_used = :isUsed AND " +
            "brand =:brand AND " +
            "expire_at > :expired"
    )
    fun getGifticonByBrand(
        userId: String,
        isUsed: Boolean,
        brand: String,
        expired: Date
    ): Flow<List<DBGifticonEntity>>

    /**
     * 기프티콘 존재여부 확인
     * 1. 유저 ID
     * 2. 사용여부
     * */
    @Query(
        "SELECT EXISTS (" +
            "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = 0)"
    )
    fun hasGifticon(
        userId: String,
        isUsed: Boolean
    ): Flow<Boolean>

    /**
     * 기프티콘 존재여부 확인
     * 1. 유저 ID
     * 2. 사용여부
     * 3. 만료일
     * */
    @Query(
        "SELECT EXISTS (" +
            "SELECT * FROM gifticon_table " +
            "WHERE user_id = :userId AND is_used = :isUsed AND expire_at >= :expired)"
    )
    fun hasGifticon(
        userId: String,
        isUsed: Boolean,
        expired: Date
    ): Flow<Boolean>

    /**
     * 브랜드 존재여부 확인
     * 1. 유저 ID
     * 2. 브랜드 이름
     * */
    @Query(
        "SELECT EXISTS(" +
            "SELECT 1 from gifticon_table " +
            "WHERE user_id = :userId AND LOWER(brand)=LOWER(:brand) " +
            ")"
    )
    suspend fun hasGifticonBrand(
        userId: String,
        brand: String
    ): Boolean

    /**
     * 오늘 날짜를 기준으로 사용 가능한 기프티콘의 브랜드명 갖고 오기
     * 1. 유저 ID
     * 2. 기준 시간
     * */
    @Query(
        "SELECT DISTINCT brand FROM gifticon_table " +
            "WHERE user_id = :userId AND expire_at >= :time AND is_used = 0"
    )
    fun getGifticonBrands(userId: String, time: Date): Flow<List<String>>
}
