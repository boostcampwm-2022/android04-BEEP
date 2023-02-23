package com.lighthouse.presentation.ui.gifticonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetAllBrandsUseCase
import com.lighthouse.domain.usecase.GetFilteredGifticonsUseCase
import com.lighthouse.domain.usecase.RemoveGifticonUseCase
import com.lighthouse.domain.usecase.UseGifticonUseCase
import com.lighthouse.presentation.mapper.toDomain
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.GifticonSortBy
import com.lighthouse.presentation.model.GifticonUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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
    private val getFilteredGifticonsUseCase: GetFilteredGifticonsUseCase,
    private val useGifticonUseCase: UseGifticonUseCase,
    private val removeGifticonUseCase: RemoveGifticonUseCase,
) : ViewModel() {

    private val filter = MutableStateFlow(setOf<String>())
    private val sortBy = MutableStateFlow(GifticonSortBy.DEADLINE)
    private val gifticons = MutableStateFlow<DbResult<List<Gifticon>>>(DbResult.Loading)
    private val brands =
        getAllBrandsUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DbResult.Loading)
    private val entireBrandsDialogShown = MutableStateFlow(false)

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
    }

    val state = combine(
        sortBy,
        gifticons,
        brands,
        entireBrandsDialogShown,
        filter,
    ) { sortBy, gifticonResult, brandResult, entireBrandsDialogShown, filter ->
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
                    gifticons = gifticonResult.data.map { it.toPresentation() },
                    brands = brands,
                    entireBrandsDialogShown = entireBrandsDialogShown,
                    selectedFilter = filter,
                    loading = false,
                )
            }

            is DbResult.Loading -> {
                val gifticons = gifticons.value.let { result ->
                    if (result is DbResult.Success) result.data.map { it.toPresentation() } else emptyList()
                }
                GifticonListViewState(
                    sortBy = sortBy,
                    gifticons = gifticons,
                    brands = emptyList(),
                    entireBrandsDialogShown = entireBrandsDialogShown,
                    selectedFilter = filter,
                    loading = true,
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

    fun completeUsage(gifticon: GifticonUIModel) {
        viewModelScope.launch {
            useGifticonUseCase(gifticon.id, false)
        }
    }

    fun removeGifticon(gifticon: GifticonUIModel) {
        viewModelScope.launch {
            removeGifticonUseCase(gifticon.id)
        }
    }

    fun sort(newSortBy: GifticonSortBy) {
        sortBy.value = newSortBy
    }
}
