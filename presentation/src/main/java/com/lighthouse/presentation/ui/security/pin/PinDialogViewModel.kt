package com.lighthouse.presentation.ui.security.pin

import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.domain.usecase.SavePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PinDialogViewModel @Inject constructor(
    private val savePinUseCase: SavePinUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : PinViewModel() {
    override fun goPreviousStep() {
        TODO("Not yet implemented")
    }

    override fun goNextStep() {
        TODO("Not yet implemented")
    }
}
