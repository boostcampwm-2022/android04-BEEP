package com.lighthouse.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.data.database.entity.DBSectionEntity.Companion.SECTION_TABLE
import java.util.Date

@Entity(tableName = SECTION_TABLE)
internal data class DBSectionEntity(
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
