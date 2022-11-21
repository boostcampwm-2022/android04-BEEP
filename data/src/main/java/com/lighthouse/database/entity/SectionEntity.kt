package com.lighthouse.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lighthouse.database.entity.SectionEntity.Companion.SECTION_TABLE

@Entity(tableName = SECTION_TABLE)
data class SectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "section_id")
    val id: Long = 0,
    @ColumnInfo(name = "min_x") val minX: String,
    @ColumnInfo(name = "min_y") val minY: String
) {

    companion object {
        const val SECTION_TABLE = "section_table"
    }
}
