package com.lighthouse.presentation.ui.edit.addgifticon

import android.graphics.RectF
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.core.exts.toDayOfMonth
import com.lighthouse.core.exts.toDigit
import com.lighthouse.core.exts.toMonth
import com.lighthouse.core.exts.toYear
import com.lighthouse.core.utils.flow.MutableEventFlow
import com.lighthouse.core.utils.flow.asEventFlow
import com.lighthouse.domain.usecase.edit.HasGifticonBrandUseCase
import com.lighthouse.domain.usecase.edit.addgifticon.AddRecognizeUseCase
import com.lighthouse.domain.usecase.edit.addgifticon.SaveGifticonsUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.mapper.toAddGifticonItemUIModel
import com.lighthouse.presentation.mapper.toAddGifticonUIModel
import com.lighthouse.presentation.mapper.toDomain
import com.lighthouse.presentation.mapper.toGalleryUIModel
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.edit.addgifticon.adapter.AddGifticonItemUIModel
import com.lighthouse.presentation.ui.edit.addgifticon.event.AddGifticonCrop
import com.lighthouse.presentation.ui.edit.addgifticon.event.AddGifticonEvent
import com.lighthouse.presentation.ui.edit.addgifticon.event.AddGifticonTag
import com.lighthouse.presentation.ui.edit.addgifticon.event.AddGifticonValid
import com.lighthouse.presentation.util.resource.AnimInfo
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddGifticonViewModel @Inject constructor(
    private val saveGifticonsUseCase: SaveGifticonsUseCase,
    private val hasGifticonBrandUseCase: HasGifticonBrandUseCase,
    private val addRecognizeUseCase: AddRecognizeUseCase
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

    private val _displayList =
        MutableStateFlow<List<AddGifticonItemUIModel>>(listOf(AddGifticonItemUIModel.Gallery))
    val displayList = _displayList.asStateFlow()

    private val gifticonList = MutableStateFlow<List<AddGifticonUIModel>>(emptyList())

    val registeredSizeText = gifticonList.map { list ->
        if (list.isNotEmpty()) {
            UIText.StringResource(R.string.add_gifticon_registered, list.size)
        } else {
            UIText.Empty
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UIText.Empty)

    private fun updateSelectedDisplayGifticon(
        update: (AddGifticonItemUIModel.Gifticon) -> AddGifticonItemUIModel.Gifticon
    ) {
        updateDisplayGifticon(selectedGifticon.value?.id, update)
    }

    private fun updateDisplayGifticon(
        gifticonId: Long?,
        update: (AddGifticonItemUIModel.Gifticon) -> AddGifticonItemUIModel.Gifticon
    ) {
        val index = displayList.value.indexOfFirst {
            it is AddGifticonItemUIModel.Gifticon && it.id == gifticonId
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
        _displayList.value =
            oldList.subList(0, index) + listOf(newItem) + oldList.subList(index + 1, oldList.size)
    }

    private fun updateSelectedGifticon(
        checkValid: Boolean = false,
        update: (AddGifticonUIModel) -> AddGifticonUIModel
    ) = updateGifticon(checkValid, selectedGifticon.value?.id, update)

    private fun updateGifticon(
        checkValid: Boolean = false,
        gifticonId: Long? = selectedGifticon.value?.id,
        update: (AddGifticonUIModel) -> AddGifticonUIModel
    ): AddGifticonUIModel? {
        val index = gifticonList.value.indexOfFirst { it.id == gifticonId }
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

        if (checkValid) {
            updateDisplayGifticon(gifticonId) { it.copy(isValid = checkGifticonValid(newItem) == AddGifticonValid.VALID) }
        }
        return newItem
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
        _displayList.value = oldList.subList(0, index) + oldList.subList(index + 1, oldList.size)
    }

    private fun deleteGifticon(id: Long) {
        val index = gifticonList.value.indexOfFirst { it.id == id }
        if (index == -1) {
            return
        }
        val oldList = gifticonList.value
        gifticonList.value = oldList.subList(0, index) + oldList.subList(index + 1, oldList.size)
    }

    fun deleteGifticon(gifticon: AddGifticonItemUIModel.Gifticon) {
        if (selectedGifticon.value?.id == gifticon.id) {
            val oldList = gifticonList.value
            val index = oldList.indexOfFirst { it.id == selectedGifticon.value?.id }
            val id = when {
                index + 1 < oldList.size -> oldList[index + 1].id
                index - 1 >= 0 -> oldList[index - 1].id
                else -> -1
            }
            selectGifticonId(id)
        }
        deleteDisplayGifticon(gifticon.id)
        deleteGifticon(gifticon.id)
    }

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

    private var selectedId = MutableSharedFlow<Long>()

    private fun selectGifticonId(id: Long?) {
        id ?: return
        viewModelScope.launch {
            selectedId.emit(id)
        }
    }

    fun selectGifticon(gifticon: AddGifticonItemUIModel.Gifticon) {
        selectGifticonId(gifticon.id)
    }

    val displayName = MutableStateFlow("")
    val displayBrand = MutableStateFlow("")

    val selectedGifticon = selectedId.onEach { selectedId ->
        _displayList.value = displayList.value.map {
            if (it is AddGifticonItemUIModel.Gifticon) {
                it.copy(isSelected = it.id == selectedId)
            } else {
                it
            }
        }
        val gifticon = gifticonList.value.find { it.id == selectedId }
        displayName.value = gifticon?.name ?: ""
        displayBrand.value = gifticon?.brandName ?: ""
    }.combine(gifticonList) { id, list ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val expiredAtDate: Date?
        get() = selectedGifticon.value?.expiredAt?.let {
            if (it == EMPTY_DATE) today else it
        }

    val isSelected = selectedGifticon.map {
        it != null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val gifticonImage = selectedGifticon.map {
        it?.gifticonImage
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val displayGifticonImage = selectedGifticon.map {
        it?.uri
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateCroppedGifticonImage(croppedImage: CroppedImage) {
        val updated = updateSelectedGifticon { it.copy(gifticonImage = croppedImage) } ?: return
        updateSelectedDisplayGifticon {
            it.copy(
                thumbnailImage = croppedImage,
                isValid = checkGifticonValid(updated) == AddGifticonValid.VALID
            )
        }
    }

    private val isApproveGifticonImage = selectedGifticon.map {
        it?.approveGifticonImage ?: false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isApproveGifticonImageDescriptionVisible =
        gifticonImage.combine(isApproveGifticonImage) { image, isApprove ->
            if (image == null) false else image.uri == null && isApprove.not()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun approveGifticonImage() {
        updateSelectedGifticon(true) {
            it.copy(approveGifticonImage = true)
        }
    }

    val isCashCard = selectedGifticon.map {
        it?.isCashCard
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun updateCashCard(checked: Boolean) {
        updateSelectedGifticon(true) { it.copy(isCashCard = checked) }
    }

    private val name = selectedGifticon.map {
        it?.name
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateGifticonName(name: CharSequence) {
        updateSelectedGifticon(true) { it.copy(name = name.toString()) }
    }

    private val nameFocus = MutableStateFlow(false)

    fun onNameFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(AddGifticonTag.GIFTICON_NAME)
        }
        nameFocus.value = hasFocus
    }

    val nameRemoveVisible = name.combine(nameFocus) { name, focus ->
        !name.isNullOrEmpty() && focus
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeName() {
        updateGifticonName("")
    }

    private val brand = selectedGifticon.map {
        it?.brandName ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val confirmedBrandMap = hashMapOf<String, Boolean>()

    private val approveBrandName = selectedGifticon.map {
        it?.approveBrandName ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private var hasGifticonBrandJob: Job? = null

    private fun checkHasGifticonBrand(brand: String) {
        hasGifticonBrandJob?.cancel()
        if (brand.isNotEmpty() && brand != approveBrandName.value) {
            hasGifticonBrandJob = viewModelScope.launch {
                isLoadingConfirmBrand.value = true
                delay(1000)
                val approve = confirmedBrandMap[brand] ?: run {
                    hasGifticonBrandUseCase(brand).also {
                        confirmedBrandMap[brand] = it
                    }
                }
                updateApproveBrandName(if (approve) brand else "")
            }
            hasGifticonBrandJob?.invokeOnCompletion {
                isLoadingConfirmBrand.value = false
            }
        }
    }

    fun updateBrandName(brandName: CharSequence) {
        updateSelectedGifticon(true) { it.copy(brandName = brandName.toString()) }
        checkHasGifticonBrand(brandName.toString())
    }

    private fun updateApproveBrandName(approveBrandName: String) {
        updateSelectedGifticon(true) {
            it.copy(approveBrandName = approveBrandName)
        }
    }

    private val isApproveBrandName = brand.combine(approveBrandName) { brand, approveBrand ->
        brand == approveBrand
    }

    private val brandFocus = MutableStateFlow(false)

    fun onBrandFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(AddGifticonTag.BRAND_NAME)
        }
        brandFocus.value = hasFocus
    }

    val brandRemoveVisible = brand.combine(brandFocus) { brand, focus ->
        brand.isNotEmpty() && focus
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeBrand() {
        updateBrandName("")
    }

    val isLoadingConfirmBrand = MutableStateFlow(false)

    private val isApproveBrandNameVisible =
        brand.combine(isLoadingConfirmBrand) { brand, isLoading ->
            brand != "" && !isLoading
        }

    val isApproveBrandNameVisibility = isApproveBrandNameVisible.map { isVisible ->
        if (isVisible) View.VISIBLE else View.INVISIBLE
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isApproveBrandNameDescriptionVisible =
        isApproveBrandName.combine(isApproveBrandNameVisible) { isApprove, isVisible ->
            isApprove.not() && isVisible
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isApproveBrandNameResId = isApproveBrandName.map { isApprove ->
        if (isApprove) R.drawable.ic_confirm else R.drawable.ic_question
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isApproveBrandNameTint = isApproveBrandName.map { isApprove ->
        if (isApprove) R.color.point_green else R.color.yellow
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isApproveBrandNameAnimation =
        isApproveBrandName.combine(isApproveBrandNameVisible) { isApprove, isVisible ->
            if (isApprove) {
                AnimInfo.AnimResource(R.anim.anim_fadein_up, isVisible)
            } else {
                AnimInfo.AnimResource(R.anim.anim_jump, isVisible)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, AnimInfo.Empty)

    fun approveBrandName() {
        val approveBrandName = brand.value
        confirmedBrandMap[approveBrandName] = true
        gifticonList.value.forEach { gifticon ->
            if (gifticon.brandName != approveBrandName) {
                return@forEach
            }
            updateGifticon(true, gifticon.id) {
                it.copy(approveBrandName = approveBrandName)
            }
        }
    }

    val barcode = selectedGifticon.map {
        it?.barcode ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private fun updateBarcode(barcode: String) {
        updateSelectedGifticon(true) {
            it.copy(barcode = barcode)
        }
    }

    fun changeBarcode(value: String) {
        updateBarcode(value)
    }

    private val barcodeFocus = MutableStateFlow(false)

    fun onBarcodeFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(AddGifticonTag.BARCODE)
        }
        barcodeFocus.value = hasFocus
    }

    val barcodeRemoveVisible = barcode.combine(barcodeFocus) { barcode, focus ->
        barcode.isNotEmpty() && focus
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeBarcode() {
        updateBarcode("")
    }

    val balance = selectedGifticon.map {
        it?.balance ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private fun updateBalance(balance: String) {
        updateSelectedGifticon(true) {
            it.copy(balance = balance)
        }
    }

    fun changeBalance(value: String) {
        updateBalance(value)
    }

    private val balanceFocus = MutableStateFlow(false)

    fun onBalanceFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(AddGifticonTag.BALANCE)
        }
        balanceFocus.value = hasFocus
    }

    val balanceRemoveVisible = balance.combine(balanceFocus) { balance, focus ->
        balance != "0" && balance.isNotEmpty() && focus
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeBalance() {
        updateBalance("")
    }

    private val expiredAt = selectedGifticon.map {
        it?.expiredAt ?: EMPTY_DATE
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EMPTY_DATE)

    val expiredAtUIText = expiredAt.map { date ->
        if (date != EMPTY_DATE) {
            UIText.StringResource(
                R.string.all_date,
                date.toYear(),
                date.toMonth(),
                date.toDayOfMonth()
            )
        } else {
            UIText.Empty
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UIText.Empty)

    private val approveExpiredAt = selectedGifticon.map {
        it?.approveExpiredAt ?: false
    }

    fun updateExpiredAt(expiredAt: Date) {
        updateSelectedGifticon(true) { it.copy(expiredAt = expiredAt) }
    }

    private val isApproveExpired = expiredAt.combine(approveExpiredAt) { expiredAt, approve ->
        expiredAt >= today || approve
    }

    val isApproveExpiredAtDescriptionVisible =
        expiredAt.combine(approveExpiredAt) { expiredAt, approve ->
            expiredAt != EMPTY_DATE && (expiredAt < today && approve.not())
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isApproveExpiredAtVisible = expiredAt.map { expiredAt ->
        expiredAt != EMPTY_DATE
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isApproveExpiredAtResId = isApproveExpired.map { isApprove ->
        if (isApprove) R.drawable.ic_confirm else R.drawable.ic_question
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isApproveExpiredAtTint = isApproveExpired.map { isApprove ->
        if (isApprove) R.color.point_green else R.color.yellow
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isApproveExpiredAtAnimation =
        isApproveExpired.combine(isApproveExpiredAtVisible) { isApprove, isVisible ->
            if (isApprove) {
                AnimInfo.AnimResource(R.anim.anim_fadein_up, isVisible)
            } else {
                AnimInfo.AnimResource(R.anim.anim_jump, isVisible)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, AnimInfo.Empty)

    fun approveExpiredAt() {
        updateSelectedGifticon(true) {
            it.copy(approveExpiredAt = true)
        }
    }

    val memo = selectedGifticon.map {
        it?.memo
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateMemo(memo: CharSequence) {
        updateSelectedGifticon { it.copy(memo = memo.toString()) }
    }

    fun onActionNextListener(actionId: Int): Boolean {
        val gifticon = selectedGifticon.value ?: return false
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            val event = when (checkGifticonValid(gifticon)) {
                AddGifticonValid.INVALID_GIFTICON_NAME -> AddGifticonEvent.RequestFocus(
                    AddGifticonTag.GIFTICON_NAME
                )

                AddGifticonValid.INVALID_BRAND_NAME -> AddGifticonEvent.RequestFocus(AddGifticonTag.BRAND_NAME)
                AddGifticonValid.INVALID_BARCODE -> AddGifticonEvent.RequestFocus(AddGifticonTag.BARCODE)
                AddGifticonValid.INVALID_BALANCE -> AddGifticonEvent.RequestFocus(AddGifticonTag.BALANCE)
                AddGifticonValid.INVALID_EXPIRED_AT -> AddGifticonEvent.ShowExpiredAtDatePicker(
                    expiredAtDate ?: today
                )

                else -> {
                    requestAddGifticon()
                    return true
                }
            }
            viewModelScope.launch {
                _eventFlow.emit(event)
            }
            return true
        }
        return false
    }

    fun loadGalleryImages(list: List<GalleryUIModel.Gallery>) {
        val oldDisplayList = _displayList.value
        _displayList.value = listOf(AddGifticonItemUIModel.Gallery) + list.map { newItem ->
            oldDisplayList.find { oldItem -> oldItem is AddGifticonItemUIModel.Gifticon && newItem.id == oldItem.id }
                ?: newItem.toAddGifticonItemUIModel()
        }

        val oldGifticonList = gifticonList.value
        gifticonList.value = list.map { newItem ->
            oldGifticonList.find { oldItem -> newItem.id == oldItem.id }
                ?: newItem.toAddGifticonUIModel()
        }

        val newList = list.filter { newItem ->
            oldGifticonList.none { oldItem ->
                oldItem.id == newItem.id
            }
        }

        selectGifticonId(newList.getOrNull(0)?.id)

        recognizeGifticonList(newList)
    }

    private fun recognizeGifticonList(list: List<GalleryUIModel.Gallery>) {
        if (list.isEmpty()) {
            return
        }

        requestLoading(true)
        viewModelScope.launch {
            launch {
                list.forEach { gallery ->
                    launch {
                        recognizeGifticonItem(gallery)
                    }
                }
            }.join()
            requestLoading(false)
        }
    }

    private suspend fun recognizeGifticonItem(gallery: GalleryUIModel.Gallery) {
        val result = addRecognizeUseCase.gifticon(gallery.toDomain()) ?: return
        var approveBrandName = ""
        if (result.brandName != "" && hasGifticonBrandUseCase(result.brandName)) {
            approveBrandName = result.brandName
        }
        val updated = updateGifticon(gifticonId = gallery.id) {
            result.toPresentation(
                id = gallery.id,
                createdDate = gallery.createdDate,
                approveBrandName = approveBrandName
            )
        } ?: return
        updateDisplayGifticon(gallery.id) {
            it.copy(
                thumbnailImage = updated.gifticonImage,
                isValid = checkGifticonValid(updated) == AddGifticonValid.VALID
            )
        }
        if (updated.id == selectedGifticon.value?.id) {
            selectGifticonId(updated.id)
        }
    }

    fun recognizeGifticonName(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = addRecognizeUseCase.gifticonName(uri.toString())
            if (result != "") {
                updateGifticon(true) {
                    it.copy(
                        name = result,
                        nameRectF = croppedImage.croppedRect
                    )
                }
            } else {
                updateGifticon { it.copy(nameRectF = croppedImage.croppedRect) }
                _eventFlow.emit(AddGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_name)))
            }
            selectGifticonId(selectedGifticon.value?.id)
        }
    }

    fun recognizeBrand(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = addRecognizeUseCase.brandName(uri.toString())
            if (result != "") {
                updateGifticon(true) {
                    it.copy(
                        brandName = result,
                        brandNameRectF = croppedImage.croppedRect
                    )
                }
            } else {
                updateGifticon { it.copy(brandNameRectF = croppedImage.croppedRect) }
                _eventFlow.emit(AddGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_brand)))
            }
            selectGifticonId(selectedGifticon.value?.id)
        }
    }

    fun recognizeBarcode(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = addRecognizeUseCase.barcode(uri.toString())
            if (result != "") {
                updateGifticon(true) {
                    it.copy(
                        barcode = result,
                        barcodeRectF = croppedImage.croppedRect
                    )
                }
            } else {
                updateGifticon { it.copy(barcodeRectF = croppedImage.croppedRect) }
                _eventFlow.emit(AddGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_barcode)))
            }
            selectGifticonId(selectedGifticon.value?.id)
        }
    }

    fun recognizeBalance(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = addRecognizeUseCase.balance(uri.toString())
            if (result > 0) {
                updateGifticon(true) {
                    it.copy(
                        isCashCard = true,
                        balance = result.toString(),
                        balanceRectF = croppedImage.croppedRect
                    )
                }
            } else {
                updateGifticon { it.copy(balanceRectF = croppedImage.croppedRect) }
                _eventFlow.emit(AddGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_balance)))
            }
            selectGifticonId(selectedGifticon.value?.id)
        }
    }

    fun recognizeExpired(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = addRecognizeUseCase.expired(uri.toString())
            if (result != EMPTY_DATE) {
                updateGifticon(true) {
                    it.copy(
                        expiredAt = result,
                        expiredAtRectF = croppedImage.croppedRect
                    )
                }
            } else {
                updateGifticon { it.copy(expiredAtRectF = croppedImage.croppedRect) }
                _eventFlow.emit(AddGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_expired_at)))
            }
            selectGifticonId(selectedGifticon.value?.id)
        }
    }

    private fun checkGifticonValid(gifticon: AddGifticonUIModel): AddGifticonValid {
        return when {
            gifticon.name.isEmpty() -> AddGifticonValid.INVALID_GIFTICON_NAME
            gifticon.brandName.isEmpty() -> AddGifticonValid.INVALID_BRAND_NAME
            gifticon.brandName != gifticon.approveBrandName -> AddGifticonValid.INVALID_APPROVE_BRAND_NAME
            gifticon.barcode.length !in VALID_BARCODE_COUNT -> AddGifticonValid.INVALID_BARCODE
            gifticon.isCashCard && gifticon.balance.toDigit() == 0 -> AddGifticonValid.INVALID_BALANCE
            gifticon.expiredAt == EMPTY_DATE -> AddGifticonValid.INVALID_EXPIRED_AT
            gifticon.expiredAt < today && gifticon.approveExpiredAt.not() -> AddGifticonValid.INVALID_APPROVE_EXPIRED_AT
            gifticon.gifticonImage.uri == null && gifticon.approveGifticonImage.not() -> AddGifticonValid.INVALID_APPROVE_GIFTICON_IMAGE
            else -> AddGifticonValid.VALID
        }
    }

    private fun handleGifticonInvalid(valid: AddGifticonValid) {
        viewModelScope.launch {
            when (valid) {
                AddGifticonValid.VALID -> {}
                else -> {
                    _eventFlow.emit(AddGifticonEvent.ShowSnackBar(valid.text))
                    _eventFlow.emit(AddGifticonEvent.RequestFocus(valid.tag))
                    _eventFlow.emit(AddGifticonEvent.RequestScroll(valid.tag))
                }
            }
        }
    }

    fun requestCashCard() {
        val gifticon = selectedGifticon.value ?: return
        viewModelScope.launch {
            val event = if (gifticon.isCashCard) {
                AddGifticonEvent.RequestFocus(AddGifticonTag.BALANCE)
            } else {
                AddGifticonEvent.RequestFocus(AddGifticonTag.NONE)
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
                        selectGifticonId(gifticon.id)
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
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonTag.NONE))
            _eventFlow.emit(AddGifticonEvent.PopupBackStack)
        }
    }

    private fun showCancelConfirmation() {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonTag.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowCancelConfirmation)
        }
    }

    fun showDeleteConfirmation(gifticon: AddGifticonItemUIModel.Gifticon) {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonTag.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowDeleteConfirmation(gifticon))
        }
    }

    fun gotoGallery() {
        changeDeleteMode(false)

        val list = gifticonList.value.mapIndexed { index, gifticon ->
            gifticon.toGalleryUIModel(index + 1)
        }
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonTag.NONE))
            _eventFlow.emit(AddGifticonEvent.NavigateToGallery(list))
        }
    }

    fun gotoCropGifticonImage() {
        val gifticon = selectedGifticon.value ?: return
        gotoCropGifticon(
            crop = AddGifticonCrop.GIFTICON_IMAGE,
            croppedRect = gifticon.gifticonImage.croppedRect
        )
    }

    fun gotoCropGifticonName() {
        val gifticon = selectedGifticon.value ?: return
        gotoCropGifticon(
            crop = AddGifticonCrop.GIFTICON_NAME,
            croppedRect = gifticon.nameRectF
        )
    }

    fun gotoCropBrandName() {
        val gifticon = selectedGifticon.value ?: return
        gotoCropGifticon(
            crop = AddGifticonCrop.BRAND_NAME,
            croppedRect = gifticon.brandNameRectF
        )
    }

    fun gotoCropBarcode() {
        val gifticon = selectedGifticon.value ?: return
        gotoCropGifticon(
            crop = AddGifticonCrop.BARCODE,
            croppedRect = gifticon.barcodeRectF
        )
    }

    fun gotoCropBalance() {
        val gifticon = selectedGifticon.value ?: return
        gotoCropGifticon(
            crop = AddGifticonCrop.BALANCE,
            croppedRect = gifticon.balanceRectF
        )
    }

    fun gotoCropExpired() {
        val gifticon = selectedGifticon.value ?: return
        gotoCropGifticon(
            crop = AddGifticonCrop.EXPIRED,
            croppedRect = gifticon.expiredAtRectF
        )
    }

    private fun gotoCropGifticon(
        crop: AddGifticonCrop,
        croppedRect: RectF = RectF()
    ) {
        val originUri = selectedGifticon.value?.origin ?: return
        changeDeleteMode(false)

        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonTag.NONE))
            _eventFlow.emit(
                AddGifticonEvent.NavigateToCrop(crop, originUri, croppedRect)
            )
        }
    }

    fun showOriginGifticon() {
        val originUri = selectedGifticon.value?.origin ?: return
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonTag.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowOriginGifticon(originUri))
        }
    }

    fun showExpiredAtDatePicker() {
        val expiredAt = expiredAtDate ?: return
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestFocus(AddGifticonTag.NONE))
            _eventFlow.emit(AddGifticonEvent.ShowExpiredAtDatePicker(expiredAt))
        }
    }

    private fun requestLoading(loading: Boolean) {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestLoading(loading))
        }
    }

    private fun requestScroll(tag: AddGifticonTag) {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvent.RequestScroll(tag))
        }
    }

    companion object {
        private val EMPTY_DATE = Date(0)

        private val VALID_BARCODE_COUNT = setOf(12, 14, 16, 18, 20, 22, 24)
    }
}
