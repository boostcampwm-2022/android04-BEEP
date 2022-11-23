package com.lighthouse.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lighthouse.database.entity.SectionEntity.Companion.SECTION_TABLE
import java.util.Date

@Entity(tableName = SECTION_TABLE)
data class SectionEntity(
    @PrimaryKey
    @ColumnInfo(name = "section_id")
    val id: String,
    @ColumnInfo(name = "search_date") val searchDate: Date
) {

    companion object {
        const val SECTION_TABLE = "section_table"
    }
}
