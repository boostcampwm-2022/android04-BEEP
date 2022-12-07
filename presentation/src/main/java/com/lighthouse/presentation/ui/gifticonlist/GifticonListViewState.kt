package com.lighthouse.presentation.ui.gifticonlist

import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.GifticonSortBy

data class GifticonListViewState(
    val sortBy: GifticonSortBy = GifticonSortBy.DEADLINE,
    val gifticons: List<Gifticon> = emptyList(),
    val showUsedGifticon: Boolean = true,
    val loading: Boolean = false,
    val brands: List<Brand> = emptyList(),
    val selectedFilter: Set<String> = emptySet(),
    val entireBrandsDialogShown: Boolean = false,
    val errorMessage: String? = null
)
