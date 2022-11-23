package com.lighthouse.presentation.ui.detailgifticon

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetGifticonUseCase
import com.lighthouse.domain.usecase.UnUseGifticonUseCase
import com.lighthouse.domain.usecase.UseCashCardGifticonUseCase
import com.lighthouse.domain.usecase.UseGifticonUseCase
import com.lighthouse.presentation.extra.Extras.KEY_GIFTICON_ID
import com.lighthouse.presentation.model.CashAmountPreset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifticonDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    getGifticonUseCase: GetGifticonUseCase,
    private val useGifticonUseCase: UseGifticonUseCase,
    private val useCashCardGifticonUseCase: UseCashCardGifticonUseCase,
    private val unUseGifticonUseCase: UnUseGifticonUseCase
) : ViewModel() {

    private val gifticonId = stateHandle.get<String>(KEY_GIFTICON_ID) ?: error("Gifticon id is null")
    private val dbResult =
        getGifticonUseCase(gifticonId).stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    val gifticon = dbResult.transform {
        if (it is DbResult.Success) {
            emit(it.data)
            switchMode(if (it.data.isUsed) GifticonDetailMode.USED else GifticonDetailMode.UNUSED)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val failure = dbResult.transform {
        if (it is DbResult.Failure) {
            emit(it.throwable)
        }
    }

    private val _mode = MutableStateFlow(GifticonDetailMode.UNUSED)
    val mode = _mode.asStateFlow()

    val amountToUse = MutableStateFlow(0)

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    fun switchMode(mode: GifticonDetailMode) {
        _mode.update { mode }
    }

    fun scrollDownForUseButtonClicked() {
        event(Event.ScrollDownForUseButtonClicked)
    }

    fun shareButtonClicked() {
        event(Event.ShareButtonClicked)
    }

    fun editButtonClicked() {
        event(Event.EditButtonClicked)
    }

    fun expireDateClicked() {
        event(Event.ExpireDateClicked)
    }

    fun showAllUsedInfoButtonClicked() {
        event(Event.ShowAllUsedInfoButtonClicked)
    }

    fun useGifticonButtonClicked() {
        when (mode.value) {
            GifticonDetailMode.UNUSED -> {
                viewModelScope.launch {
                    if (gifticon.value?.isCashCard == true) {
                        useCashCardGifticonUseCase(gifticonId, amountToUse.value)
                    } else {
                        useGifticonUseCase(gifticonId)
                    }
                }
            }
            GifticonDetailMode.USED -> {
                viewModelScope.launch {
                    unUseGifticonUseCase(gifticonId)
                }
            }
            GifticonDetailMode.EDIT -> {
                // TODO(수정사항 반영)
                _mode.update { GifticonDetailMode.UNUSED }
            }
        }
    }

    fun amountChipClicked(amountPreset: CashAmountPreset) {
        /*amountPreset.amount?.let { amount ->
            if (amount + amountToUse.value <= gifticon.value.balance) {
                amountToUse.update {
                    it + amount
                }
            }
        } ?: amountToUse.update {
            gifticon.value.balance
        }*/
    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
