package com.lighthouse.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lighthouse.database.entity.SectionEntity.Companion.SECTION_TABLE

@Entity(tableName = SECTION_TABLE)
data class SectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "max_x") val maxX: Int,
    @ColumnInfo(name = "min_x") val minX: Int,
    @ColumnInfo(name = "max_y") val maxY: Int,
    @ColumnInfo(name = "min_y") val minY: Int
) {

    companion object {
        const val SECTION_TABLE = "section_table"
    }
}
