package com.lighthouse.presentation.ui.addgifticon

import android.graphics.RectF
import android.net.Uri
import android.text.InputFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toDate
import com.lighthouse.presentation.extension.toDigit
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import com.lighthouse.presentation.mapper.toAddGifticonItemUIModel
import com.lighthouse.presentation.mapper.toAddGifticonUIModel
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonItemUIModel
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.lighthouse.presentation.util.resource.UIText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date

class AddGifticonViewModel : ViewModel() {

    private val today = Calendar.getInstance().let {
        it.set(Calendar.HOUR, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.time
    }

    private val _eventFlow = MutableEventFlow<AddGifticonEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    init {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.NavigateToGallery())
        }
    }

    private val _displayList = MutableStateFlow<List<AddGifticonItemUIModel>>(listOf(AddGifticonItemUIModel.Gallery))
    val displayList = _displayList.asStateFlow()

    private val gifticonList = MutableStateFlow<List<AddGifticonUIModel>>(emptyList())

    private var selectedId = MutableStateFlow(-1L)

    val selectedGifticon = selectedId.combine(gifticonList) { id, list ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isSelected = selectedGifticon.map {
        it != null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isCashCard = selectedGifticon.map {
        it?.isCashCard
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val thumbnailImage = selectedGifticon.map {
        it?.thumbnailImage?.uri ?: it?.origin
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val name = selectedGifticon.map {
        it?.name
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val brand = selectedGifticon.map {
        it?.brandName
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val barcode = selectedGifticon.map {
        it?.barcode
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val expiredAt = selectedGifticon.map {
        val date = it?.expiredAt
        if (date != null && date != EMPTY_DATE) {
            UIText.StringResource(R.string.all_date, date.toYear(), date.toMonth(), date.toDate())
        } else {
            UIText.Empty
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val balanceFormat = DecimalFormat("###,###,###")

    val balance = selectedGifticon.map {
        it?.balance
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val balanceSelection = MutableStateFlow(0)

    val memo = selectedGifticon.map {
        it?.memo
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val registeredSizeText = gifticonList.map { list ->
        if (list.isNotEmpty()) {
            UIText.StringResource(R.string.add_gifticon_registered, list.size)
        } else {
            UIText.Empty
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UIText.Empty)

    private val _isDeleteMode = MutableStateFlow(false)

    fun changeDeleteMode() {
        changeDeleteMode(_isDeleteMode.value.not())
    }

    private fun changeDeleteMode(newDeleteMode: Boolean) {
        _isDeleteMode.value = newDeleteMode
        _displayList.value = _displayList.value.map {
            if (it is AddGifticonItemUIModel.Gifticon) {
                it.copy(isDelete = newDeleteMode)
            } else {
                it
            }
        }
    }

    val deleteModeText = _isDeleteMode.map { isDeleteMode ->
        if (isDeleteMode) {
            UIText.StringResource(R.string.add_gifticon_edit_mode)
        } else {
            UIText.StringResource(R.string.add_gifticon_delete_mode)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UIText.Empty)

    fun loadGalleryImages(list: List<GalleryUIModel.Gallery>) {
        if (list.find { selectedId.value == it.id } == null) {
            selectedId.value = -1L
        }
        if (selectedId.value == -1L) {
            selectedId.value = list.getOrNull(0)?.id ?: -1L
        }

        val oldDisplayList = _displayList.value
        _displayList.value = listOf(AddGifticonItemUIModel.Gallery) + list.map { newItem ->
            oldDisplayList.find { oldItem -> oldItem is AddGifticonItemUIModel.Gifticon && newItem.id == oldItem.id }
                ?: newItem.toAddGifticonItemUIModel()
        }

        val oldGifticonList = gifticonList.value
        gifticonList.value = list.map { newItem ->
            oldGifticonList.find { oldItem -> newItem.id == oldItem.id } ?: newItem.toAddGifticonUIModel()
        }
    }

    private fun updateSelectedDisplayGifticon(update: (AddGifticonItemUIModel.Gifticon) -> AddGifticonItemUIModel.Gifticon) {
        val index = displayList.value.indexOfFirst {
            it is AddGifticonItemUIModel.Gifticon && it.id == selectedId.value
        }
        if (index == -1) {
            return
        }
        val oldList = _displayList.value
        val oldItem = oldList[index] as? AddGifticonItemUIModel.Gifticon ?: return
        val newItem = update(oldItem)
        if (oldItem == newItem) {
            return
        }
        _displayList.value = oldList.subList(0, index) + listOf(newItem) + oldList.subList(index + 1, oldList.size)
    }

    private fun updateSelectedGifticon(update: (AddGifticonUIModel) -> AddGifticonUIModel) {
        val index = gifticonList.value.indexOfFirst { it.id == selectedId.value }
        if (index == -1) {
            return
        }
        val oldList = gifticonList.value
        val oldItem = oldList[index]
        val newItem = update(oldItem)
        if (oldItem == newItem) {
            return
        }
        gifticonList.value =
            oldList.subList(0, index) + listOf(newItem) + oldList.subList(index + 1, oldList.size)
    }

    fun croppedImage(uri: Uri, rect: RectF) {
        val image = CroppedImage(uri, rect)
        updateSelectedDisplayGifticon { it.copy(thumbnailImage = image) }
        updateSelectedGifticon { it.copy(thumbnailImage = image) }
    }

    fun croppedBrandImage(uri: Uri, rect: RectF) {
        updateSelectedGifticon { it.copy(brandImage = CroppedImage(uri, rect)) }
    }

    fun changeCashCard(checked: Boolean) {
        updateSelectedGifticon { it.copy(isCashCard = checked) }
    }

    fun changeGifticonName(name: CharSequence) {
        updateSelectedGifticon { it.copy(name = name.toString()) }
    }

    fun changeBrandName(brandName: CharSequence) {
        updateSelectedGifticon { it.copy(brandName = brandName.toString()) }
    }

    fun changeBarcode(barcode: CharSequence) {
        updateSelectedGifticon { it.copy(barcode = barcode.toString()) }
    }

    fun changeExpiredAt(expiredAt: Date) {
        updateSelectedGifticon { it.copy(expiredAt = expiredAt) }
    }

    val balanceFilters = arrayOf(
        InputFilter.LengthFilter(10),
        InputFilter { source, _, _, _, dstStart, _ ->
            return@InputFilter if (dstStart == 0) {
                var zeroIndex = 0
                for (char in source) {
                    if (char != '0') {
                        break
                    }
                    zeroIndex += 1
                }
                source.subSequence(zeroIndex, source.length)
            } else {
                source
            }
        }
    )

    fun changeBalance(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        val newBalance = charSequence.toString()
        val oldBalance = balance.value ?: return
        if (oldBalance == newBalance) {
            return
        }

        var newValueText = newBalance.filter { it.isDigit() }
        val unitCount = oldBalance.substring(0, start + before).count { it == ',' }
        if (before == 1 && count == 0 && oldBalance[start] == ',') {
            val numIndex = start - unitCount
            newValueText =
                newValueText.substring(0, numIndex) + newValueText.substring(numIndex + 1, newValueText.length)
        }

        val newText = balanceFormat.format(newValueText.toDigit())
        updateSelectedGifticon { it.copy(balance = newText) }

        balanceSelection.value = if (oldBalance.length == start) {
            newText.length
        } else {
            val numIndex = start + count - unitCount
            var numCount = 0
            var newSelection = 0
            while (numCount < numIndex) {
                if (newText[newSelection].isDigit()) {
                    numCount += 1
                }
                newSelection += 1
            }
            newSelection
        }
    }

    fun changeMemo(memo: CharSequence) {
        updateSelectedGifticon { it.copy(memo = memo.toString()) }
    }

    fun selectGifticon(gifticon: AddGifticonItemUIModel.Gifticon) {
        selectedId.value = gifticon.id
    }

    private fun deleteDisplayGifticon(id: Long) {
        val index = _displayList.value.indexOfFirst {
            it is AddGifticonItemUIModel.Gifticon && it.id == id
        }
        if (index == -1) {
            return
        }
        val oldList = _displayList.value
        if (oldList[index] !is AddGifticonItemUIModel.Gifticon) {
            return
        }
        _displayList.value =
            oldList.subList(0, index) + oldList.subList(index + 1, oldList.size)
    }

    private fun deleteGifticon(id: Long) {
        val index = gifticonList.value.indexOfFirst { it.id == id }
        if (index == -1) {
            return
        }
        val oldList = gifticonList.value
        gifticonList.value =
            oldList.subList(0, index) + oldList.subList(index + 1, oldList.size)
    }

    fun deleteGifticon(gifticon: AddGifticonItemUIModel.Gifticon) {
        if (selectedId.value == gifticon.id) {
            selectedId.value = -1
        }
        deleteDisplayGifticon(gifticon.id)
        deleteGifticon(gifticon.id)
    }

    private fun checkGifticonValid(gifticon: AddGifticonUIModel): AddGifticonValid {
        return when {
            gifticon.name.isEmpty() -> AddGifticonValid.INVALID_GIFTICON_NAME
            gifticon.brandName.isEmpty() -> AddGifticonValid.INVALID_BRAND_NAME
            gifticon.barcode.length != 12 && gifticon.barcode.length != 16 -> AddGifticonValid.INVALID_BARCODE
            gifticon.expiredAt < today -> AddGifticonValid.INVALID_EXPIRED_AT
            gifticon.isCashCard && gifticon.balance.isEmpty() -> AddGifticonValid.INVALID_BALANCE
            else -> AddGifticonValid.VALID
        }
    }

    private fun handleGifticonInvalid(valid: AddGifticonValid) {
        viewModelScope.launch {
            when (valid) {
                AddGifticonValid.VALID -> {}
                else -> {
                    _eventFlow.emit(AddGifticonEvent.ShowSnackBar(valid.text))
                    _eventFlow.emit(AddGifticonEvent.RequestFocus(valid.focus))
                }
            }
        }
    }

    fun requestCashCard() {
        val gifticon = selectedGifticon.value ?: return
        if (gifticon.isCashCard) {
            viewModelScope.launch {
                _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.BALANCE))
            }
        }
    }

    fun requestAddGifticon() {
        viewModelScope.launch {
            var valid: AddGifticonValid = AddGifticonValid.VALID
            if (gifticonList.value.isEmpty()) {
                valid = AddGifticonValid.INVALID_EMPTY
            } else {
                for (gifticon in gifticonList.value) {
                    valid = checkGifticonValid(gifticon)
                    if (valid != AddGifticonValid.VALID) {
                        selectedId.emit(gifticon.id)
                        break
                    }
                }
            }
            when (valid) {
                AddGifticonValid.VALID -> {
                }
                else -> {
                    handleGifticonInvalid(valid)
                }
            }
        }
    }

    fun requestPopBackstack() {
        if (gifticonList.value.isEmpty()) {
            popBackstack()
        } else {
            showConfirmation()
        }
    }

    private fun popBackstack() {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.PopupBackStack)
        }
    }

    private fun showConfirmation() {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.ShowConfirmation)
        }
    }

    fun gotoGallery() {
        changeDeleteMode(false)

        val list = _displayList.value.filterIsInstance<AddGifticonItemUIModel.Gifticon>().map { it.id }
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.NavigateToGallery(list))
        }
    }

    fun gotoCropGifticon() {
        changeDeleteMode(false)

        val gifticon = selectedGifticon.value ?: return
        viewModelScope.launch {
            _eventFlow.emit(
                AddGifticonEvent.NavigateToCropGifticon(
                    gifticon.origin,
                    gifticon.thumbnailImage.croppedRect
                )
            )
        }
    }

    fun showOriginGifticon() {
        val originUri = selectedGifticon.value?.origin ?: return
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.ShowOriginGifticon(originUri))
        }
    }

    fun showExpiredAtDatePicker() {
        var expiredAt = selectedGifticon.value?.expiredAt ?: return
        if (expiredAt == EMPTY_DATE) {
            expiredAt = today
        }
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.ShowExpiredAtDatePicker(expiredAt))
        }
    }

    companion object {
        private val EMPTY_DATE = Date(0)
    }
}
