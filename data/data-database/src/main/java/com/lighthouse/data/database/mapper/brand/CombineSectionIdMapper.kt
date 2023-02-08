package com.lighthouse.data.database.mapper.brand

import com.lighthouse.beep.model.location.Dms

internal fun combineSectionId(x: Dms, y: Dms, brandName: String): String {
    return "${x.dmsToString()}_${y.dmsToString()}_${brandName.lowercase()}"
}
