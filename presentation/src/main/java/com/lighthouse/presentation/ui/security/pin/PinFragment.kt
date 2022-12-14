package com.lighthouse.presentation.ui.security.pin

import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.security.SecurityViewModel
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import com.lighthouse.presentation.ui.setting.SecurityOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class PinFragment : Fragment(R.layout.fragment_pin) {

    private val binding: FragmentPinBinding by viewBindings()
    private val viewModel: PinSettingViewModel by viewModels()
    private val activityViewModel: SecurityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.tvSecureNotUse.visibility = if (activityViewModel.isRevise) View.INVISIBLE else View.VISIBLE

        managePinMode()

        viewLifecycleOwner.repeatOnStarted {
            viewModel.pushedNum.collect { num ->
                animateNumberPadBackground(num)
            }
        }

        binding.tvSecureNotUse.setOnClickListener {
            activityViewModel.setSecurityOption(SecurityOption.NONE)
            activityViewModel.gotoOtherScreen(SecurityDirections.LOCATION)
        }
    }

    private fun managePinMode() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.pinMode.collect {
                when (it) {
                    PinSettingType.INITIAL -> {
                        binding.tvPinDescription.text = getString(R.string.pin_initial_description)
                        binding.btnPinPrev.visibility = View.GONE
                    }
                    PinSettingType.CONFIRM -> {
                        binding.tvPinDescription.text = getString(R.string.pin_confirm_description)
                        binding.btnPinPrev.visibility = View.VISIBLE
                    }
                    PinSettingType.WRONG -> {
                        binding.tvPinDescription.text = getString(R.string.pin_wrong_description)
                    }
                    PinSettingType.COMPLETE -> {
                        Snackbar.make(requireView(), getString(R.string.pin_complete), Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .show()
                        delay(1000L)
                        if (activityViewModel.isRevise) {
                            requireActivity().apply {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        } else {
                            activityViewModel.setSecurityOption(SecurityOption.PIN)
                            activityViewModel.gotoOtherScreen(SecurityDirections.FINGERPRINT)
                        }
                    }
                    PinSettingType.ERROR -> {
                        Snackbar.make(requireView(), getString(R.string.pin_internal_error), Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .show()
                        delay(500L)
                        activityViewModel.gotoOtherScreen(SecurityDirections.LOCATION)
                    }
                    PinSettingType.WAIT -> {}
                }
            }
        }
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
