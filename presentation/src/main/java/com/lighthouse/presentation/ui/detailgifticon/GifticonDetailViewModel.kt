package com.lighthouse.presentation.ui.detailgifticon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class GifticonDetailViewModel : ViewModel() {
    private val _gifticon = MutableStateFlow(
        Gifticon(
            id = UUID.generate(),
            userId = "temp_id",
            name = "딸기마카롱설빙",
            brand = "설빙",
            expireAt = Date(System.currentTimeMillis()),
            barcode = "123456781234",
            isCashCard = false,
            balance = 100,
            memo = "",
            isUsed = false
        )
    )
    val gifticon = _gifticon.asStateFlow()
    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    fun scrollDownForUseButtonClicked() {
        event(Event.ScrollDownForUseButtonClicked)
    }

    fun shareButtonClicked() {
        event(Event.ShareButtonClicked)
    }

    fun editButtonClicked() {
        event(Event.EditButtonClicked)
    }

    fun showAllUsedInfoButtonClicked() {
        event(Event.ShowAllUsedInfoButtonClicked)
    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
