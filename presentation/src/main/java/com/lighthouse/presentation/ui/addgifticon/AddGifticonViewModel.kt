package com.lighthouse.presentation.ui.addgifticon

import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.addgifticon.RecognizeGifticonImageUseCase
import com.lighthouse.domain.usecase.addgifticon.SaveGifticonsUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toDate
import com.lighthouse.presentation.extension.toDigit
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import com.lighthouse.presentation.mapper.toAddGifticonItemUIModel
import com.lighthouse.presentation.mapper.toAddGifticonUIModel
import com.lighthouse.presentation.mapper.toDomain
import com.lighthouse.presentation.mapper.toGalleryUIModel
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.EditTextInfo
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonItemUIModel
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddGifticonViewModel @Inject constructor(
    private val saveGifticonsUseCase: SaveGifticonsUseCase,
    private val recognizeGifticonImageUseCase: RecognizeGifticonImageUseCase
) : ViewModel() {

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

    private val expiredAtDate: Date?
        get() = selectedGifticon.value?.expiredAt?.let {
            if (it == EMPTY_DATE) today else it
        }

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

    private val displayBarcodeSelection = MutableStateFlow(0)

    val barcode = selectedGifticon.map {
        it?.barcode ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private fun barcodeToTransformed(text: String): String {
        return text.chunked(4).joinToString(" ")
    }

    private fun transformedToBarcode(text: String): String {
        return text.filter { it.isDigit() }
    }

    val displayBarcode = barcode.combine(displayBarcodeSelection) { barcode, selection ->
        val displayText = barcodeToTransformed(barcode)
        EditTextInfo(displayText, min(selection, displayText.length))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EditTextInfo())

    private val displayBalanceSelection = MutableStateFlow(0)

    val balance = selectedGifticon.map {
        it?.balance ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val balanceFormat = DecimalFormat("###,###,###")

    private fun balanceToTransformed(text: String): String {
        return balanceFormat.format(text.toDigit())
    }

    private fun transformedToBalance(text: String): String {
        return text.filter { it.isDigit() }
    }

    val displayBalance = balance.combine(displayBalanceSelection) { balance, selection ->
        val displayText = balanceToTransformed(balance)
        EditTextInfo(displayText, min(selection, displayText.length))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EditTextInfo())

    val expiredAt = selectedGifticon.map {
        val date = it?.expiredAt
        if (date != null && date != EMPTY_DATE) {
            UIText.StringResource(R.string.all_date, date.toYear(), date.toMonth(), date.toDate())
        } else {
            UIText.Empty
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

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

        list.forEach { gallery ->
            viewModelScope.launch {
                val result = recognizeGifticonImageUseCase(gallery.toDomain())
                if (result != null) {
                    val updated = updateGifticon(gallery.id) {
                        result.toPresentation(gallery.id)
                    }
                    if (updated != null) {
                        updateDisplayGifticon(gallery.id) {
                            it.copy(
                                thumbnailImage = updated.thumbnailImage,
                                isValid = checkGifticonValid(updated) == AddGifticonValid.VALID
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateSelectedDisplayGifticon(
        update: (AddGifticonItemUIModel.Gifticon) -> AddGifticonItemUIModel.Gifticon
    ) {
        updateDisplayGifticon(selectedId.value, update)
    }

    private fun updateDisplayGifticon(
        srcIndex: Long?,
        update: (AddGifticonItemUIModel.Gifticon) -> AddGifticonItemUIModel.Gifticon
    ) {
        val index = displayList.value.indexOfFirst {
            it is AddGifticonItemUIModel.Gifticon && it.id == srcIndex
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

    private fun updateSelectedGifticon(
        update: (AddGifticonUIModel) -> AddGifticonUIModel
    ) = updateGifticon(selectedId.value, update)

    private fun updateGifticon(
        srcIndex: Long?,
        update: (AddGifticonUIModel) -> AddGifticonUIModel
    ): AddGifticonUIModel? {
        val index = gifticonList.value.indexOfFirst { it.id == srcIndex }
        if (index == -1) {
            return null
        }
        val oldList = gifticonList.value
        val oldItem = oldList[index]
        val newItem = update(oldItem)
        if (oldItem == newItem) {
            return null
        }
        gifticonList.value =
            oldList.subList(0, index) + listOf(newItem) + oldList.subList(index + 1, oldList.size)
        return newItem
    }

    fun onActionNextListener(actionId: Int): Boolean {
        val gifticon = selectedGifticon.value ?: return false
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            val event = when (checkGifticonValid(gifticon)) {
                AddGifticonValid.INVALID_GIFTICON_NAME -> AddGifticonEvent.RequestFocus(AddGifticonFocus.GIFTICON_NAME)
                AddGifticonValid.INVALID_BRAND_NAME -> AddGifticonEvent.RequestFocus(AddGifticonFocus.BRAND_NAME)
                AddGifticonValid.INVALID_BARCODE -> AddGifticonEvent.RequestFocus(AddGifticonFocus.BARCODE)
                AddGifticonValid.INVALID_BALANCE -> AddGifticonEvent.RequestFocus(AddGifticonFocus.BALANCE)
                AddGifticonValid.INVALID_EXPIRED_AT -> AddGifticonEvent.ShowExpiredAtDatePicker(expiredAtDate ?: today)
                else -> AddGifticonEvent.RequestFocus(AddGifticonFocus.MEMO)
            }
            viewModelScope.launch {
                _eventFlow.emit(event)
            }
            return true
        }
        return false
    }

    fun croppedGifticonImage(croppedImage: CroppedImage) {
        updateSelectedDisplayGifticon { it.copy(thumbnailImage = croppedImage) }
        updateSelectedGifticon { it.copy(thumbnailImage = croppedImage) }
    }

    fun changeCashCard(checked: Boolean) {
        val updated = updateSelectedGifticon { it.copy(isCashCard = checked) }
        if (updated != null) {
            updateSelectedDisplayGifticon { it.copy(isValid = checkGifticonValid(updated) == AddGifticonValid.VALID) }
        }
    }

    fun changeGifticonName(name: CharSequence) {
        val updated = updateSelectedGifticon { it.copy(name = name.toString()) }
        if (updated != null) {
            updateSelectedDisplayGifticon { it.copy(isValid = checkGifticonValid(updated) == AddGifticonValid.VALID) }
        }
    }

    fun changeBrandName(brandName: CharSequence) {
        val updated = updateSelectedGifticon { it.copy(brandName = brandName.toString()) }
        if (updated != null) {
            updateSelectedDisplayGifticon { it.copy(isValid = checkGifticonValid(updated) == AddGifticonValid.VALID) }
        }
    }

    fun changeBarcode(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        val newBarcode = charSequence.toString()
        val oldBarcode = displayBarcode.value.text
        if (oldBarcode == newBarcode) {
            return
        }

        val newValue = transformedToBarcode(newBarcode)
        val newSelection = start + count

//        val newValue = newBarcode.filter { it.isDigit() }
//        var newValueText = newValue
//        val spaceCount = oldBarcode.substring(0, start + before).count { it == ' ' }
//        if (before == 1 && count == 0 && oldBarcode[start] == ' ') {
//            val numIndex = start - spaceCount
//            newValueText =
//                newValueText.substring(0, numIndex) + newValueText.substring(numIndex + 1, newValueText.length)
//        }
//
//        val newText = newValueText.chunked(4).joinToString(" ")
//        val newSelection = if (oldBarcode.length == start) {
//            newText.length
//        } else {
//            val numIndex = start + count - spaceCount
//            var numCount = 0
//            var selection = 0
//            while (numCount < numIndex) {
//                if (newText[selection].isDigit()) {
//                    numCount += 1
//                }
//                selection += 1
//            }
//            selection
//        }

        val updated = updateSelectedGifticon { it.copy(barcode = newValue) }
        viewModelScope.launch {
            displayBarcodeSelection.emit(newSelection)
        }
        if (updated != null) {
            updateSelectedDisplayGifticon { it.copy(isValid = checkGifticonValid(updated) == AddGifticonValid.VALID) }
        }
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
        val oldBalance = displayBalance.value.text
        if (oldBalance == newBalance) {
            return
        }

        val newValue = transformedToBalance(newBalance)
        val newSelection = start + count

//        val newValue = newBalance.filter { it.isDigit() }
//        var newValueText = newValue
//        val unitCount = oldBalance.substring(0, start + before).count { it == ',' }
//        if (before == 1 && count == 0 && oldBalance[start] == ',') {
//            val numIndex = start - unitCount
//            newValueText =
//                newValueText.substring(0, numIndex) + newValueText.substring(numIndex + 1, newValueText.length)
//        }
//
//        val newText = balanceFormat.format(newValueText.toDigit())
//        val newSelection = if (oldBalance.length == start) {
//            newText.length
//        } else {
//            val numIndex = start + count - unitCount
//            var numCount = 0
//            var selection = 0
//            while (numCount < numIndex) {
//                if (newText[selection].isDigit()) {
//                    numCount += 1
//                }
//                selection += 1
//            }
//            selection
//        }

        val updated = updateSelectedGifticon { it.copy(balance = newValue) }
        viewModelScope.launch {
            displayBalanceSelection.emit(newSelection)
        }
        if (updated != null) {
            updateSelectedDisplayGifticon { it.copy(isValid = checkGifticonValid(updated) == AddGifticonValid.VALID) }
        }
    }

    fun changeExpiredAt(expiredAt: Date) {
        val updated = updateSelectedGifticon { it.copy(expiredAt = expiredAt) }
        if (updated != null) {
            updateSelectedDisplayGifticon { it.copy(isValid = checkGifticonValid(updated) == AddGifticonValid.VALID) }
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
            gifticon.barcode.length != 12 &&
                gifticon.barcode.length != 14 &&
                gifticon.barcode.length != 16 -> AddGifticonValid.INVALID_BARCODE
            gifticon.expiredAt == EMPTY_DATE -> AddGifticonValid.INVALID_EXPIRED_AT
            gifticon.isCashCard && gifticon.balance.toDigit() == 0 -> AddGifticonValid.INVALID_BALANCE
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
        viewModelScope.launch {
            val event = if (gifticon.isCashCard) {
                AddGifticonEvent.RequestFocus(AddGifticonFocus.BALANCE)
            } else {
                AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE)
            }
            _eventFlow.emit(event)
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
                    val gifticons = gifticonList.value.map {
                        it.toDomain()
                    }
                    saveGifticonsUseCase(gifticons)
                    _eventFlow.emit(AddGifticonEvent.RegistrationCompleted)
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
            showCancelConfirmation()
        }
    }

    private fun popBackstack() {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE))
            _eventFlow.emit(AddGifticonEvent.PopupBackStack)
        }
    }

    private fun showCancelConfirmation() {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowCancelConfirmation)
        }
    }

    fun showDeleteConfirmation(gifticon: AddGifticonItemUIModel.Gifticon) {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowDeleteConfirmation(gifticon))
        }
    }

    fun gotoGallery() {
        changeDeleteMode(false)

        val list = gifticonList.value.mapIndexed { index, gifticon ->
            gifticon.toGalleryUIModel(index + 1)
        }
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE))
            _eventFlow.emit(AddGifticonEvent.NavigateToGallery(list))
        }
    }

    fun gotoCropGifticon() {
        changeDeleteMode(false)

        val gifticon = selectedGifticon.value ?: return
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE))
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
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowOriginGifticon(originUri))
        }
    }

    fun showExpiredAtDatePicker() {
        val expiredAt = expiredAtDate ?: return
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonFocus.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowExpiredAtDatePicker(expiredAt))
        }
    }

    companion object {
        private val EMPTY_DATE = Date(0)
    }
}
