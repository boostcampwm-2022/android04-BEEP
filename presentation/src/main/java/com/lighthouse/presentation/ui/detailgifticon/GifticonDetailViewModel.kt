package com.lighthouse.presentation.ui.detailgifticon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.CashAmountPreset
import com.lighthouse.presentation.util.UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GifticonDetailViewModel @Inject constructor() : ViewModel() {
    private val _gifticon = MutableStateFlow(
        Gifticon(
            id = UUID.generate(),
            userId = "temp_id",
            name = "딸기마카롱설빙",
            brand = "설빙",
            expireAt = Date(System.currentTimeMillis()),
            barcode = "998935552189",
            isCashCard = true,
            balance = 100,
            memo = "",
            isUsed = false
        )
    )
    val gifticon = _gifticon.asStateFlow()

    private val _mode = MutableStateFlow(GifticonDetailMode.UNUSED)
    val mode = _mode.asStateFlow()

    val amountToUse = MutableStateFlow(0)

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    fun scrollDownForUseButtonClicked() {
        event(Event.ScrollDownForUseButtonClicked)
    }

    fun shareButtonClicked() {
        event(Event.ShareButtonClicked)
    }

    fun editButtonClicked() {
        _mode.update { GifticonDetailMode.EDIT }
    }

    fun showAllUsedInfoButtonClicked() {
        event(Event.ShowAllUsedInfoButtonClicked)
    }

    fun useGifticonButtonClicked() {
        when (mode.value) {
            GifticonDetailMode.UNUSED -> event(Event.UseGifticonButtonClicked) // TODO(USED 모드로 변경해야 함)
            GifticonDetailMode.USED -> {
                _mode.update { GifticonDetailMode.UNUSED }
            }
            GifticonDetailMode.EDIT -> {
                // TODO(수정사항 반영)
                _mode.update { GifticonDetailMode.UNUSED }
            }
        }
    }

    fun amountChipClicked(amountPreset: CashAmountPreset) {
        amountPreset.amount?.let { amount ->
            if (amount + amountToUse.value <= gifticon.value.balance) {
                amountToUse.update {
                    it + amount
                }
            }
        } ?: amountToUse.update {
            gifticon.value.balance
        }
    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
