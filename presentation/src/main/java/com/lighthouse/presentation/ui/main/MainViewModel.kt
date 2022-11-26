package com.lighthouse.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _eventFlow = MutableEventFlow<MainEvents>()
    val eventFlow = _eventFlow.asEventFlow()

    val selectedMenuItem = MutableStateFlow(R.id.menu_home)

    init {
        viewModelScope.launch {
            _eventFlow.emit(MainEvents.NavigateHome)
        }
    }

    fun gotoAddGifticon() {
        viewModelScope.launch {
            _eventFlow.emit(MainEvents.NavigateAddGifticon)
        }
    }

    fun gotoMenuItem(itemId: Int): Boolean {
        if (selectedMenuItem.value == itemId) {
            return true
        }
        selectedMenuItem.value = itemId
        viewModelScope.launch {
            val directions = when (itemId) {
                R.id.menu_list -> MainEvents.NavigateList
                R.id.menu_home -> MainEvents.NavigateHome
                R.id.menu_setting -> MainEvents.NavigateSetting
                else -> null
            } ?: return@launch
            _eventFlow.emit(directions)
        }
        return true
    }
}
