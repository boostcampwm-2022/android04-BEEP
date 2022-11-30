package com.lighthouse.presentation.ui.security.pin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.security.SecurityViewModel
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import com.lighthouse.presentation.ui.setting.SecurityOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        lifecycleScope.launch {
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
                        Snackbar.make(view, getString(R.string.pin_complete), Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .show()
                        binding.ivCheck.apply {
                            visibility = View.VISIBLE
                            startAnimation(fadeUpAnimation)
                        }
                        delay(1000L)
                        Timber.tag("REVISE").d("securityViewModel: ${activityViewModel.isRevise}")
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
                        Snackbar.make(view, getString(R.string.pin_internal_error), Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .show()
                        delay(500L)
                        activityViewModel.gotoOtherScreen(SecurityDirections.MAIN)
                    }
                }
            }
        }

        binding.tvSecureNotUse.setOnClickListener {
            activityViewModel.setSecurityOption(SecurityOption.NONE)
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
}
