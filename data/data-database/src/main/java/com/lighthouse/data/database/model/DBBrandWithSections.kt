package com.lighthouse.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.lighthouse.data.database.entity.DBBrandLocationEntity
import com.lighthouse.data.database.entity.DBSectionEntity

internal data class DBBrandWithSections(
    @Embedded val DBSectionEntity: DBSectionEntity,
    @Relation(
        parentColumn = PARENT_COLUMN_ID,
        entityColumn = ENTITY_COLUMN_ID
    )
    val brands: List<DBBrandLocationEntity>
) {

    companion object {
        private const val PARENT_COLUMN_ID = "section_id"
        private const val ENTITY_COLUMN_ID = "parent_section_id"
    }
}
