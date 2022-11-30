package com.lighthouse.presentation.ui.gifticonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetAllGifticonsUseCase
import com.lighthouse.domain.usecase.GetFilteredGifticonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GifticonListViewModel @Inject constructor(
    getAllGifticonsUseCase: GetAllGifticonsUseCase,
    private val getFilteredGifticonsUseCase: GetFilteredGifticonsUseCase
) : ViewModel() {

    private val allGifticons: StateFlow<List<Gifticon>> = getAllGifticonsUseCase().transform {
        if (it is DbResult.Success) {
            emit(it.data)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val brands: StateFlow<List<Brand>> = allGifticons.map { gifticons ->
        gifticons.groupBy {
            it.brand
        }.map { (brand, list) ->
            Brand(brand, list.size)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val filter = MutableStateFlow(setOf<String>())
    private val sortBy = MutableStateFlow(GifticonSortBy.DEADLINE)
    private val gifticons = MutableStateFlow<DbResult<List<Gifticon>>>(DbResult.Loading)
    private val entireBrandsDialogShown = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            filter.flatMapLatest {
                getFilteredGifticonsUseCase(it)
            }.collect { dbResult ->
                gifticons.value = dbResult
            }
        }
    }

    val state = combine(
        sortBy,
        gifticons,
        brands,
        entireBrandsDialogShown,
        filter
    ) { sortBy, dbResult, brands, entireBrandsDialogShown, filter ->
        when (dbResult) {
            is DbResult.Success -> {
                GifticonListViewState(
                    sortBy = sortBy,
                    gifticons = dbResult.data,
                    brands = brands,
                    entireBrandsDialogShown = entireBrandsDialogShown,
                    selectedFilter = filter,
                    loading = false
                )
            }
            is DbResult.Loading -> {
                val gifticons = gifticons.value.let {
                    if (it is DbResult.Success) it.data else emptyList()
                }
                GifticonListViewState(
                    sortBy = sortBy,
                    gifticons = gifticons,
                    brands = brands,
                    entireBrandsDialogShown = entireBrandsDialogShown,
                    selectedFilter = filter,
                    loading = true
                )
            }
            else -> {
                GifticonListViewState()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GifticonListViewState(loading = true))

    fun showEntireBrandsDialog() {
        entireBrandsDialogShown.value = true
    }

    fun dismissEntireBrandsDialog() {
        entireBrandsDialogShown.value = false
    }

    fun toggleFilterSelection(brand: Brand) {
        filter.value = if (brand.name in state.value.selectedFilter) {
            filter.value.minus(brand.name)
        } else {
            filter.value.plus(brand.name)
        }
    }

    fun clearFilter() {
        filter.value = emptySet()
    }
}
