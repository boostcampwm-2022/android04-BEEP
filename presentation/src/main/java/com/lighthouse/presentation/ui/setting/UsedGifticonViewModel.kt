package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.presentation.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UsedGifticonViewModel @Inject constructor(
    getGifticonsUseCase: GetGifticonsUseCase
) : ViewModel() {

    val usedGifticons = getGifticonsUseCase.getUsedGifticons().map { dbResult ->
        if (dbResult is DbResult.Success) {
            dbResult.data.map {
                it.toPresentation()
            }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
