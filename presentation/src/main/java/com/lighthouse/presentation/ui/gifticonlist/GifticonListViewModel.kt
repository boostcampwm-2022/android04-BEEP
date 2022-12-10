package com.lighthouse.presentation.ui.gifticonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetAllBrandsUseCase
import com.lighthouse.domain.usecase.GetFilteredGifticonsUseCase
import com.lighthouse.domain.util.isExpired
import com.lighthouse.presentation.mapper.toDomain
import com.lighthouse.presentation.model.GifticonSortBy
import com.lighthouse.presentation.util.flow.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class GifticonListViewModel @Inject constructor(
    getAllBrandsUseCase: GetAllBrandsUseCase,
    private val getFilteredGifticonsUseCase: GetFilteredGifticonsUseCase
) : ViewModel() {

    private val filter = MutableStateFlow(setOf<String>())
    private val sortBy = MutableStateFlow(GifticonSortBy.DEADLINE)
    private val gifticons = MutableStateFlow<DbResult<List<Gifticon>>>(DbResult.Loading)
    private val brands = MutableStateFlow<DbResult<List<Brand>>>(DbResult.Loading)
    private val entireBrandsDialogShown = MutableStateFlow(false)
    private val showExpiredGifticon = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            filter.flatMapLatest {
                getFilteredGifticonsUseCase(it, sortBy.value.toDomain()).distinctUntilChanged()
            }.debounce(50).collect { dbResult ->
                gifticons.value = dbResult
            }
        }
        viewModelScope.launch {
            sortBy.flatMapLatest {
                getFilteredGifticonsUseCase(filter.value, it.toDomain()).distinctUntilChanged()
            }.debounce(50).collect { dbResult ->
                gifticons.value = dbResult
            }
        }
        viewModelScope.launch {
            showExpiredGifticon.flatMapLatest { showExpired ->
                getAllBrandsUseCase(showExpired.not()).distinctUntilChanged()
            }.debounce(50).collect { dbResult ->
                filter.value = emptySet() // 만료된 기프티콘 표시 옵션이 변경되면 필터 초기화
                brands.value = dbResult
            }
        }
    }

    val state = combine(
        sortBy,
        gifticons,
        showExpiredGifticon,
        brands,
        entireBrandsDialogShown,
        filter
    ) { sortBy, gifticonResult, showExpired, brandResult, entireBrandsDialogShown, filter ->
        when (gifticonResult) {
            is DbResult.Success -> {
                val brands = when (brandResult) {
                    is DbResult.Success -> {
                        brandResult.data
                    }
                    else -> emptyList()
                }
                GifticonListViewState(
                    sortBy = sortBy,
                    gifticons = if (showExpired) {
                        gifticonResult.data
                    } else {
                        gifticonResult.data.filterNot { it.expireAt.isExpired() }
                    },
                    showExpiredGifticon = showExpired,
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
                    brands = emptyList(),
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
        showExpiredGifticon.value = show
    }

    fun clearFilter() {
        filter.value = emptySet()
    }

    fun sort(newSortBy: GifticonSortBy) {
        sortBy.value = newSortBy
    }
}
