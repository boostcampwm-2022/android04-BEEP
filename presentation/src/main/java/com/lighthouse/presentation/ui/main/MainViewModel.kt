package com.lighthouse.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.domain.usecase.HasVariableGifticonUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.setting.SecurityOption
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    hasVariableGifticonUseCase: HasVariableGifticonUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _eventFlow = MutableEventFlow<MainEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    private val _pageFlow = MutableStateFlow<MainPage>(MainPage.Home)
    val pageFlow = _pageFlow.asStateFlow()

    val hasVariableGifticon = hasVariableGifticonUseCase()

    private val widgetEvent = savedStateHandle.get<String>(Extras.KEY_WIDGET_EVENT)
    private val widgetBrandName = savedStateHandle.get<String>(Extras.KEY_WIDGET_BRAND)

    init {
        viewModelScope.launch() {
            widgetEvent ?: return@launch
            widgetBrandName ?: return@launch
            when (widgetEvent) {
                Extras.WIDGET_EVENT_MAP -> _eventFlow.emit(MainEvent.NavigateMap(widgetBrandName))
            }
        }
    }

    val fabFlow = _pageFlow.combine(hasVariableGifticon) { page, hasVariableGifticon ->
        when (page) {
            MainPage.Home -> hasVariableGifticon
            MainPage.List -> true
            MainPage.Setting -> false
            MainPage.Other -> false
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val bnvFlow = _pageFlow.map { page ->
        when (page) {
            MainPage.Home -> true
            MainPage.List -> true
            MainPage.Setting -> true
            MainPage.Other -> false
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val securityOption = userPreferencesRepository.isStored(UserPreferenceOption.SECURITY)

    val selectedMenuItem = MutableStateFlow(R.id.menu_home)

    fun gotoAddGifticon() {
        viewModelScope.launch {
            _eventFlow.emit(MainEvent.NavigateAddGifticon)
        }
    }

    fun gotoList() {
        viewModelScope.launch {
            _pageFlow.emit(MainPage.List)
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
                else -> MainPage.Other
            }
            _pageFlow.emit(pages)
        }
        return true
    }

    fun setSecurityNoUse() {
        viewModelScope.launch {
            userPreferencesRepository.setSecurityOption(SecurityOption.NONE.ordinal)
        }
    }
}
