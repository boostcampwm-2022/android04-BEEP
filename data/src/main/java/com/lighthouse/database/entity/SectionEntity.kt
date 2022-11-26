package com.lighthouse.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lighthouse.database.entity.SectionEntity.Companion.SECTION_TABLE
import com.lighthouse.domain.Dms
import java.util.Date

@Entity(tableName = SECTION_TABLE)
data class SectionEntity(
    @PrimaryKey
    @ColumnInfo(name = "section_id")
    val id: String,
    @ColumnInfo(name = "search_date") val searchDate: Date,
    @ColumnInfo(name = "x") val x: Dms,
    @ColumnInfo(name = "y") val y: Dms
) {

    companion object {
        const val SECTION_TABLE = "section_table"
    }
}
