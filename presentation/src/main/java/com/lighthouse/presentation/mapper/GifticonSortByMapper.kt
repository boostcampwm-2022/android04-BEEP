package com.lighthouse.presentation.mapper

import com.lighthouse.beep.model.etc.SortBy
import com.lighthouse.presentation.model.GifticonSortBy

fun GifticonSortBy.toDomain(): SortBy {
    return when (this) {
        GifticonSortBy.RECENT -> SortBy.RECENT
        GifticonSortBy.DEADLINE -> SortBy.DEADLINE
    }
}
