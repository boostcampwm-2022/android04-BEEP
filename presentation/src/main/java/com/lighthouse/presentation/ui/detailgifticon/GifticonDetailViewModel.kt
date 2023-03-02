package com.lighthouse.presentation.ui.detailgifticon

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.History
import com.lighthouse.domain.usecase.GetGifticonUseCase
import com.lighthouse.domain.usecase.GetHistoryUseCase
import com.lighthouse.domain.usecase.UnUseGifticonUseCase
import com.lighthouse.domain.usecase.UseCashCardGifticonUseCase
import com.lighthouse.domain.usecase.UseGifticonUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toConcurrency
import com.lighthouse.presentation.extension.toString
import com.lighthouse.presentation.extra.Extras.KEY_GIFTICON_ID
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.CashAmountPreset
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.model.HistoryUiModel
import com.lighthouse.presentation.util.Geography
import com.lighthouse.presentation.util.resource.UIText
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
import javax.inject.Inject

@HiltViewModel
class GifticonDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    getGifticonUseCase: GetGifticonUseCase,
    getHistoryUseCase: GetHistoryUseCase,
    private val useGifticonUseCase: UseGifticonUseCase,
    private val useCashCardGifticonUseCase: UseCashCardGifticonUseCase,
    private val unUseGifticonUseCase: UnUseGifticonUseCase,
    private val geography: Geography,
) : ViewModel() {

    private val gifticonId = stateHandle.get<String>(KEY_GIFTICON_ID) ?: error("Gifticon id is null")
    private val gifticonDbResult =
        getGifticonUseCase(gifticonId).stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    private val _mode = MutableStateFlow(GifticonDetailMode.UNUSED)
    val mode = _mode.asStateFlow()

    val gifticon: StateFlow<GifticonUIModel?> = gifticonDbResult.transform {
        if (it is DbResult.Success) {
            emit(it.data.toPresentation())
            switchMode(if (it.data.isUsed) GifticonDetailMode.USED else GifticonDetailMode.UNUSED)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _history = MutableStateFlow<List<HistoryUiModel>>(emptyList())
    val history = _history.asStateFlow()

    val balanceUIText: StateFlow<UIText> = gifticon.transform {
        if (it == null) return@transform
        emit(UIText.StringResource(R.string.all_balance_label, it.balance.toConcurrency()))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIText.Empty)

    val failure = gifticonDbResult.transform {
        if (it is DbResult.Failure) {
            emit(it.throwable)
        }
    }

    val amountToBeUsed = MutableStateFlow(0)

    private val _event = MutableSharedFlow<GifticonDetailEvent>()
    val event = _event.asSharedFlow()

    private val _tempGifticon = MutableStateFlow<GifticonUIModel?>(null)
    val tempGifticon = _tempGifticon.asStateFlow()

    private val _scrollDownChipLabel = MutableStateFlow<UIText>(UIText.Empty)
    val scrollDownChipLabel = _scrollDownChipLabel.asStateFlow()

    private val _masterButtonLabel = MutableStateFlow<UIText>(UIText.Empty)
    val masterButtonLabel = _masterButtonLabel.asStateFlow()

    var hasLocationPermission = MutableStateFlow(false)
        private set

    init {
        viewModelScope.launch {
            // History 작업
            getHistoryUseCase(gifticonId).collect { historyResult ->
                when (historyResult) {
                    DbResult.Empty -> _history.value = emptyList()
                    is DbResult.Failure -> {
                        // TODO DbResult.Failure
                    }
                    DbResult.Loading -> {
                        // TODO DbResult.Loading
                    }
                    is DbResult.Success -> {
                        val histories = historyResult.data
                        _history.value = histories.fold(mutableListOf<HistoryUiModel>()) { acc, history ->
                            if (acc.isEmpty()) {
                                history.date
                                acc.add(HistoryUiModel.Header(history.date.toString("yyyy-MM-dd")))
                            } else if (acc.last() is HistoryUiModel.History && (acc.last() as HistoryUiModel.History).date.toString(
                                    "yyyy-MM-dd",
                                ) != history.date.toString("yyyy-MM-dd")
                            ) {
                                acc.add(HistoryUiModel.Header(history.date.toString("yyyy-MM-dd")))
                            }

                            val typeRes = when (history) {
                                is History.Init -> R.string.history_type_init
                                is History.Use -> R.string.history_type_use
                                is History.UseCashCard -> R.string.history_type_use
                                is History.CancelUsage -> R.string.history_type_cancel
                                is History.ModifyAmount -> R.string.history_type_modify_balance
                            }

                            val gifticon = gifticon.value ?: return@collect
                            val location = when (history) {
                                is History.Use -> geography.getAddress(history.location)
                                is History.UseCashCard -> geography.getAddress(history.location)
                                else -> ""
                            }
                            acc.add(
                                HistoryUiModel.History(
                                    date = history.date,
                                    type = UIText.StringResource(typeRes),
                                    gifticonName = gifticon.name,
                                    balance = UIText.StringResource(
                                        R.string.all_cash_unit,
                                        gifticon.balance.toString(),
                                    ),
                                    location = location,
                                ),
                            )
                            acc
                        }
                    }
                }
            }
        }
    }

    fun scrollDownForUseButtonClicked() {
        event(GifticonDetailEvent.ScrollDownForUseButtonClicked)
    }

    fun shareButtonClicked() {
        event(GifticonDetailEvent.ShareButtonClicked)
    }

    fun editButtonClicked() {
        event(GifticonDetailEvent.EditButtonClicked)
    }

    fun expireDateClicked() {
        event(GifticonDetailEvent.ExpireDateClicked)
    }

    fun showAllUsedInfoButtonClicked() {
        event(GifticonDetailEvent.ShowAllUsedInfoButtonClicked)
    }

    fun useGifticonButtonClicked() {
        when (mode.value) {
            GifticonDetailMode.UNUSED -> {
                event(GifticonDetailEvent.UseGifticonButtonClicked)
            }

            GifticonDetailMode.USED -> {
                viewModelScope.launch {
                    unUseGifticonUseCase(gifticonId)
                }
            }

            GifticonDetailMode.EDIT -> {
                if (checkEditValidation().not()) {
                    event(GifticonDetailEvent.ExistEmptyInfo)
                } else {
                    _mode.value = GifticonDetailMode.UNUSED
                }
            }
        }
    }

    fun completeUseGifticonButtonClicked() {
        viewModelScope.launch {
            if (gifticon.value?.isCashCard == true) {
                assert((gifticon.value?.balance ?: 0) >= amountToBeUsed.value)
                useCashCardGifticonUseCase(gifticonId, amountToBeUsed.value, hasLocationPermission.value)
                amountToBeUsed.value = 0
                event(GifticonDetailEvent.UseGifticonComplete)
            } else {
                useGifticonUseCase(gifticonId, hasLocationPermission.value)
                event(GifticonDetailEvent.UseGifticonComplete)
            }
        }
    }

    fun amountChipClicked(amountPreset: CashAmountPreset) {
        amountPreset.amount?.let { amount ->
            amountToBeUsed.update {
                minOf(it + amount, gifticon.value?.balance ?: 0)
            }
        } ?: run { // "전액" 버튼이 클릭된 경우
            amountToBeUsed.update {
                gifticon.value?.balance ?: return@run
            }
        }
    }

    fun showOriginalImage() {
        val origin = gifticon.value?.originPath ?: return
        event(GifticonDetailEvent.ShowOriginalImage(origin))
    }

    fun showLargeBarcode() {
        viewModelScope.launch {
            val barcode = gifticon.value?.barcode ?: return@launch
            event(GifticonDetailEvent.ShowLargeBarcode(barcode))
        }
    }

    fun editBalance(newBalance: Int) {
        tempGifticon.value?.let {
            _tempGifticon.value = it.copy(balance = newBalance)
        }
    }

    fun updateLocationPermission(isLocationPermission: Boolean) {
        hasLocationPermission.value = isLocationPermission
    }

    private fun switchMode(mode: GifticonDetailMode) {
        _mode.value = mode
        _scrollDownChipLabel.value = when (_mode.value) {
            GifticonDetailMode.UNUSED -> UIText.StringResource(R.string.gifticon_detail_scroll_down_chip_unused)
            GifticonDetailMode.EDIT -> UIText.StringResource(R.string.gifticon_detail_scroll_down_chip_edit)
            GifticonDetailMode.USED -> UIText.StringResource(R.string.gifticon_detail_scroll_down_chip_used)
        }
        _masterButtonLabel.value = when (_mode.value) {
            GifticonDetailMode.UNUSED -> UIText.StringResource(R.string.gifticon_detail_unused_mode_button_text)
            GifticonDetailMode.EDIT -> UIText.StringResource(R.string.gifticon_detail_edit_mode_button_text)
            GifticonDetailMode.USED -> UIText.StringResource(R.string.gifticon_detail_used_mode_button_text)
        }
    }

    private fun checkEditValidation(): Boolean {
        val temp = tempGifticon.value ?: return true
        return (temp.name.trim().isBlank() || temp.brand.trim().isBlank()).not()
    }

    private fun event(event: GifticonDetailEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
