package com.lighthouse.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.beep.model.user.UserPreferenceOption
import com.lighthouse.core.utils.flow.MutableEventFlow
import com.lighthouse.core.utils.flow.asEventFlow
import com.lighthouse.domain.usecase.HasVariableGifticonUseCase
import com.lighthouse.domain.usecase.setting.GetOptionStoredUseCase
import com.lighthouse.domain.usecase.setting.SaveNotificationOptionUseCase
import com.lighthouse.domain.usecase.setting.SaveSecurityOptionUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.setting.SecurityOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    hasVariableGifticonUseCase: HasVariableGifticonUseCase,
    getOptionStoredUseCase: GetOptionStoredUseCase,
    private val saveSecurityOptionUseCase: SaveSecurityOptionUseCase,
    private val saveNotificationOptionUseCase: SaveNotificationOptionUseCase
) : ViewModel() {

    private val _eventFlow = MutableEventFlow<MainEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    val selectedMenuItem = MutableStateFlow(R.id.menu_home)

    private val _pageFlow = MutableStateFlow(MainPage.HOME)
    val pageFlow = _pageFlow
        .onEach { page ->
            pageToMenuId(page)?.let { menuId ->
                gotoMenuItem(menuId)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, MainPage.HOME)

    val hasVariableGifticon = hasVariableGifticonUseCase()

    private val widgetEvent = savedStateHandle.get<String>(Extras.KEY_WIDGET_EVENT)
    private val widgetBrandName = savedStateHandle.get<String>(Extras.KEY_WIDGET_BRAND)

    init {
        viewModelScope.launch {
            widgetEvent ?: return@launch
            widgetBrandName ?: return@launch
            when (widgetEvent) {
                Extras.WIDGET_EVENT_MAP -> _eventFlow.emit(MainEvent.NavigateMap(widgetBrandName))
            }
        }
    }

    val fabFlow = _pageFlow.combine(hasVariableGifticon) { page, hasVariableGifticon ->
        when (page) {
            MainPage.HOME -> hasVariableGifticon
            MainPage.LIST -> true
            MainPage.SETTING,
            MainPage.OTHER -> false
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val bnvFlow = _pageFlow.map { page ->
        when (page) {
            MainPage.HOME,
            MainPage.LIST,
            MainPage.SETTING -> true

            MainPage.OTHER -> false
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isSecurityOptionExist = getOptionStoredUseCase(UserPreferenceOption.SECURITY)
    val isNotificationOptionExist = getOptionStoredUseCase(UserPreferenceOption.NOTIFICATION)

    fun gotoAddGifticon() {
        viewModelScope.launch {
            _eventFlow.emit(MainEvent.NavigateAddGifticon)
        }
    }

    fun gotoList() {
        viewModelScope.launch {
            _pageFlow.emit(MainPage.LIST)
        }
    }

    private fun pageToMenuId(page: MainPage): Int? {
        return when (page) {
            MainPage.LIST -> R.id.menu_list
            MainPage.HOME -> R.id.menu_home
            MainPage.SETTING -> R.id.menu_setting
            else -> null
        }
    }

    fun gotoMenuItem(itemId: Int): Boolean {
        if (selectedMenuItem.value == itemId) {
            return true
        }
        selectedMenuItem.value = itemId
        viewModelScope.launch {
            val pages = when (itemId) {
                R.id.menu_list -> MainPage.LIST
                R.id.menu_home -> MainPage.HOME
                R.id.menu_setting -> MainPage.SETTING
                else -> MainPage.OTHER
            }
            _pageFlow.emit(pages)
        }
        return true
    }

    fun saveSecurityNotUse() {
        viewModelScope.launch {
            saveSecurityOptionUseCase(SecurityOption.NONE.ordinal)
        }
    }

    fun saveNotificationUse() {
        viewModelScope.launch {
            saveNotificationOptionUseCase(true)
        }
    }

    fun gotoHome() {
        viewModelScope.launch {
            _pageFlow.emit(MainPage.HOME)
        }
    }
}
