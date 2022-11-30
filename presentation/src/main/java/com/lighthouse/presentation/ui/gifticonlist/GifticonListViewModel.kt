package com.lighthouse.presentation.ui.gifticonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetAllGifticonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GifticonListViewModel @Inject constructor(
    getAllGifticonsUseCase: GetAllGifticonsUseCase
) : ViewModel() {

    private val gifticons: StateFlow<List<Gifticon>> = getAllGifticonsUseCase().transform {
        if (it is DbResult.Success) {
            Timber.tag("GifticonList").d("gifticons: ${it.data}")
            emit(it.data)
        } else if (it is DbResult.Failure) {
            Timber.tag("GifticonList").d("fail: ${it.throwable}")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val brands: StateFlow<List<Brand>> = gifticons.map { gifticons ->
        gifticons.groupBy {
            it.brand
        }.map { (brand, list) ->
            Brand(brand, list.size)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val loading = MutableStateFlow(false)

    private val _state = MutableStateFlow(GifticonListViewState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loading.value = true
            combine(
                gifticons,
                brands,
                loading
            ) { gifticons, brands, refreshing ->
                GifticonListViewState(
                    gifticons = gifticons,
                    loading = refreshing,
                    brands = brands
                )
            }.catch { throwable ->
                loading.value = false
                throw throwable // TODO 어떻게 처리할 지 고민해보자
            }.collect {
                loading.value = false
                _state.value = it
            }
        }
    }

    fun showEntireBrandsDialog() {
        _state.value = state.value.copy(entireBrandsDialogShown = true)
    }

    fun dismissEntireBrandsDialog() {
        _state.value = state.value.copy(entireBrandsDialogShown = false)
    }
}
