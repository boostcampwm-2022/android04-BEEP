package com.lighthouse.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lighthouse.database.entity.BrandLocationEntity.Companion.BRAND_LOCATION_TABLE

@Entity(
    tableName = BRAND_LOCATION_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = arrayOf("section_id"),
            childColumns = arrayOf("parent_section_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BrandLocationEntity(
    @PrimaryKey
    @ColumnInfo(name = "place_url")
    val placeUrl: String,
    @ColumnInfo(name = "address_name") val addressName: String,
    @ColumnInfo(name = "parent_section_id") val sectionId: String,
    @ColumnInfo(name = "place_name") val placeName: String,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "brand") val brand: String,
    @ColumnInfo(name = "x") val x: String,
    @ColumnInfo(name = "y") val y: String
) {

    companion object {
        const val BRAND_LOCATION_TABLE = "brand_location_table"
    }
}
