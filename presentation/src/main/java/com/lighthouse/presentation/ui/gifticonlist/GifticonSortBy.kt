package com.lighthouse.presentation.ui.gifticonlist

import androidx.annotation.StringRes
import com.lighthouse.presentation.R

enum class GifticonSortBy(@StringRes val stringRes: Int) {
    RECENT(R.string.gifticon_list_sort_by_recent),
    DEADLINE(R.string.gifticon_list_sort_by_deadline)
}
