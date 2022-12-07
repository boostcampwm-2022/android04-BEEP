package com.lighthouse.presentation.ui.gifticonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetAllBrandsUseCase
import com.lighthouse.domain.usecase.GetFilteredGifticonsUseCase
import com.lighthouse.presentation.mapper.toDomain
import com.lighthouse.presentation.model.GifticonSortBy
import com.lighthouse.presentation.util.flow.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GifticonListViewModel @Inject constructor(
    getAllBrandsUseCase: GetAllBrandsUseCase,
    private val getFilteredGifticonsUseCase: GetFilteredGifticonsUseCase
) : ViewModel() {

    private val brands: StateFlow<List<Brand>> = getAllBrandsUseCase().transform {
        if (it is DbResult.Success) {
            emit(it.data)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val filter = MutableStateFlow(setOf<String>())
    private val sortBy = MutableStateFlow(GifticonSortBy.DEADLINE)
    private val gifticons = MutableStateFlow<DbResult<List<Gifticon>>>(DbResult.Loading)
    private val entireBrandsDialogShown = MutableStateFlow(false)
    private val showUsedGifticon = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            filter.flatMapLatest {
                getFilteredGifticonsUseCase(it, sortBy.value.toDomain())
            }.collect { dbResult ->
                gifticons.value = dbResult
            }
        }
        viewModelScope.launch {
            sortBy.flatMapLatest {
                getFilteredGifticonsUseCase(filter.value, it.toDomain())
            }.collect { dbResult ->
                gifticons.value = dbResult
            }
        }
    }

    val state = combine(
        sortBy,
        gifticons,
        showUsedGifticon,
        brands,
        entireBrandsDialogShown,
        filter
    ) { sortBy, dbResult, isFiltered, brands, entireBrandsDialogShown, filter ->
        when (dbResult) {
            is DbResult.Success -> {
                GifticonListViewState(
                    sortBy = sortBy,
                    gifticons = if (isFiltered) {
                        dbResult.data.filterNot { it.isUsed }
                    } else {
                        dbResult.data
                    },
                    showUsedGifticon = isFiltered,
                    brands = brands,
                    entireBrandsDialogShown = entireBrandsDialogShown,
                    selectedFilter = filter,
                    loading = false
                )
            }
            is DbResult.Loading -> {
                // TODO Shimmer UI
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

    fun filterUsedGifticon(show: Boolean = true) {
        showUsedGifticon.value = show
    }

    fun clearFilter() {
        filter.value = emptySet()
    }

    fun sort(newSortBy: GifticonSortBy) {
        sortBy.value = newSortBy
    }
}
