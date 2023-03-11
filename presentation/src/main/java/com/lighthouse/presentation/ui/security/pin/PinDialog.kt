package com.lighthouse.presentation.ui.security.pin

import android.animation.ValueAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewLifecycleOwner.repeatOnStarted {
            viewModel.pushedNum.collect { num ->
                animateNumberPadBackground(num)
            }
        }

        initBottomSheetDialog(view)
        managePinMode()
    }

    private fun managePinMode() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.pinMode.collect { mode ->
                when (mode) {
                    PinSettingType.CONFIRM -> binding.tvPinDescription.text = getString(R.string.pin_input_description)
                    PinSettingType.WRONG -> binding.tvPinDescription.text = getString(R.string.pin_wrong_description)
                    PinSettingType.COMPLETE -> {
                        delay(500L)
                        authCallback.onAuthSuccess()
                        dismiss()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initBottomSheetDialog(view: View) {
        BottomSheetBehavior.from(view.parent as View).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
        binding.clPin.minHeight = (screenHeight * 0.9).toInt()
    }

    private fun animateNumberPadBackground(num: Int) {
        if (num < 0) return

        val view = when (num) {
            0 -> binding.tvNum0
            1 -> binding.tvNum1
            2 -> binding.tvNum2
            3 -> binding.tvNum3
            4 -> binding.tvNum4
            5 -> binding.tvNum5
            6 -> binding.tvNum6
            7 -> binding.tvNum7
            8 -> binding.tvNum8
            9 -> binding.tvNum9
            else -> binding.ivBackspace
        }

        val startColor =
            when (requireContext().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> requireContext().getColor(R.color.gray_500)
                else -> requireContext().getColor(R.color.gray_200)
            }

        val endColor = TypedValue().let {
            requireContext().theme.resolveAttribute(android.R.attr.background, it, true)
            it.data
        }

        ValueAnimator.ofArgb(startColor, endColor)
            .apply {
                duration = 300
                addUpdateListener {
                    view.setBackgroundColor(it.animatedValue as Int)
                }
            }.start()
    }
}
