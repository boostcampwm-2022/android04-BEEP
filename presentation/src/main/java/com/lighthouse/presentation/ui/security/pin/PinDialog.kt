package com.lighthouse.presentation.ui.security.pin

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.security.AuthCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PinDialog(private val authCallback: AuthCallback) : BottomSheetDialogFragment(R.layout.fragment_pin) {

    private val binding by viewBindings(FragmentPinBinding::bind)
    private val viewModel: PinDialogViewModel by viewModels()

    private val shakeAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_shake)
    }

    private val fadeUpAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fadein_up)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.btnPinPrev.visibility = View.INVISIBLE

        initBottomSheetDialog(view)
        lifecycleScope.launch {
            viewModel.pinMode.collect { mode ->
                when (mode) {
                    PinSettingType.CONFIRM -> binding.tvPinDescription.text = getString(R.string.pin_input_description)
                    PinSettingType.WRONG -> {
                        binding.tvPinDescription.text = getString(R.string.pin_wrong_description)
                        playWrongPinAnimation()
                    }
                    PinSettingType.COMPLETE -> {
                        binding.ivCheck.apply {
                            visibility = View.VISIBLE
                            startAnimation(fadeUpAnimation)
                        }
                        delay(1000L)
                        dismiss()
                        authCallback.onAuthSuccess()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initBottomSheetDialog(view: View) {
        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.clPin.minHeight = (screenHeight * 0.9).toInt()
    }

    private fun playWrongPinAnimation() {
        binding.ivPin0.startAnimation(shakeAnimation)
        binding.ivPin1.startAnimation(shakeAnimation)
        binding.ivPin2.startAnimation(shakeAnimation)
        binding.ivPin3.startAnimation(shakeAnimation)
        binding.ivPin4.startAnimation(shakeAnimation)
        binding.ivPin5.startAnimation(shakeAnimation)
    }
}
