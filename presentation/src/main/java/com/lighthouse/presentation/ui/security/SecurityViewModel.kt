package com.lighthouse.presentation.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SecurityViewModel : ViewModel() {

    val directionsFlow = MutableSharedFlow<SecurityDirections>()

    fun gotoOtherScreen(directions: SecurityDirections) {
        viewModelScope.launch {
            directionsFlow.emit(directions)
        }
    }
}
