package com.lighthouse.presentation.ui.gifticonlist

import com.lighthouse.beep.model.brand.Brand
import com.lighthouse.presentation.model.GifticonSortBy
import com.lighthouse.presentation.model.GifticonUIModel

data class GifticonListViewState(
    val sortBy: GifticonSortBy = GifticonSortBy.DEADLINE,
    val gifticons: List<GifticonUIModel> = emptyList(),
    val showExpiredGifticon: Boolean = false,
    val loading: Boolean = false,
    val brands: List<Brand> = emptyList(),
    val selectedFilter: Set<String> = emptySet(),
    val entireBrandsDialogShown: Boolean = false,
    val errorMessage: String? = null
)
