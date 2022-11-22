package com.lighthouse.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BrandWithSections(
    @Embedded val sectionEntity: SectionEntity,
    @Relation(
        parentColumn = PARENT_COLUMN_ID,
        entityColumn = ENTITY_COLUMN_ID
    )
    val brands: List<BrandLocationEntity>
) {

    companion object {
        private const val PARENT_COLUMN_ID = "section_id"
        private const val ENTITY_COLUMN_ID = "parent_section_id"
    }
}
