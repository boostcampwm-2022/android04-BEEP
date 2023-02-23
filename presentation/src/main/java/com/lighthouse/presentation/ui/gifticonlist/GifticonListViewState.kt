package com.lighthouse.presentation.ui.gifticonlist

import com.lighthouse.domain.model.Brand
import com.lighthouse.presentation.model.GifticonSortBy
import com.lighthouse.presentation.model.GifticonUIModel

data class GifticonListViewState(
    val sortBy: GifticonSortBy = GifticonSortBy.DEADLINE,
    val gifticons: List<GifticonUIModel> = emptyList(),
    val loading: Boolean = false,
    val brands: List<Brand> = emptyList(),
    val selectedFilter: Set<String> = emptySet(),
    val entireBrandsDialogShown: Boolean = false,
    val errorMessage: String? = null,
)
