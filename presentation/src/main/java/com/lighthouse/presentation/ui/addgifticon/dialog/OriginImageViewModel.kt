package com.lighthouse.presentation.ui.addgifticon.dialog

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.extra.Extras
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OriginImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dismissFlow = MutableSharedFlow<Unit>()
    val dismissFlow = _dismissFlow.asSharedFlow()

    val originUri = savedStateHandle.get<Uri?>(Extras.OriginImage)

    fun dismiss() {
        viewModelScope.launch {
            _dismissFlow.emit(Unit)
        }
    }
}
