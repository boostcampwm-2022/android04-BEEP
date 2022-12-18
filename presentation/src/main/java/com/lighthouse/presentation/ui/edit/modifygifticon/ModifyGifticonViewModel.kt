package com.lighthouse.presentation.ui.edit.modifygifticon

import android.graphics.RectF
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.edit.HasGifticonBrandUseCase
import com.lighthouse.domain.usecase.edit.modifygifticon.GetGifticonForUpdateUseCase
import com.lighthouse.domain.usecase.edit.modifygifticon.ModifyGifticonUseCase
import com.lighthouse.domain.usecase.edit.modifygifticon.ModifyRecognizeUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toDayOfMonth
import com.lighthouse.presentation.extension.toDigit
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.mapper.toDomain
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.ModifyGifticonUIModel
import com.lighthouse.presentation.ui.edit.modifygifticon.event.ModifyGifticonCrop
import com.lighthouse.presentation.ui.edit.modifygifticon.event.ModifyGifticonEvent
import com.lighthouse.presentation.ui.edit.modifygifticon.event.ModifyGifticonTag
import com.lighthouse.presentation.ui.edit.modifygifticon.event.ModifyGifticonValid
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.lighthouse.presentation.util.resource.AnimInfo
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ModifyGifticonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getGifticonForUpdateUseCase: GetGifticonForUpdateUseCase,
    private val hasGifticonBrandUseCase: HasGifticonBrandUseCase,
    private val modifyGifticonUseCase: ModifyGifticonUseCase,
    private val modifyRecognizeUseCase: ModifyRecognizeUseCase
) : ViewModel() {

    private val today = Calendar.getInstance().let {
        it.set(Calendar.HOUR, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.time
    }

    private val _eventFlow = MutableEventFlow<ModifyGifticonEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    private val gifticonId = savedStateHandle.get<String>(Extras.KEY_MODIFY_GIFTICON_ID) ?: ""
    private var originGifticon: ModifyGifticonUIModel? = null
    private var gifticon = MutableStateFlow<ModifyGifticonUIModel?>(null)

    private fun isNothingChanged(): Boolean {
        return false
    }

    private fun updateGifticon(
        update: (ModifyGifticonUIModel) -> ModifyGifticonUIModel
    ) {
        gifticon.value = gifticon.value?.let {
            update(it)
        }
    }

    val displayGifticonImage = gifticon.map {
        it?.croppedUri
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isCashCard = gifticon.map {
        it?.isCashCard ?: false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun updateCashCard(checked: Boolean) {
        updateGifticon { it.copy(isCashCard = checked) }
    }

    val displayName = MutableStateFlow("")

    private val name = gifticon.map {
        it?.name ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun updateGifticonName(name: CharSequence) {
        updateGifticon { it.copy(name = name.toString()) }
    }

    private val nameFocus = MutableStateFlow(false)

    fun onNameFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(ModifyGifticonTag.GIFTICON_NAME)
        }
        nameFocus.value = hasFocus
    }

    val nameRemoveVisible = name.combine(nameFocus) { name, focus ->
        name.isNotEmpty() && focus
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeName() {
        updateGifticon { it.copy(name = "") }
    }

    val displayBrand = MutableStateFlow("")

    private val brand = gifticon.map {
        it?.brandName ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val confirmedBrandMap = hashMapOf<String, Boolean>()

    private val approveBrandName = gifticon.map {
        it?.approveBrandName ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private var hasGifticonBrandJob: Job? = null

    val isLoadingConfirmBrand = MutableStateFlow(false)

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
                updateGifticon { it.copy(approveBrandName = if (approve) brand else "") }
            }
            hasGifticonBrandJob?.invokeOnCompletion {
                isLoadingConfirmBrand.value = false
            }
        }
    }

    fun updateBrandName(brandName: CharSequence) {
        val brand = brandName.toString()
        updateGifticon { it.copy(brandName = brand) }
        checkHasGifticonBrand(brand)
    }

    private val brandFocus = MutableStateFlow(false)

    fun onBrandFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(ModifyGifticonTag.BRAND_NAME)
        }
        brandFocus.value = hasFocus
    }

    val brandRemoveVisible = brand.map {
        it != ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeBrand() {
        updateBrandName("")
    }

    private val isApproveBrandNameVisible = brand.combine(isLoadingConfirmBrand) { brand, isLoading ->
        brand != "" && !isLoading
    }

    val isApproveBrandNameVisibility = isApproveBrandNameVisible.map { isVisible ->
        if (isVisible) View.VISIBLE else View.INVISIBLE
    }.stateIn(viewModelScope, SharingStarted.Eagerly, View.INVISIBLE)

    private val isApproveBrandName = brand.combine(approveBrandName) { brand, approveBrand ->
        brand == approveBrand
    }

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

    val isApproveBrandNameAnimation = isApproveBrandName.combine(isApproveBrandNameVisible) { isApprove, isVisible ->
        if (isApprove) {
            AnimInfo.AnimResource(R.anim.anim_fadein_up, isVisible)
        } else {
            AnimInfo.AnimResource(R.anim.anim_jump, isVisible)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AnimInfo.Empty)

    fun approveBrandName() {
        val approveBrandName = brand.value
        confirmedBrandMap[approveBrandName] = true
        updateGifticon { it.copy(approveBrandName = approveBrandName) }
    }

    val barcode = gifticon.map {
        it?.barcode ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun updateBarcode(barcode: String) {
        updateGifticon { it.copy(barcode = barcode) }
    }

    private val barcodeFocus = MutableStateFlow(false)

    fun onBarcodeFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(ModifyGifticonTag.BARCODE)
        }
        barcodeFocus.value = hasFocus
    }

    val barcodeRemoveVisible = barcode.combine(barcodeFocus) { barcode, focus ->
        barcode.isNotEmpty() && focus
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeBarcode() {
        updateBarcode("")
    }

    val balance = gifticon.map {
        it?.balance ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun updateBalance(balance: String) {
        updateGifticon { it.copy(balance = balance) }
    }

    private val balanceFocus = MutableStateFlow(false)

    fun onBalanceFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            requestScroll(ModifyGifticonTag.BALANCE)
        }
        balanceFocus.value = hasFocus
    }

    val balanceRemoveVisible = balance.combine(balanceFocus) { balance, focus ->
        balance != "0" && balance.isNotEmpty() && focus
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun removeBalance() {
        updateBalance("")
    }

    private val expiredAt = gifticon.map {
        it?.expiredAt ?: EMPTY_DATE
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EMPTY_DATE)

    private val expiredAtDate: Date?
        get() = expiredAt.value.let {
            if (it == EMPTY_DATE) today else it
        }

    val expiredAtUIText = expiredAt.map { date ->
        if (date != EMPTY_DATE) {
            UIText.StringResource(R.string.all_date, date.toYear(), date.toMonth(), date.toDayOfMonth())
        } else {
            UIText.Empty
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UIText.Empty)

    private val approveExpiredAt = gifticon.map {
        it?.approveExpiredAt ?: false
    }

    fun updateExpiredAt(expiredAt: Date) {
        updateGifticon { it.copy(expiredAt = expiredAt) }
    }

    private val isApproveExpired = expiredAt.combine(approveExpiredAt) { expiredAt, approve ->
        expiredAt >= today || approve
    }

    val isApproveExpiredAtDescriptionVisible = expiredAt.combine(approveExpiredAt) { expiredAt, approve ->
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

    val isApproveExpiredAtAnimation = isApproveExpired.combine(isApproveExpiredAtVisible) { isApprove, isVisible ->
        if (isApprove) {
            AnimInfo.AnimResource(R.anim.anim_fadein_up, isVisible)
        } else {
            AnimInfo.AnimResource(R.anim.anim_jump, isVisible)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AnimInfo.Empty)

    fun approveExpiredAt() {
        updateGifticon { it.copy(approveExpiredAt = true) }
    }

    val memo = gifticon.map {
        it?.memo ?: ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun updateMemo(memo: CharSequence) {
        updateGifticon { it.copy(memo = memo.toString()) }
    }

    init {
        viewModelScope.launch {
            val modifyGifticon = getGifticonForUpdateUseCase(gifticonId)?.toPresentation() ?: return@launch
            loadGifticon(modifyGifticon)
        }
    }

    private fun loadGifticon(modifyGifticon: ModifyGifticonUIModel) {
        originGifticon = modifyGifticon
        gifticon.value = modifyGifticon
        displayName.value = modifyGifticon.name
        displayBrand.value = modifyGifticon.brandName
        confirmedBrandMap[modifyGifticon.brandName] = true
    }

    fun onActionNextListener(actionId: Int): Boolean {
        val gifticon = gifticon.value ?: return false
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            val event = when (checkGifticonValid(gifticon)) {
                ModifyGifticonValid.INVALID_GIFTICON_NAME -> ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.GIFTICON_NAME)
                ModifyGifticonValid.INVALID_BRAND_NAME -> ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.BRAND_NAME)
                ModifyGifticonValid.INVALID_BARCODE -> ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.BARCODE)
                ModifyGifticonValid.INVALID_BALANCE -> ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.BALANCE)
                ModifyGifticonValid.INVALID_EXPIRED_AT -> ModifyGifticonEvent.ShowExpiredAtDatePicker(
                    expiredAtDate ?: today
                )
                else -> {
                    requestModifyGifticon()
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

    fun recognizeGifticonName(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = modifyRecognizeUseCase.gifticonName(uri.toString())
            if (result != "") {
                updateGifticon { it.copy(name = result, nameRectF = croppedImage.croppedRect) }
                displayName.value = result
            } else {
                updateGifticon { it.copy(nameRectF = croppedImage.croppedRect) }
                _eventFlow.emit(ModifyGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_name)))
            }
        }
    }

    fun recognizeBrand(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = modifyRecognizeUseCase.brandName(uri.toString())
            if (result != "") {
                updateGifticon { it.copy(brandName = result, brandNameRectF = croppedImage.croppedRect) }
                displayBrand.value = result
            } else {
                updateGifticon { it.copy(brandNameRectF = croppedImage.croppedRect) }
                _eventFlow.emit(ModifyGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_brand)))
            }
        }
    }

    fun recognizeBarcode(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = modifyRecognizeUseCase.barcode(uri.toString())
            if (result != "") {
                updateGifticon { it.copy(barcode = result, barcodeRectF = croppedImage.croppedRect) }
            } else {
                updateGifticon { it.copy(barcodeRectF = croppedImage.croppedRect) }
                _eventFlow.emit(ModifyGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_barcode)))
            }
        }
    }

    fun recognizeBalance(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = modifyRecognizeUseCase.balance(uri.toString())
            if (result > 0) {
                updateGifticon {
                    it.copy(
                        isCashCard = true,
                        balance = result.toString(),
                        balanceRectF = croppedImage.croppedRect
                    )
                }
            } else {
                updateGifticon { it.copy(balanceRectF = croppedImage.croppedRect) }
                _eventFlow.emit(ModifyGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_balance)))
            }
        }
    }

    fun recognizeGifticonExpired(croppedImage: CroppedImage?) {
        croppedImage ?: return
        val uri = croppedImage.uri ?: return
        viewModelScope.launch {
            val result = modifyRecognizeUseCase.expired(uri.toString())
            if (result != EMPTY_DATE) {
                updateGifticon { it.copy(expiredAt = result, expiredAtRectF = croppedImage.croppedRect) }
            } else {
                updateGifticon { it.copy(expiredAtRectF = croppedImage.croppedRect) }
                _eventFlow.emit(ModifyGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.edit_gifticon_failed_recognize_expired_at)))
            }
        }
    }

    private fun checkGifticonValid(gifticon: ModifyGifticonUIModel): ModifyGifticonValid {
        return when {
            isNothingChanged() -> ModifyGifticonValid.INVALID_NOTHING_CHANGED
            gifticon.name.isEmpty() -> ModifyGifticonValid.INVALID_GIFTICON_NAME
            gifticon.brandName.isEmpty() -> ModifyGifticonValid.INVALID_BRAND_NAME
            gifticon.brandName != gifticon.approveBrandName -> ModifyGifticonValid.INVALID_APPROVE_BRAND_NAME
            gifticon.barcode.length !in VALID_BARCODE_COUNT -> ModifyGifticonValid.INVALID_BARCODE
            gifticon.isCashCard && gifticon.balance.toDigit() == 0 -> ModifyGifticonValid.INVALID_BALANCE
            gifticon.expiredAt == EMPTY_DATE -> ModifyGifticonValid.INVALID_EXPIRED_AT
            gifticon.expiredAt < today && gifticon.approveExpiredAt.not() -> ModifyGifticonValid.INVALID_APPROVE_EXPIRED_AT
            else -> ModifyGifticonValid.VALID
        }
    }

    private fun handleGifticonInvalid(valid: ModifyGifticonValid) {
        viewModelScope.launch {
            when (valid) {
                ModifyGifticonValid.VALID -> {}
                else -> {
                    _eventFlow.emit(ModifyGifticonEvent.ShowSnackBar(valid.text))
                    _eventFlow.emit(ModifyGifticonEvent.RequestFocus(valid.tag))
                    _eventFlow.emit(ModifyGifticonEvent.RequestScroll(valid.tag))
                }
            }
        }
    }

    fun requestCashCard() {
        val gifticon = gifticon.value ?: return
        viewModelScope.launch {
            val event = if (gifticon.isCashCard) {
                ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.BALANCE)
            } else {
                ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.NONE)
            }
            _eventFlow.emit(event)
        }
    }

    fun requestPopBackstack() {
        if (isNothingChanged()) {
            popBackstack()
        } else {
            showCancelConfirmation()
        }
    }

    private fun popBackstack() {
        viewModelScope.launch {
            _eventFlow.emit(ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.NONE))
            _eventFlow.emit(ModifyGifticonEvent.PopupBackStack)
        }
    }

    private fun showCancelConfirmation() {
        viewModelScope.launch {
            _eventFlow.emit(ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.NONE))
            _eventFlow.emit(ModifyGifticonEvent.ShowCancelConfirmation)
        }
    }

    private fun gotoCropGifticon(
        crop: ModifyGifticonCrop,
        croppedRect: RectF
    ) {
        val originFileName = gifticon.value?.originFileName ?: return
        viewModelScope.launch {
            _eventFlow.emit(ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.NONE))
            _eventFlow.emit(
                ModifyGifticonEvent.NavigateToCrop(crop, originFileName, croppedRect)
            )
        }
    }

    fun gotoCropGifticonImage() {
        val gifticon = gifticon.value ?: return
        gotoCropGifticon(
            crop = ModifyGifticonCrop.GIFTICON_IMAGE,
            croppedRect = gifticon.croppedRect
        )
    }

    fun gotoCropGifticonName() {
        val gifticon = gifticon.value ?: return
        gotoCropGifticon(
            crop = ModifyGifticonCrop.GIFTICON_NAME,
            croppedRect = gifticon.nameRectF
        )
    }

    fun gotoCropBrandName() {
        val gifticon = gifticon.value ?: return
        gotoCropGifticon(
            crop = ModifyGifticonCrop.BRAND_NAME,
            croppedRect = gifticon.brandNameRectF
        )
    }

    fun gotoCropBarcode() {
        val gifticon = gifticon.value ?: return
        gotoCropGifticon(
            crop = ModifyGifticonCrop.BARCODE,
            croppedRect = gifticon.barcodeRectF
        )
    }

    fun gotoCropBalance() {
        val gifticon = gifticon.value ?: return
        gotoCropGifticon(
            crop = ModifyGifticonCrop.BALANCE,
            croppedRect = gifticon.balanceRectF
        )
    }

    fun gotoCropExpired() {
        val gifticon = gifticon.value ?: return
        gotoCropGifticon(
            crop = ModifyGifticonCrop.EXPIRED,
            croppedRect = gifticon.expiredAtRectF
        )
    }

    fun showOriginGifticon() {
        val originFileName = gifticon.value?.originFileName ?: return
        viewModelScope.launch {
            _eventFlow.emit(ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.NONE))
            _eventFlow.emit(ModifyGifticonEvent.ShowOriginGifticon(originFileName))
        }
    }

    fun showExpiredAtDatePicker() {
        val expiredAt = expiredAtDate ?: return
        viewModelScope.launch {
            _eventFlow.emit(ModifyGifticonEvent.RequestFocus(ModifyGifticonTag.NONE))
            _eventFlow.emit(ModifyGifticonEvent.ShowExpiredAtDatePicker(expiredAt))
        }
    }

    private fun requestLoading(loading: Boolean) {
        viewModelScope.launch {
            _eventFlow.emit(ModifyGifticonEvent.RequestLoading(loading))
        }
    }

    private fun requestScroll(tag: ModifyGifticonTag) {
        viewModelScope.launch {
            _eventFlow.emit(ModifyGifticonEvent.RequestScroll(tag))
        }
    }

    fun requestModifyGifticon() {
        val gifticon = gifticon.value ?: return
        viewModelScope.launch {
            val valid: ModifyGifticonValid = checkGifticonValid(gifticon)
            if (valid == ModifyGifticonValid.VALID) {
                modifyGifticonUseCase(gifticon.toDomain())
                _eventFlow.emit(ModifyGifticonEvent.ModifyCompleted)
            } else {
                handleGifticonInvalid(valid)
            }
        }
    }

    companion object {
        private val EMPTY_DATE = Date(0)

        private val VALID_BARCODE_COUNT = setOf(12, 14, 16, 18, 20, 22, 24)
    }
}
