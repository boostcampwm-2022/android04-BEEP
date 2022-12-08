package com.lighthouse.presentation.ui.security.pin

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.security.AuthCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class PinDialog(private val authCallback: AuthCallback) : BottomSheetDialogFragment(R.layout.fragment_pin) {

    private val binding: FragmentPinBinding by viewBindings()
    private val viewModel: PinDialogViewModel by viewModels()

    private val shakeAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_shake)
    }

    private val fadeUpAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fadein_up)
    }

    private lateinit var numberPadViews: List<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        numberPadViews = listOf(
            binding.tvNum0,
            binding.tvNum1,
            binding.tvNum2,
            binding.tvNum3,
            binding.tvNum4,
            binding.tvNum5,
            binding.tvNum6,
            binding.tvNum7,
            binding.tvNum8,
            binding.tvNum9,
            binding.ivBackspace
        )

        repeatOnStarted {
            viewModel.pushedNum.collect { num ->
                animateNumberPadBackground(num)
            }
        }

        initBottomSheetDialog(view)
        managePinMode()
    }

    private fun managePinMode() {
        repeatOnStarted {
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
                        authCallback.onAuthSuccess()
                        delay(1000L)
                        dismiss()
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

    private fun animateNumberPadBackground(num: Int) {
        if (num < 0) return

        ValueAnimator.ofArgb(requireContext().getColor(R.color.gray_200), requireContext().getColor(R.color.white))
            .apply {
                duration = 300
                addUpdateListener {
                    numberPadViews[num].setBackgroundColor(it.animatedValue as Int)
                }
            }.start()
    }
}
