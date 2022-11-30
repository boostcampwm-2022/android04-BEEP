package com.lighthouse.presentation.ui.gifticonlist

import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.Gifticon

data class GifticonListViewState(
    val sortBy: GifticonSortBy = GifticonSortBy.DEADLINE,
    val gifticons: List<Gifticon> = emptyList(),
    val loading: Boolean = false,
    val brands: List<Brand> = emptyList(),
    val selectedFilter: HashSet<Brand> = hashSetOf(),
    val entireBrandsDialogShown: Boolean = false,
    val errorMessage: String? = null
)
