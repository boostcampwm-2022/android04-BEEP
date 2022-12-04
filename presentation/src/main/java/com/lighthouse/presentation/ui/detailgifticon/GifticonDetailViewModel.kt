package com.lighthouse.presentation.ui.detailgifticon

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetGifticonUseCase
import com.lighthouse.domain.usecase.GetUsageHistoriesUseCase
import com.lighthouse.domain.usecase.UnUseGifticonUseCase
import com.lighthouse.domain.usecase.UpdateGifticonInfoUseCase
import com.lighthouse.domain.usecase.UseCashCardGifticonUseCase
import com.lighthouse.domain.usecase.UseGifticonUseCase
import com.lighthouse.presentation.extra.Extras.KEY_GIFTICON_ID
import com.lighthouse.presentation.model.CashAmountPreset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class GifticonDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    getGifticonUseCase: GetGifticonUseCase,
    getUsageHistoryUseCase: GetUsageHistoriesUseCase,
    private val useGifticonUseCase: UseGifticonUseCase,
    private val useCashCardGifticonUseCase: UseCashCardGifticonUseCase,
    private val unUseGifticonUseCase: UnUseGifticonUseCase,
    private val updateGifticonInfoUseCase: UpdateGifticonInfoUseCase
) : ViewModel() {

    private val gifticonId = stateHandle.get<String>(KEY_GIFTICON_ID) ?: error("Gifticon id is null")
    private val gifticonDbResult =
        getGifticonUseCase(gifticonId).stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    val gifticon: StateFlow<Gifticon?> = gifticonDbResult.transform {
        if (it is DbResult.Success) {
            emit(it.data)
            switchMode(if (it.data.isUsed) GifticonDetailMode.USED else GifticonDetailMode.UNUSED)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val usageHistoryDbResult = getUsageHistoryUseCase(gifticonId)

    val usageHistory = usageHistoryDbResult.transform {
        if (it is DbResult.Success) {
            emit(it.data)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val latestUsageHistory = usageHistory.transform {
        emit(it?.last())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val failure = gifticonDbResult.transform {
        if (it is DbResult.Failure) {
            emit(it.throwable)
        }
    }

    private val _mode = MutableStateFlow(GifticonDetailMode.UNUSED)
    val mode = _mode.asStateFlow()

    val amountToUse = MutableStateFlow(0)

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val _tempGifticon = MutableStateFlow<Gifticon?>(null)
    val tempGifticon = _tempGifticon.asStateFlow()

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
        Timber.tag("gifticon_detail").d("expireDateClicked() 호출")
        event(Event.ExpireDateClicked)
    }

    fun showAllUsedInfoButtonClicked() {
        event(Event.ShowAllUsedInfoButtonClicked)
    }

    fun useGifticonButtonClicked() {
        when (mode.value) {
            GifticonDetailMode.UNUSED -> {
                event(Event.UseGifticonButtonClicked)
                viewModelScope.launch {
                    if (gifticon.value?.isCashCard == true) {
//                        useCashCardGifticonUseCase(gifticonId, amountToUse.value) // TODO 이걸로 사용해야 함
                        useGifticonUseCase(gifticonId) // TODO 이건 제거
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
                endEdit()
            }
        }
    }

    fun editProductName(newName: String) {
        tempGifticon.value?.let {
            _tempGifticon.value = it.copy(name = newName)
        }
        Timber.tag("edit").d("editProductName: ${tempGifticon.value}")
    }

    fun editBrand(newBrand: String) {
        tempGifticon.value?.let {
            _tempGifticon.value = it.copy(brand = newBrand)
        }
        Timber.tag("edit").d("editBrand: ${tempGifticon.value}")
    }

    fun editBalance(newBalance: Int) {
        tempGifticon.value?.let {
            _tempGifticon.value = it.copy(balance = newBalance)
        }
        Timber.tag("edit").d("editBalance: ${tempGifticon.value}")
    }

    fun editExpireDate(year: Int, month: Int, dayOfMonth: Int) {
        tempGifticon.value?.let {
            val cal = Calendar.getInstance().apply {
                set(year, month - 1, dayOfMonth)
            }
            _tempGifticon.value = it.copy(expireAt = cal.time)
        }
        Timber.tag("edit").d("editExpireDate: ${tempGifticon.value}")
    }

    fun editMemo(newMemo: String) {
        tempGifticon.value?.let {
            _tempGifticon.value = it.copy(memo = newMemo)
        }
        Timber.tag("edit").d("editMemo: ${tempGifticon.value}")
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

    fun rollbackChangedGifticonInfo(before: Gifticon) {
        Timber.tag("edit").d("기프티콘 정보 되돌리기")
        viewModelScope.launch {
            updateGifticonInfoUseCase(before)
        }
    }

    fun startEdit() {
        _tempGifticon.value = gifticon.value ?: return
    }

    private fun endEdit() {
        val before = gifticon.value ?: return
        val after = tempGifticon.value ?: return

        if (before != after) {
            viewModelScope.launch {
                updateGifticonInfoUseCase(after)
            }
        }
        event(Event.OnGifticonInfoChanged(before, after))
        _tempGifticon.value = null
    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
