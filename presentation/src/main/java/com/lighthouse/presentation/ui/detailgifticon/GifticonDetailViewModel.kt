package com.lighthouse.presentation.ui.detailgifticon

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetGifticonUseCase
import com.lighthouse.domain.usecase.GetHistoryUseCase
import com.lighthouse.domain.usecase.UnUseGifticonUseCase
import com.lighthouse.domain.usecase.UseCashCardGifticonUseCase
import com.lighthouse.domain.usecase.UseGifticonUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toDayOfMonth
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import com.lighthouse.presentation.extra.Extras.KEY_GIFTICON_ID
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.mapper.toUiModel
import com.lighthouse.presentation.model.CashAmountPreset
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.model.HistoryUiModel
import com.lighthouse.presentation.util.Geography
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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

    val gifticon: StateFlow<GifticonUIModel?> = gifticonDbResult.transform {
        if (it is DbResult.Success) {
            emit(it.data.toPresentation())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val mode = gifticon.mapLatest { gifticon ->
        when (gifticon?.isUsed) {
            true -> GifticonDetailMode.USED
            else -> GifticonDetailMode.UNUSED
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GifticonDetailMode.UNUSED)

    val history = getHistoryUseCase(gifticonId)

    private val latestHistory = history.mapLatest { historyResult ->
        if (historyResult is DbResult.Success) {
            historyResult.data.first().toUiModel(gifticon.value?.name ?: "", geography)
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val latestUsageHistoryUIText = latestHistory.transform {
        val date = it?.date ?: return@transform
        emit(
            UIText.StringResource(
                R.string.gifticon_detail_used_image_label,
                date.toYear(),
                date.toMonth(),
                date.toDayOfMonth(),
            ),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIText.Empty)

    val latestUsedDate = latestHistory.transform {
        val date = it?.date ?: return@transform
        emit(
            UIText.StringResource(
                R.string.all_date,
                date.toYear(),
                date.toMonth(),
                date.toDayOfMonth(),
            ),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIText.Empty)

    val latestUsedLocation = latestHistory.map {
        it?.location?.displayText
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _historyUiModel = MutableStateFlow<List<HistoryUiModel>>(emptyList())
    val historyUiModel = _historyUiModel.asStateFlow()

    val balanceOriginUIText: StateFlow<UIText> = gifticon.transform {
        if (it == null) return@transform
        emit(
            it.balance?.let { balance ->
                UIText.NumberFormatString(balance, R.string.all_cash_unit)
            } ?: UIText.Empty,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIText.Empty)

    val balanceUIText: StateFlow<UIText> = gifticon.transform {
        if (it == null) return@transform
        emit(
            it.balance?.let { balance ->
                UIText.NumberFormatString(balance, R.string.all_balance_label)
            } ?: UIText.Empty,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIText.Empty)

    val failure = gifticonDbResult.transform {
        if (it is DbResult.Failure) {
            emit(it.throwable)
        }
    }

    private val _amountToBeUsed = MutableStateFlow(0)
    val amountToBeUsed = _amountToBeUsed.asStateFlow()

    private val _event = MutableSharedFlow<GifticonDetailEvent>()
    val event = _event.asSharedFlow()

    val scrollDownChipLabel = mode.map { mode ->
        when (mode) {
            GifticonDetailMode.UNUSED -> UIText.StringResource(R.string.gifticon_detail_scroll_down_chip_unused)
            GifticonDetailMode.USED -> UIText.StringResource(R.string.gifticon_detail_scroll_down_chip_used)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIText.Empty)

    val masterButtonLabel = mode.map { mode ->
        when (mode) {
            GifticonDetailMode.UNUSED -> UIText.StringResource(R.string.gifticon_detail_unused_mode_button_text)
            GifticonDetailMode.USED -> UIText.StringResource(R.string.gifticon_detail_used_mode_button_text)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIText.Empty)

    var hasLocationPermission = MutableStateFlow(false)
        private set

    private val _completeUseProgress = MutableStateFlow(false)
    val completeUseProgress = _completeUseProgress.asStateFlow()

    init {
        viewModelScope.launch {
            gifticon.collect { gifticon ->
                gifticon ?: return@collect
                // History 작업
                getHistoryUseCase(gifticonId).collect { historyResult ->
                    when (historyResult) {
                        DbResult.Empty -> _historyUiModel.value = emptyList()
                        is DbResult.Failure -> {
                            // TODO DbResult.Failure
                        }

                        DbResult.Loading -> {
                            // TODO DbResult.Loading
                        }

                        is DbResult.Success -> {
                            val histories = historyResult.data
                            _historyUiModel.value = histories.toUiModel(
                                gifticon,
                                geography,
                            )
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

    fun showAllUsedInfoButtonClicked() {
        event(GifticonDetailEvent.ShowAllUsedInfoButtonClicked)
    }

    fun useGifticonButtonClicked() {
        when (mode.value) {
            GifticonDetailMode.UNUSED -> {
                event(GifticonDetailEvent.UseGifticonButtonClicked)
            }

            GifticonDetailMode.USED -> {
                cancelUsage()
            }
        }
    }

    fun completeUseGifticonButtonClicked() {
        viewModelScope.launch {
            _completeUseProgress.value = true
            if (gifticon.value?.isCashCard == true) {
                assert((gifticon.value?.balance ?: 0) >= amountToBeUsed.value)
                if (amountToBeUsed.value > 0) { // 0원 이상인 경우만 사용 처리
                    useCashCardGifticonUseCase(gifticonId, amountToBeUsed.value, hasLocationPermission.value)
                    editBalance(0)
                } else {
                    event(GifticonDetailEvent.InvalidCashCardUsage)
                }
                event(GifticonDetailEvent.UseGifticonComplete)
            } else {
                useGifticonUseCase(gifticonId, hasLocationPermission.value)
                event(GifticonDetailEvent.UseGifticonComplete)
            }
            _completeUseProgress.value = false
        }
    }

    fun amountChipClicked(amountPreset: CashAmountPreset) {
        amountPreset.amount?.let { amount ->
            editBalance(minOf(_amountToBeUsed.value + amount, gifticon.value?.balance ?: 0))
        } ?: run { // "전액" 버튼이 클릭된 경우
            editBalance(gifticon.value?.balance ?: return@run)
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
        _amountToBeUsed.update { newBalance }
    }

    fun updateLocationPermission(isLocationPermission: Boolean) {
        hasLocationPermission.value = isLocationPermission
    }

    fun cancelUsage() {
        viewModelScope.launch {
            unUseGifticonUseCase(gifticonId)
        }
    }

    private fun event(event: GifticonDetailEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
