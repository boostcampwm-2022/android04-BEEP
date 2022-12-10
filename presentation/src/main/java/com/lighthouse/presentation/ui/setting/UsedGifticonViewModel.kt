package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UsedGifticonViewModel @Inject constructor(
    getGifticonsUseCase: GetGifticonsUseCase
) : ViewModel() {

    val usedGifticons = getGifticonsUseCase.getUsedGifticons().map { dbResult ->
        if (dbResult is DbResult.Success) {
            dbResult.data
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch {
            while (true) {
                Timber.tag("USED").d("${usedGifticons.value}")
                delay(1000L)
            }
        }
    }
}
