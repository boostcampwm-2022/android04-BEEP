package com.lighthouse.presentation.ui.security.pin

import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
        binding.lifecycleOwner = this
        binding.tvSecureNotUse.visibility = if (activityViewModel.isRevise) View.INVISIBLE else View.VISIBLE

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

        managePinMode()

        repeatOnStarted {
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
                        playWrongPinAnimation()
                    }
                    PinSettingType.COMPLETE -> {
                        Snackbar.make(requireView(), getString(R.string.pin_complete), Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .show()
                        binding.ivCheck.apply {
                            visibility = View.VISIBLE
                            startAnimation(fadeUpAnimation)
                        }
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
                    numberPadViews[num].setBackgroundColor(it.animatedValue as Int)
                }
            }.start()
    }
}
