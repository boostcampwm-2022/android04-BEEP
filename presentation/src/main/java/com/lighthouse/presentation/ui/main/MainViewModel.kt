package com.lighthouse.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _eventFlow = MutableEventFlow<MainEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    private val _pageFlow = MutableStateFlow<MainPage>(MainPage.Home)
    val pageFlow = _pageFlow.asStateFlow()

    val selectedMenuItem = MutableStateFlow(R.id.menu_home)

    fun gotoAddGifticon() {
        viewModelScope.launch {
            _eventFlow.emit(MainEvent.NavigateAddGifticon)
        }
    }

    fun gotoMap() {
        viewModelScope.launch {
            _eventFlow.emit(MainEvent.NavigateMap)
        }
    }

    fun gotoMenuItem(itemId: Int): Boolean {
        if (selectedMenuItem.value == itemId) {
            return true
        }
        selectedMenuItem.value = itemId
        viewModelScope.launch {
            val pages = when (itemId) {
                R.id.menu_list -> MainPage.List
                R.id.menu_home -> MainPage.Home
                R.id.menu_setting -> MainPage.Setting
                else -> null
            } ?: return@launch
            _pageFlow.emit(pages)
        }
        return true
    }
}
