package com.lighthouse.presentation.ui.addgifticon.dialog

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.lighthouse.presentation.extra.Extras
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class OriginImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val originUri = savedStateHandle.getStateFlow<Uri?>(Extras.OriginImage, null)
}
